package com.appxy.pocketexpensepro.setting.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.accounts.AccountDao;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.entity.MEntity;
import com.appxy.pocketexpensepro.overview.transaction.AccountsListViewAdapter;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;
import com.appxy.pocketexpensepro.overview.transaction.SplitCategoryActivity;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.appxy.pocketexpensepro.setting.payee.DialogExpandableListViewAdapter;
import com.appxy.pocketexpensepro.setting.payee.PayeeDao;
import com.dropbox.sync.android.DbxRecord;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfContentByte.GraphicState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.parser.GraphicsState;
import com.itextpdf.text.xml.simpleparser.NewLineHandler;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Switch;

public class ExportTransactionPDFActivity extends BaseHomeActivity {

	private Spinner groupSpinner;
	private Button categoryButton;
	private Button accountButton;
	private Button startButton;
	private Button endButton;
	private Spinner sequenceSpinner;

	private int sYear;
	private int sMonth;
	private int sDay;

	private int eYear;
	private int eMonth;
	private int eDay;
	private long startDate;
	private long endDate;

	private LayoutInflater inflater;

	private RelativeLayout expenseButton; // 初始化Category相关
	private RelativeLayout incomeButton;
	private View chooseView1;
	private View chooseView2;
	private int mCategoryType = 0; // 0 expense 1 income
	private MultipleExpandableListViewAdapter mDialogExpandableListViewAdapter;
	private List<Map<String, Object>> groupDataList;
	private List<List<Map<String, Object>>> childrenAllDataList;
	private ExpandableListView mExpandableListView;
	private int checkedItem;
	private int gCheckedItem;// 选择位置
	private int cCheckedItem;
	private AlertDialog mCategoryDialog;

	private List<Map<String, Object>> groupExpenseDataList;
	private List<List<Map<String, Object>>> childrenExpenseDataList;
	private List<Map<String, Object>> groupIncomeDataList;
	private List<List<Map<String, Object>>> childrenIncomeDataList;

	private HashSet<Integer> selectExpenseIdSet; // 只保存选中的categoryID，remove未选中的category
	private HashSet<Integer> selectIncomeIdSet;
	private List<List<Map<Integer, Boolean>>> selectedtExpenseList; // 保存所有category的选中状态
	private List<List<Map<Integer, Boolean>>> selectedtIncomeList;
	private List<Map<String, Object>> mExpenseDataList; // 所有Expense的category数据集
	private List<Map<String, Object>> mIncomeDataList;
	private Boolean isSelectAllExpenseBoolean = true;
	private Boolean isSelectAllIncomeBoolean = true;

	private ListView accountListView;
	private AlertDialog mAccountDialog;
	private int accountCheckItem = 0;
	private MultipleListAdapter accountListViewAdapter;
	private int groupType = 0; // 0 category, 1 account
	private int seqType = 0; // 0 ASC, 1 Desc

	private Map<Integer, Boolean> selectedMap; // 初始化account相关
	private HashSet<Integer> selectIdSet;
	private List<Map<String, Object>> mAccountList;
	private Boolean isSelectAllBoolean = true;
	private String nowCreat;

	private boolean isTransfer = true;
	private Switch transferSwitch;
	private static final String PDFPATH = "/sdcard/PocketExpense/PocketExpenseReport_Transaction.pdf";
	private Document doc;
	private Dialog dialog_loading;
	private List<Integer> groupSortList; // 父类分类
	private String currencySrtring = Common.CURRENCY_SIGN[Common.CURRENCY];
	private List<Integer> groupSortListForTran;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export_trans_pdf);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		inflater = LayoutInflater.from(ExportTransactionPDFActivity.this);


		groupDataList = new ArrayList<Map<String, Object>>();
		childrenAllDataList = new ArrayList<List<Map<String, Object>>>();

		groupExpenseDataList = new ArrayList<Map<String, Object>>();
		childrenExpenseDataList = new ArrayList<List<Map<String, Object>>>();
		groupIncomeDataList = new ArrayList<Map<String, Object>>();
		childrenIncomeDataList = new ArrayList<List<Map<String, Object>>>();
		selectedtExpenseList = new ArrayList<List<Map<Integer, Boolean>>>();
		selectedtIncomeList = new ArrayList<List<Map<Integer, Boolean>>>();
		mDialogExpandableListViewAdapter = new MultipleExpandableListViewAdapter(
				this);
		selectExpenseIdSet = new HashSet<Integer>();
		selectIncomeIdSet = new HashSet<Integer>();
		groupSortList = new ArrayList<Integer>();
		groupSortListForTran = new ArrayList<Integer>();
		
		try {
			mExpenseDataList = PayeeDao.selectCategory(
					ExportTransactionPDFActivity.this, 0);// 初始化Category
			filterData(mExpenseDataList, 0);

			mIncomeDataList = PayeeDao.selectCategory(
					ExportTransactionPDFActivity.this, 1);// 初始化Category
			filterData(mIncomeDataList, 1);
		} catch (Exception e) {
			// TODO: handle exception
			finish();
		}

		selectedMap = new HashMap<Integer, Boolean>(); // 初始化account
		selectIdSet = new HashSet<Integer>();
		mAccountList = AccountDao
				.selectAccount(ExportTransactionPDFActivity.this);
		for (int i = 0; i < mAccountList.size(); i++) {
			selectedMap.put(i, true);
			selectIdSet.add((Integer) mAccountList.get(i).get("_id"));
		}

		groupSpinner = (Spinner) findViewById(R.id.group_spin);
		categoryButton = (Button) findViewById(R.id.category_btn);
		accountButton = (Button) findViewById(R.id.account_btn);
		startButton = (Button) findViewById(R.id.start_btn);
		endButton = (Button) findViewById(R.id.end_btn);
		sequenceSpinner = (Spinner) findViewById(R.id.sequence_spin);
		transferSwitch = (Switch) findViewById(R.id.transfer_switch);
		transferSwitch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						isTransfer = isChecked;
					}
				});

		categoryButton.setText("All");
		if (selectIdSet.size() > 0) {
			accountButton.setText("All");
		} else {
			accountButton.setText("No Account Selected");
		}

		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(ExportTransactionPDFActivity.this,
						R.array.group_by,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groupSpinner.setAdapter(adapterSpinner);
		groupSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					groupType = 0;
				} else if (arg2 == 1) {
					groupType = 1;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		ArrayAdapter<CharSequence> adapterSpinner1 = ArrayAdapter
				.createFromResource(ExportTransactionPDFActivity.this,
						R.array.sequence,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sequenceSpinner.setAdapter(adapterSpinner1);
		sequenceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					seqType = 0;
				} else if (arg2 == 1) {
					seqType = 1;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		categoryButton.setOnClickListener(mClickListener);
		accountButton.setOnClickListener(mClickListener);
		startButton.setOnClickListener(mClickListener);
		endButton.setOnClickListener(mClickListener);

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DAY_OF_MONTH, -10);
		sYear = c1.get(Calendar.YEAR);
		sMonth = c1.get(Calendar.MONTH);
		sDay = c1.get(Calendar.DAY_OF_MONTH);

		Calendar c = Calendar.getInstance();
		eYear = c.get(Calendar.YEAR);
		eMonth = c.get(Calendar.MONTH);
		eDay = c.get(Calendar.DAY_OF_MONTH);

		updateDisplayStart();
		updateDisplayEnd();
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.category_btn:

				View view1 = inflater.inflate(
						R.layout.dialog_mul_selcect_category, null);
				LinearLayout selectAlCategorylLayout = (LinearLayout) view1
						.findViewById(R.id.selectall_layout);
				LinearLayout doneCategoryLayout = (LinearLayout) view1
						.findViewById(R.id.done_layout);
				final RadioButton selectAllCategoryRadio = (RadioButton) view1
						.findViewById(R.id.selectall_radio);

				expenseButton = (RelativeLayout) view1
						.findViewById(R.id.expense_btn);
				incomeButton = (RelativeLayout) view1
						.findViewById(R.id.income_btn);
				chooseView1 = (View) view1.findViewById(R.id.view1);
				chooseView2 = (View) view1.findViewById(R.id.view2);

				selectAlCategorylLayout
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View paramView) {
								// TODO Auto-generated method stub
								if (mCategoryType == 0) {

									isSelectAllExpenseBoolean = (isSelectAllExpenseBoolean == true) ? false
											: true;
									if (isSelectAllExpenseBoolean) {
										setSelectDate(mExpenseDataList, 0);
										selectAllCategoryRadio.setChecked(true);
									} else {
										UnSelectDate(mExpenseDataList, 0);
										selectAllCategoryRadio
												.setChecked(false);
									}
									mDialogExpandableListViewAdapter
											.setSelectedList(selectedtExpenseList);
									mDialogExpandableListViewAdapter
											.notifyDataSetChanged();

								} else {

									isSelectAllIncomeBoolean = (isSelectAllIncomeBoolean == true) ? false
											: true;
									if (isSelectAllIncomeBoolean) {
										setSelectDate(mIncomeDataList, 1);
										selectAllCategoryRadio.setChecked(true);
									} else {
										UnSelectDate(mIncomeDataList, 1);
										selectAllCategoryRadio
												.setChecked(false);
									}
									mDialogExpandableListViewAdapter
											.setSelectedList(selectedtIncomeList);
									mDialogExpandableListViewAdapter
											.notifyDataSetChanged();

								}
							}
						});

				doneCategoryLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						mCategoryDialog.dismiss();

						if ((selectIncomeIdSet.size() + selectExpenseIdSet
								.size()) == (mExpenseDataList.size() + mIncomeDataList
								.size())) {
							categoryButton.setText("All");
						} else if ((selectIncomeIdSet.size() + selectExpenseIdSet
								.size()) > 0
								&& (selectIncomeIdSet.size() + selectExpenseIdSet
										.size()) < (mExpenseDataList.size() + mIncomeDataList
										.size())) {
							categoryButton.setText("Mutiple Categories");
						} else {
							categoryButton.setText("No Category Select");
						}

					}
				});

				if (mCategoryType == 0) {
					chooseView1.setVisibility(View.VISIBLE);
					chooseView2.setVisibility(View.INVISIBLE);
				} else {
					chooseView1.setVisibility(View.INVISIBLE);
					chooseView2.setVisibility(View.VISIBLE);
				}

				expenseButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						chooseView1.setVisibility(View.VISIBLE);
						chooseView2.setVisibility(View.INVISIBLE);
						mCategoryType = 0;
						mDialogExpandableListViewAdapter.setAdapterData(
								groupExpenseDataList, childrenExpenseDataList);
						int groupCount = groupExpenseDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);

						mDialogExpandableListViewAdapter
								.setSelectedList(selectedtExpenseList);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();

						if (selectExpenseIdSet.size() == mExpenseDataList
								.size()) {
							selectAllCategoryRadio.setChecked(true);
						} else {
							selectAllCategoryRadio.setChecked(false);
						}

					}
				});

				incomeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						chooseView1.setVisibility(View.INVISIBLE);
						chooseView2.setVisibility(View.VISIBLE);
						mCategoryType = 1;
						mDialogExpandableListViewAdapter.setAdapterData(
								groupIncomeDataList, childrenIncomeDataList);
						int groupCount = groupIncomeDataList.size();

						for (int i = 0; i < groupCount; i++) {
							mExpandableListView.expandGroup(i);
						}
						mExpandableListView.setCacheColorHint(0);

						mDialogExpandableListViewAdapter
								.setSelectedList(selectedtIncomeList);
						mDialogExpandableListViewAdapter.notifyDataSetChanged();

						if (selectIncomeIdSet.size() == mIncomeDataList.size()) {
							selectAllCategoryRadio.setChecked(true);
						} else {
							selectAllCategoryRadio.setChecked(false);
						}

					}
				});

				mExpandableListView = (ExpandableListView) view1
						.findViewById(R.id.mExpandableListView);
				mExpandableListView
						.setAdapter(mDialogExpandableListViewAdapter);
				mExpandableListView
						.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
				mExpandableListView.setItemsCanFocus(false);
				mExpandableListView.setGroupIndicator(null);

				if (mCategoryType == 0) {

					mDialogExpandableListViewAdapter.setAdapterData(
							groupExpenseDataList, childrenExpenseDataList);
					int groupCount = groupExpenseDataList.size();

					for (int i = 0; i < groupCount; i++) {
						mExpandableListView.expandGroup(i);
					}
					mExpandableListView.setCacheColorHint(0);
					mExpandableListView.setSelectedChild(gCheckedItem,
							cCheckedItem, true);
					mDialogExpandableListViewAdapter
							.setSelectedList(selectedtExpenseList);
					mDialogExpandableListViewAdapter.notifyDataSetChanged();

				} else if (mCategoryType == 1) {

					mDialogExpandableListViewAdapter.setAdapterData(
							groupIncomeDataList, childrenIncomeDataList);
					int groupCount = groupIncomeDataList.size();

					for (int i = 0; i < groupCount; i++) {
						mExpandableListView.expandGroup(i);
					}
					mExpandableListView.setCacheColorHint(0);
					mExpandableListView.setSelectedChild(gCheckedItem,
							cCheckedItem, true);
					mDialogExpandableListViewAdapter
							.setSelectedList(selectedtIncomeList);
					mDialogExpandableListViewAdapter.notifyDataSetChanged();

				}

				mExpandableListView
						.setOnChildClickListener(new OnChildClickListener() {

							@Override
							public boolean onChildClick(
									ExpandableListView parent, View v,
									int groupPosition, int childPosition,
									long id) {
								// TODO Auto-generated method stub

								MultipleExpandableListViewAdapter.cViewHolder cView = (MultipleExpandableListViewAdapter.cViewHolder) v
										.getTag();

								checkedItem = mExpandableListView
										.getFlatListPosition(ExpandableListView
												.getPackedPositionForChild(
														groupPosition,
														childPosition));

								gCheckedItem = groupPosition;
								cCheckedItem = childPosition;

								if (cView.mTextView.isChecked()) {
									cView.mTextView.setChecked(false);

									if (mCategoryType == 0) {
										selectExpenseIdSet
												.remove((Integer) childrenExpenseDataList
														.get(groupPosition)
														.get(childPosition)
														.get("_id"));
									} else {
										selectIncomeIdSet
												.remove((Integer) childrenIncomeDataList
														.get(groupPosition)
														.get(childPosition)
														.get("_id"));
									}

								} else {
									cView.mTextView.setChecked(true);
									if (mCategoryType == 0) {
										selectExpenseIdSet
												.add((Integer) childrenExpenseDataList
														.get(groupPosition)
														.get(childPosition)
														.get("_id"));
									} else {
										selectIncomeIdSet
												.add((Integer) childrenIncomeDataList
														.get(groupPosition)
														.get(childPosition)
														.get("_id"));
									}
								}

								if (mCategoryType == 0) {
									selectedtExpenseList
											.get(groupPosition)
											.get(0)
											.put(childPosition,
													cView.mTextView.isChecked());
								} else {
									selectedtIncomeList
											.get(groupPosition)
											.get(0)
											.put(childPosition,
													cView.mTextView.isChecked());
								}

								if (mCategoryType == 0) {

									if (selectExpenseIdSet.size() == mExpenseDataList
											.size()) {
										selectAllCategoryRadio.setChecked(true);

										isSelectAllExpenseBoolean = true;
									} else {
										selectAllCategoryRadio
												.setChecked(false);
										isSelectAllExpenseBoolean = false;
										isSelectAllIncomeBoolean = false;
									}

								} else {

									if (selectIncomeIdSet.size() == mIncomeDataList
											.size()) {
										selectAllCategoryRadio.setChecked(true);
										isSelectAllIncomeBoolean = true;
									} else {
										selectAllCategoryRadio
												.setChecked(false);
										isSelectAllIncomeBoolean = false;
									}

								}

								return true;
							}
						});

				mExpandableListView
						.setOnGroupClickListener(new OnGroupClickListener() {

							@Override
							public boolean onGroupClick(
									ExpandableListView parent, View v,
									int groupPosition, long id) {
								// TODO Auto-generated method stub

								MultipleExpandableListViewAdapter.gViewHolder cView = (MultipleExpandableListViewAdapter.gViewHolder) v
										.getTag();

								checkedItem = mExpandableListView
										.getFlatListPosition(ExpandableListView
												.getPackedPositionForChild(
														groupPosition, 0));

								gCheckedItem = groupPosition;
								cCheckedItem = -1;

								if (cView.mTextView.isChecked()) {
									cView.mTextView.setChecked(false);

									if (mCategoryType == 0) {
										selectExpenseIdSet
												.remove((Integer) groupExpenseDataList
														.get(groupPosition)
														.get("_id"));
									} else {
										selectIncomeIdSet
												.remove((Integer) groupIncomeDataList
														.get(groupPosition)
														.get("_id"));
									}

								} else {
									cView.mTextView.setChecked(true);
									if (mCategoryType == 0) {
										selectExpenseIdSet
												.add((Integer) groupExpenseDataList
														.get(groupPosition)
														.get("_id"));
									} else {
										selectIncomeIdSet
												.add((Integer) groupIncomeDataList
														.get(groupPosition)
														.get("_id"));
									}
								}

								if (mCategoryType == 0) {
									selectedtExpenseList
											.get(groupPosition)
											.get(0)
											.put(-1,
													cView.mTextView.isChecked());
								} else {
									selectedtIncomeList
											.get(groupPosition)
											.get(0)
											.put(-1,
													cView.mTextView.isChecked());
								}

								if (mCategoryType == 0) {

									if (selectExpenseIdSet.size() == mExpenseDataList
											.size()) {
										selectAllCategoryRadio.setChecked(true);
										isSelectAllExpenseBoolean = true;
									} else {
										selectAllCategoryRadio
												.setChecked(false);
										isSelectAllExpenseBoolean = false;
										isSelectAllIncomeBoolean = false;
									}

								} else {

									if (selectIncomeIdSet.size() == mIncomeDataList
											.size()) {
										selectAllCategoryRadio.setChecked(true);
										isSelectAllIncomeBoolean = true;
									} else {
										selectAllCategoryRadio
												.setChecked(false);
										isSelectAllIncomeBoolean = false;
									}

								}

								return true;
							}
						});

				AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(
						ExportTransactionPDFActivity.this);
				mBuilder1.setTitle("Select Categories");
				mBuilder1.setView(view1);
				mBuilder1
						.setOnCancelListener(new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface arg0) {
								mCategoryDialog.dismiss();

								if ((selectIncomeIdSet.size() + selectExpenseIdSet
										.size()) == (mExpenseDataList.size() + mIncomeDataList
										.size())) {
									categoryButton.setText("All");
								} else if ((selectIncomeIdSet.size() + selectExpenseIdSet
										.size()) > 0
										&& (selectIncomeIdSet.size() + selectExpenseIdSet
												.size()) < (mExpenseDataList
												.size() + mIncomeDataList
												.size())) {
									categoryButton
											.setText("Mutiple Categories");
								} else {
									categoryButton
											.setText("No Category Select");
								}
							}
						});

				mCategoryDialog = mBuilder1.create();
				mCategoryDialog.show();

				break;
			case R.id.account_btn:

				View view2 = inflater.inflate(
						R.layout.dialog_mul_select_account, null);
				LinearLayout selectAllLayout = (LinearLayout) view2
						.findViewById(R.id.selectall_layout);
				LinearLayout doneLayout = (LinearLayout) view2
						.findViewById(R.id.done_layout);
				final RadioButton selectAllRadio = (RadioButton) view2
						.findViewById(R.id.selectall_radio);

				if (isSelectAllBoolean) {
					selectAllRadio.setChecked(true);
				} else {
					selectAllRadio.setChecked(false);
				}

				selectAllLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						isSelectAllBoolean = (isSelectAllBoolean == true) ? false
								: true;

						if (isSelectAllBoolean) {

							for (int i = 0; i < mAccountList.size(); i++) {
								selectedMap.put(i, true);
								selectIdSet.add((Integer) mAccountList.get(i)
										.get("_id"));
							}

							accountListViewAdapter
									.setItemCheckedMap(selectedMap);
							accountListViewAdapter.notifyDataSetChanged();
							selectAllRadio.setChecked(true);

						} else {

							for (int i = 0; i < mAccountList.size(); i++) {
								selectedMap.put(i, false);
								selectIdSet.clear();
							}

							accountListViewAdapter
									.setItemCheckedMap(selectedMap);
							accountListViewAdapter.notifyDataSetChanged();
							selectAllRadio.setChecked(false);
						}

					}
				});

				doneLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mAccountDialog.dismiss();
						if (mAccountList.size() == selectIdSet.size()) {
							accountButton.setText("All");
						} else if (selectIdSet.size() > 0
								&& selectIdSet.size() < mAccountList.size()) {
							accountButton.setText("Mutiple Accounts");
						} else {
							accountButton.setText("No Account Selected");
						}
					}
				});

				accountListView = (ListView) view2.findViewById(R.id.mListView);
				accountListViewAdapter = new MultipleListAdapter(
						ExportTransactionPDFActivity.this);

				accountListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				accountListView.setAdapter(accountListViewAdapter);
				accountListView
						.setSelection((accountCheckItem - 1) > 0 ? (accountCheckItem - 1)
								: 0);
				accountListViewAdapter.setItemCheckedMap(selectedMap);
				accountListViewAdapter.setAdapterDate(mAccountList);
				accountListViewAdapter.notifyDataSetChanged();
				accountListView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								MultipleListAdapter.ViewHolder view = (MultipleListAdapter.ViewHolder) arg1
										.getTag();
								if (view.radioButton.isChecked()) {
									view.radioButton.setChecked(false);
								} else {
									view.radioButton.setChecked(true);
								}
								selectedMap.put(arg2,
										view.radioButton.isChecked());

								if (selectedMap.containsValue(false)) {
									selectAllRadio.setChecked(false);
									isSelectAllBoolean = false;
								} else {
									selectAllRadio.setChecked(true);
									isSelectAllBoolean = true;
								}

								if (selectedMap.get(arg2)) {
									selectIdSet.add((Integer) mAccountList.get(
											arg2).get("_id"));
								} else {
									selectIdSet.remove((Integer) mAccountList
											.get(arg2).get("_id"));
								}

								accountCheckItem = arg2;

							}
						});

				AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(
						ExportTransactionPDFActivity.this);
				mBuilder2.setTitle("Select Accounts");
				mBuilder2.setView(view2);
				mBuilder2
						.setOnCancelListener(new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface arg0) {
								mAccountDialog.dismiss();
								if (mAccountList.size() == selectIdSet.size()) {
									accountButton.setText("All");
								} else if (selectIdSet.size() > 0
										&& selectIdSet.size() < mAccountList
												.size()) {
									accountButton.setText("Mutiple Accounts");
								} else {
									accountButton
											.setText("No Account Selected");
								}
							}
						});
				mAccountDialog = mBuilder2.create();
				mAccountDialog.show();

				break;
			case R.id.start_btn:

				DatePickerDialog DPD = new DatePickerDialog(
						new ContextThemeWrapper(
								ExportTransactionPDFActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListenerStart, sYear, sMonth, sDay);
				DPD.setTitle("Start Date");
				DPD.show();

				break;
			case R.id.end_btn:

				DatePickerDialog DPD1 = new DatePickerDialog( // 锟侥憋拷theme
						new ContextThemeWrapper(
								ExportTransactionPDFActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListenerEnd, eYear, eMonth, eDay);
				DPD1.setTitle("End Date");
				DPD1.show();

				break;
			}
		}
	};

	public void filterData(List<Map<String, Object>> mData, int type) {// 过滤子类和父类

		List<Map<String, Object>> temChildDataList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempList;

		if (type == 0) {

			groupExpenseDataList.clear();
			childrenExpenseDataList.clear();
			selectedtExpenseList.clear();

			for (Map<String, Object> mMap : mData) { // 分离父类和子类
				String categoryName = (String) mMap.get("categoryName");
				int id = (Integer) mMap.get("_id");
				selectExpenseIdSet.add(id);
				if (categoryName.contains(":")) {
					temChildDataList.add(mMap);
				} else {
					groupExpenseDataList.add(mMap);
				}

			}

			for (Map<String, Object> mMap : groupExpenseDataList) { // 改类核心部分
				int j = -1; // 子tag，从-1开始

				String categoryName = (String) mMap.get("categoryName");
				tempList = new ArrayList<Map<String, Object>>();

				List<Map<Integer, Boolean>> tempSelectList = new ArrayList<Map<Integer, Boolean>>(); // 开始添加相应位置的选中状态
				Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
				mSelectMap.put(j, true);

				for (Map<String, Object> iMap : temChildDataList) {

					String cName = (String) iMap.get("categoryName");
					String temp[] = cName.split(":");
					if (temp[0].equals(categoryName)) {
						tempList.add(iMap);
						j++;// 如果有值，子位置先+1，表示改父类下有值
						mSelectMap.put(j, true);
					}

				}
				tempSelectList.add(mSelectMap);
				selectedtExpenseList.add(tempSelectList);
				childrenExpenseDataList.add(tempList);
			}

		} else if (type == 1) {

			groupIncomeDataList.clear();
			childrenIncomeDataList.clear();
			selectedtIncomeList.clear();

			for (Map<String, Object> mMap : mData) { // 分离父类和子类
				String categoryName = (String) mMap.get("categoryName");
				int id = (Integer) mMap.get("_id");
				selectIncomeIdSet.add(id);
				if (categoryName.contains(":")) {
					temChildDataList.add(mMap);
				} else {
					groupIncomeDataList.add(mMap);
				}

			}

			for (Map<String, Object> mMap : groupIncomeDataList) {
				int j = -1; // 子tag，从-1开始

				String categoryName = (String) mMap.get("categoryName");
				tempList = new ArrayList<Map<String, Object>>();

				List<Map<Integer, Boolean>> tempSelectList = new ArrayList<Map<Integer, Boolean>>(); // 开始添加相应位置的选中状态
				Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
				mSelectMap.put(j, true);

				for (Map<String, Object> iMap : temChildDataList) {

					String cName = (String) iMap.get("categoryName");
					String temp[] = cName.split(":");
					if (temp[0].equals(categoryName)) {
						tempList.add(iMap);
						j++;// 如果有值，子位置先+1，表示改父类下有值
						mSelectMap.put(j, true);
					}

				}
				tempSelectList.add(mSelectMap);
				selectedtIncomeList.add(tempSelectList);
				childrenIncomeDataList.add(tempList);
			}

		}

	}

	public void UnSelectDate(List<Map<String, Object>> mData, int type) {// 过滤子类和父类

		List<Map<String, Object>> temChildDataList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempList;

		if (type == 0) {

			selectedtExpenseList.clear();
			selectExpenseIdSet.clear();
			for (Map<String, Object> mMap : mData) { // 分离父类和子类
				String categoryName = (String) mMap.get("categoryName");
				int id = (Integer) mMap.get("_id");
				if (categoryName.contains(":")) {
					temChildDataList.add(mMap);
				}
			}

			for (Map<String, Object> mMap : groupExpenseDataList) { // 所有项目不选中
				int j = -1; // 子tag，从-1开始

				String categoryName = (String) mMap.get("categoryName");
				tempList = new ArrayList<Map<String, Object>>();

				List<Map<Integer, Boolean>> tempSelectList = new ArrayList<Map<Integer, Boolean>>(); // 开始添加相应位置的选中状态
				Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
				mSelectMap.put(j, false);

				for (Map<String, Object> iMap : temChildDataList) {

					String cName = (String) iMap.get("categoryName");
					String temp[] = cName.split(":");
					if (temp[0].equals(categoryName)) {
						tempList.add(iMap);
						j++;// 如果有值，子位置先+1，表示改父类下有值
						mSelectMap.put(j, false);
					}

				}
				tempSelectList.add(mSelectMap);
				selectedtExpenseList.add(tempSelectList);
			}

		} else if (type == 1) {

			selectedtIncomeList.clear();
			selectIncomeIdSet.clear();

			for (Map<String, Object> mMap : mData) { // 分离父类和子类
				String categoryName = (String) mMap.get("categoryName");
				int id = (Integer) mMap.get("_id");
				if (categoryName.contains(":")) {
					temChildDataList.add(mMap);
				}
			}

			for (Map<String, Object> mMap : groupIncomeDataList) {
				int j = -1; // 子tag，从-1开始

				String categoryName = (String) mMap.get("categoryName");
				tempList = new ArrayList<Map<String, Object>>();

				List<Map<Integer, Boolean>> tempSelectList = new ArrayList<Map<Integer, Boolean>>(); // 开始添加相应位置的选中状态
				Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
				mSelectMap.put(j, false);

				for (Map<String, Object> iMap : temChildDataList) {

					String cName = (String) iMap.get("categoryName");
					String temp[] = cName.split(":");
					if (temp[0].equals(categoryName)) {
						tempList.add(iMap);
						j++;// 如果有值，子位置先+1，表示改父类下有值
						mSelectMap.put(j, false);
					}

				}
				tempSelectList.add(mSelectMap);
				selectedtIncomeList.add(tempSelectList);
			}

		}

	}

	public void setSelectDate(List<Map<String, Object>> mData, int type) {// 选中所有项目

		List<Map<String, Object>> temChildDataList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempList;

		if (type == 0) {

			selectedtExpenseList.clear();

			for (Map<String, Object> mMap : mData) { // 分离父类和子类
				String categoryName = (String) mMap.get("categoryName");
				int id = (Integer) mMap.get("_id");
				selectExpenseIdSet.add(id);
				if (categoryName.contains(":")) {
					temChildDataList.add(mMap);
				}
			}

			for (Map<String, Object> mMap : groupExpenseDataList) { // 改类核心部分
				int j = -1; // 子tag，从-1开始

				String categoryName = (String) mMap.get("categoryName");
				tempList = new ArrayList<Map<String, Object>>();

				List<Map<Integer, Boolean>> tempSelectList = new ArrayList<Map<Integer, Boolean>>(); // 开始添加相应位置的选中状态
				Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
				mSelectMap.put(j, true);

				for (Map<String, Object> iMap : temChildDataList) {

					String cName = (String) iMap.get("categoryName");
					String temp[] = cName.split(":");
					if (temp[0].equals(categoryName)) {
						tempList.add(iMap);
						j++;// 如果有值，子位置先+1，表示改父类下有值
						mSelectMap.put(j, true);
					}

				}
				tempSelectList.add(mSelectMap);
				selectedtExpenseList.add(tempSelectList);
			}
		} else if (type == 1) {

			selectedtIncomeList.clear();

			for (Map<String, Object> mMap : mData) { // 分离父类和子类
				String categoryName = (String) mMap.get("categoryName");
				int id = (Integer) mMap.get("_id");
				selectIncomeIdSet.add(id);
				if (categoryName.contains(":")) {
					temChildDataList.add(mMap);
				}

			}

			for (Map<String, Object> mMap : groupIncomeDataList) {
				int j = -1; // 子tag，从-1开始

				String categoryName = (String) mMap.get("categoryName");
				tempList = new ArrayList<Map<String, Object>>();

				List<Map<Integer, Boolean>> tempSelectList = new ArrayList<Map<Integer, Boolean>>(); // 开始添加相应位置的选中状态
				Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
				mSelectMap.put(j, true);

				for (Map<String, Object> iMap : temChildDataList) {

					String cName = (String) iMap.get("categoryName");
					String temp[] = cName.split(":");
					if (temp[0].equals(categoryName)) {
						tempList.add(iMap);
						j++;// 如果有值，子位置先+1，表示改父类下有值
						mSelectMap.put(j, true);
					}

				}
				tempSelectList.add(mSelectMap);
				selectedtIncomeList.add(tempSelectList);
			}

		}

	}

	private DatePickerDialog.OnDateSetListener mDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			sYear = year;
			sMonth = monthOfYear;
			sDay = dayOfMonth;
			updateDisplayStart();
		}

	};

	private DatePickerDialog.OnDateSetListener mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			eYear = year;
			eMonth = monthOfYear;
			eDay = dayOfMonth;
			updateDisplayEnd();
		}

	};

	private void updateDisplayStart() {
		// TODO Auto-generated method stub

		String dueDateString = (new StringBuilder().append(sMonth + 1)
				.append("-").append(sDay).append("-").append(sYear)).toString();

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dueDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startDate = c.getTimeInMillis();
		Date date = new Date(startDate);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
		startButton.setText(sdf.format(date));
	}

	private void updateDisplayEnd() {
		// TODO Auto-generated method stub

		String dueDateString = (new StringBuilder().append(eMonth + 1)
				.append("-").append(eDay).append("-").append(eYear)).toString();

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dueDateString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		endDate = c.getTimeInMillis();
		Date date = new Date(endDate);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
		endButton.setText(sdf.format(date));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: // 锟斤拷锟较角帮拷钮锟斤拷锟截碉拷id
			finish();
			return true;

		case R.id.export:

			if ((selectIncomeIdSet.size() + selectExpenseIdSet.size()) == 0) {

				new AlertDialog.Builder(ExportTransactionPDFActivity.this)
						.setTitle("Warning! ")
						.setMessage(
								" Please make sure the category name is not empty! ")
						.setPositiveButton("Retry",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();

									}
								}).show();

			} else if (selectIdSet.size() == 0) {

				new AlertDialog.Builder(ExportTransactionPDFActivity.this)
						.setTitle("Warning! ")
						.setMessage(
								" Please make sure the account name is not empty! ")
						.setPositiveButton("Retry",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();

									}
								}).show();

			} else {

				creatPDFAll();

			}

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message message) {

			switch (message.what) {

			case 1:
				dialog_loading.dismiss();

				final Intent pdfemailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				pdfemailIntent.setType("text/html;charset=utf-8");
				pdfemailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"subject");
				pdfemailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Transactions data: "+ getMilltoTimeStyle1(startDate)+" - "+ getMilltoTimeStyle1(endDate));
				pdfemailIntent.putExtra(Intent.EXTRA_TEXT, "These Transactions was generated by Pocket Expense.");
				pdfemailIntent.putExtra(
						Intent.EXTRA_STREAM,
						Uri.parse("file://"+PDFPATH));
				startActivity(Intent
						.createChooser(pdfemailIntent, "Export"));
				
				
				break;
			}
		}

	};

	public void creatPDFAll() {

		File file = new File("/sdcard/PocketExpense/");
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e + "something wrong");
			}
		}

		File path;
		path = new File("/sdcard/PocketExpense/");
		File dir = null;

		dir = new File(path, "PocketExpenseReport_Transaction.pdf");

		if (!dir.exists()) {
			try {
				dir.createNewFile();
			} catch (Exception e) {
			}
		} else {
			dir.delete();
		}

		dialog_loading = ProgressDialog.show(ExportTransactionPDFActivity.this,
				"waiting...", "Hold on,Data is being exported ...", true);

		new Thread(new Runnable() {
			public void run() {
				try {

					doc = new Document(PageSize.A4, 40, 40, 20, 60);
					
					doc.addAuthor("Pocket Expense @Appxy");
					doc.addCreator("Pocket Expense");
					doc.addSubject("Transactions Export");
					doc.addKeywords("Transactions"); 
					doc.addTitle("Transactions Data");
				      
					PdfWriter writer = PdfWriter.getInstance(doc,
							new FileOutputStream(PDFPATH));
					doc.open();

					Font fontTitle = new Font(Font.FontFamily.HELVETICA, 30,
							Font.BOLD);
					fontTitle.setColor(60, 60, 60);
					Paragraph title = new Paragraph("Transaction Report",
							fontTitle); // 添加标题
					title.setAlignment(Element.ALIGN_CENTER);
					doc.add(title);

					createPDF();

					doc.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					doc.close();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					doc.close();
				}

				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		}).start();

	}

	private void createPDF() {

		/*
		 * Head info 1
		 */

		PdfPCell cell;
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(30f);

		Font fontHead = new Font(Font.FontFamily.HELVETICA, 15, Font.NORMAL);
		fontHead.setColor(128, 128, 128);

		cell = new PdfPCell(new Phrase("Report Date:  "
				+ getMilltoTimeStyle1(System.currentTimeMillis()), fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(4);
		cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setMinimumHeight(30);
		table.addCell(cell);

		cell = new PdfPCell(
				new Phrase("Generated By Pockect Expense", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(4);
		cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setMinimumHeight(30);
		table.addCell(cell);
		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Head info 2
		 */
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(11f);

		Font fontHead1 = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);
		fontHead1.setColor(128, 128, 128);

		cell = new PdfPCell(new Phrase(getMilltoTimeStyle1(startDate) + " ~ "
				+ getMilltoTimeStyle1(endDate), fontHead1));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(30);
		table.addCell(cell);
		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashSet<Integer> AllIdSet = new HashSet<Integer>();
		AllIdSet.addAll(selectExpenseIdSet);
		AllIdSet.addAll(selectIncomeIdSet);
		List<Map<String, Object>> mList = ExportDao.selectTransactionByTimeBE(
				ExportTransactionPDFActivity.this, startDate, endDate, seqType,
				groupType, AllIdSet,
				mExpenseDataList.size() + mIncomeDataList.size(), selectIdSet,
				mAccountList.size());


		BigDecimal expenseAmount = new BigDecimal(0);
		BigDecimal incomeAmount = new BigDecimal(0);
		BigDecimal clearAmount = new BigDecimal(0);
		BigDecimal unClearAmount = new BigDecimal(0);

		for (Map<String, Object> iMap : mList) {
			int isClear = (Integer) iMap.get("isClear");
			String amount = (String) iMap.get("amount");
			int expenseAccount = (Integer) iMap.get("expenseAccount");
			int incomeAccount = (Integer) iMap.get("incomeAccount");

			BigDecimal b1 = new BigDecimal(amount);

			if (expenseAccount > 0) {
				expenseAmount = expenseAmount.add(b1);

				if (isClear != 1) {
					unClearAmount = unClearAmount.subtract(b1);
				} else {
					clearAmount = clearAmount.subtract(b1);
				}

			} else if (incomeAccount > 0) {
				incomeAmount = incomeAmount.add(b1);

				if (isClear != 1) {
					unClearAmount = unClearAmount.add(b1);
				} else {
					clearAmount = clearAmount.add(b1);
				}

			}

		}

		List<Map<String, Object>> mTranList = new ArrayList<Map<String, Object>>(); // 查询account的transfer
		if (isTransfer) {
			mTranList = ExportDao.selectTransferByTimeBE(
					ExportTransactionPDFActivity.this, startDate, endDate,
					selectIdSet, mAccountList.size());
		}
		/*
		 * Head info 3
		 */
		table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingBefore(11f);

		Font fontHead2 = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);
		fontHead2.setColor(88, 88, 88);

		cell = new PdfPCell(new Phrase("Total Expense:  " + currencySrtring
				+ MEntity.doublepoint2str(expenseAmount.doubleValue() + ""),
				fontHead2));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setMinimumHeight(30);
		table.addCell(cell);

		String totalClearString = "";
		double clearDouble = clearAmount.doubleValue();
		if (clearDouble >= 0) {
			totalClearString = currencySrtring
					+ MEntity.doublepoint2str(clearDouble + "");
		} else {
			totalClearString = "-" + currencySrtring
					+ MEntity.doublepoint2str((0 - clearDouble) + "");
		}
		cell = new PdfPCell(new Phrase("Total Cleared:  " + totalClearString,
				fontHead2));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setMinimumHeight(30);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase((mList.size() + mTranList.size())
				+ " Transactons", fontHead2));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setMinimumHeight(30);
		table.addCell(cell);

		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Head info 4
		 */
		table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingBefore(2f);

		cell = new PdfPCell(new Phrase("Total Income:  " + currencySrtring
				+ MEntity.doublepoint2str(incomeAmount.doubleValue() + ""),
				fontHead2));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setMinimumHeight(30);
		table.addCell(cell);

		String totalUnClearString = "";
		double clearUnDouble = unClearAmount.doubleValue();
		if (clearUnDouble >= 0) {
			totalUnClearString = currencySrtring
					+ MEntity.doublepoint2str(clearUnDouble + "");
		} else {
			totalUnClearString = "-" + currencySrtring
					+ MEntity.doublepoint2str((0 - clearUnDouble) + "");
		}

		cell = new PdfPCell(new Phrase("Total Uncleared:  "
				+ totalUnClearString, fontHead2));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setMinimumHeight(30);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("", fontHead2));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(15);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setMinimumHeight(30);
		table.addCell(cell);

		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getGroupSet(groupType, mList); // 筛选父本

		for (int gId : groupSortList) {

			if (groupType == 0) {
				
				getLittleTitle(ExportDao.selectCategoryName(
						ExportTransactionPDFActivity.this, gId), "Account");
				
				BigDecimal totalAmount = new BigDecimal(0);
				
				for (Map<String, Object> iMap : mList) {
					int category = (Integer) iMap.get("category");
					if (category == gId) {

						long dateTime = (Long) iMap.get("dateTime");
						String accName = (String) iMap.get("accName");
						String categoryName = (String) iMap.get("categoryName");
						String payeeName = (String) iMap.get("payeeName");
						String amount = (String) iMap.get("amount");
						int isClear = (Integer) iMap.get("isClear");
						String notes = (String) iMap.get("notes");
						int expenseAccount = (Integer) iMap
								.get("expenseAccount");
						int incomeAccount = (Integer) iMap.get("incomeAccount");
						
						BigDecimal b1 = new BigDecimal(amount);
						
						String everyAmountString = "";
						double amountDouble = b1.doubleValue();
						
						if (expenseAccount > 0) {
							totalAmount = totalAmount.subtract(b1);
							everyAmountString = "-" + currencySrtring
									+ MEntity.doublepoint2str((amountDouble) + "");
							
						} else if (incomeAccount > 0) {
							totalAmount = totalAmount.add(b1);
							everyAmountString = currencySrtring
									+ MEntity.doublepoint2str(amountDouble + "");
						}
						
						getContent( getMilltoTimeStyle1(dateTime), payeeName, accName, everyAmountString, notes);
					}
					
				}
				
				String totalAmountString = "";
				double totalDouble = totalAmount.doubleValue();
				if (totalDouble >= 0) {
					totalAmountString = currencySrtring
							+ MEntity.doublepoint2str(totalDouble + "");
				} else {
					totalAmountString = "-" + currencySrtring
							+ MEntity.doublepoint2str((0 - totalDouble) + "");
				}
				
				getTotal(totalAmountString);
				
			} else {
				getLittleTitle(ExportDao.selectAccountName(
						ExportTransactionPDFActivity.this, gId), "Category");
				
				BigDecimal totalAmount = new BigDecimal(0);
				
				for (Map<String, Object> iMap : mList) {
					int expenseAccount = (Integer) iMap.get("expenseAccount");
					int incomeAccount = (Integer) iMap.get("incomeAccount");
					
					if ( (expenseAccount == gId) || (incomeAccount == gId) ) {

						long dateTime = (Long) iMap.get("dateTime");
						String accName = (String) iMap.get("accName");
						String categoryName = (String) iMap.get("categoryName");
						String payeeName = (String) iMap.get("payeeName");
						String amount = (String) iMap.get("amount");
						int isClear = (Integer) iMap.get("isClear");
						String notes = (String) iMap.get("notes");
						
						BigDecimal b1 = new BigDecimal(amount);
						
						String everyAmountString = "";
						double amountDouble = b1.doubleValue();
						
						if (expenseAccount > 0) {
							totalAmount = totalAmount.subtract(b1);
							everyAmountString = "-" + currencySrtring
									+ MEntity.doublepoint2str((amountDouble) + "");
							
						} else if (incomeAccount > 0) {
							totalAmount = totalAmount.add(b1);
							everyAmountString = currencySrtring
									+ MEntity.doublepoint2str(amountDouble + "");
						}
						
						getContent( getMilltoTimeStyle1(dateTime), payeeName, categoryName, everyAmountString, notes);
					}
					
				}
				
				String totalAmountString = "";
				double totalDouble = totalAmount.doubleValue();
				if (totalDouble >= 0) {
					totalAmountString = currencySrtring
							+ MEntity.doublepoint2str(totalDouble + "");
				} else {
					totalAmountString = "-" + currencySrtring
							+ MEntity.doublepoint2str((0 - totalDouble) + "");
				}
				
				getTotal(totalAmountString);
				
			}

		}

		if (isTransfer) {
			getGroupSetForTranfer( mTranList);
			for (int gId:groupSortListForTran) {
				
				if (groupType == 0) {
					
					getLittleTitle("To "+ExportDao.selectAccountName(ExportTransactionPDFActivity.this, gId), "Account");
				}else{
					getLittleTitle("To "+ExportDao.selectAccountName(ExportTransactionPDFActivity.this, gId), "Category");
				}
					
					BigDecimal totalAmount = new BigDecimal(0);
					
					for (Map<String, Object> iMap : mTranList) {
						int category = (Integer) iMap.get("incomeAccount");
						if (category == gId) {

							long dateTime = (Long) iMap.get("dateTime");
							String payeeName = (String) iMap.get("payeeName");
							String amount = (String) iMap.get("amount");
							String notes = (String) iMap.get("notes");
							int expenseAccount = (Integer) iMap
									.get("expenseAccount");
							int incomeAccount = (Integer) iMap.get("incomeAccount");
							
							BigDecimal b1 = new BigDecimal(amount);
							
							String everyAmountString = "";
							double amountDouble = b1.doubleValue();
							totalAmount = totalAmount.add(b1);
							
							everyAmountString = currencySrtring+ MEntity.doublepoint2str(amountDouble + "");
							
							getContent( getMilltoTimeStyle1(dateTime), payeeName, "XFER From "+ExportDao.selectAccountName(ExportTransactionPDFActivity.this, expenseAccount), everyAmountString, notes);
						}
						
					}
					
					String totalAmountString = "";
					double totalDouble = totalAmount.doubleValue();
				    totalAmountString = currencySrtring+ MEntity.doublepoint2str(totalDouble + "");
					getTotal(totalAmountString);
					
				
			}
		}

	}

	public void getTotal(String totalAmount) {
		PdfPCell cell;
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(2f);
		
		Font fontHead = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
		fontHead.setColor(BaseColor.BLACK);

		cell = new PdfPCell(new Phrase("Total: ", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(totalAmount, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);
		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getContent(String date, String payee, String ACCorCATE,
			String amount, String memo) { // 设置内容样板

		PdfPCell cell;
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(2f);

		Font fontHead = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
		fontHead.setColor(100, 100, 100);

		cell = new PdfPCell(new Phrase(date, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(payee, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(ACCorCATE, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(amount, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(memo, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);
		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getLittleTitle(String categoryName, String sortTypeName) {

		PdfPCell cell;
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(18f);

		Font fontTitle = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
		fontTitle.setColor(128, 128, 128);
		cell = new PdfPCell(new Phrase(categoryName, fontTitle));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(2f);

		Font fontHead = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
		fontHead.setColor(60, 60, 60);

		cell = new PdfPCell(new Phrase("Date", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Payee", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(sortTypeName, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Amount", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Memo", fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);
		table.addCell(cell);
		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getGroupSetForTranfer(List<Map<String, Object>> mList) {
		HashSet<Integer> groupSet = new HashSet<Integer>();
		groupSortListForTran.clear();
		for (Map<String, Object> iMap : mList) {
			
				int incomeAccount = (Integer) iMap.get("incomeAccount");
				if (groupSet.add(incomeAccount)) {
					groupSortListForTran.add(incomeAccount);
				}

		}
	}
	

	public void getGroupSet(int groupType, List<Map<String, Object>> mList) {
		HashSet<Integer> groupSet = new HashSet<Integer>();
		groupSortList.clear();
		for (Map<String, Object> iMap : mList) {
			if (groupType == 0) {
				int category = (Integer) iMap.get("category");

				if (groupSet.add(category)) {
					groupSortList.add(category);
				}

			} else {
				int expenseAccount = (Integer) iMap.get("expenseAccount");
				int incomeAccount = (Integer) iMap.get("incomeAccount");
				if (expenseAccount > 0) {

					if (groupSet.add(expenseAccount)) {
						groupSortList.add(expenseAccount);
					}

				} else {
					if (groupSet.add(incomeAccount)) {
						groupSortList.add(incomeAccount);
					}
				}
			}
		}
	}

	public String getMilltoTimeStyle1(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String getMilltoTime(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String turnMilltoString(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String turnMilltoMDY(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String getMilltoTimeDetail(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMMMdd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.export, menu);
		return true;
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Dropbox sync successed",
				Toast.LENGTH_SHORT).show();
	}
	
}
