package com.appxy.pocketexpensepro.setting.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;
import com.appxy.pocketexpensepro.overview.OverViewDao;
import com.appxy.pocketexpensepro.passcode.BaseHomeActivity;
import com.dropbox.sync.android.DbxRecord;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.R.integer;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class ExportFlowPDFActivity extends BaseHomeActivity {

	private Spinner typeSpinner;
	private Button startButton;
	private Button endButton;
	private Spinner columnSpinner;

	private int sYear;
	private int sMonth;
	private int sDay;

	private int eYear;
	private int eMonth;
	private int eDay;
	private long startDate;
	private long endDate;

	private int typeExport = 0; // 导出类型
	private int columnType = 0; // 排列类型
																	
	private static final String PDFPATH = "/sdcard/PocketExpense/PocketExpenseReport_CashFlow.pdf";
	private Document doc;
	private Dialog dialog_loading;
	private final static long DAYMILLIS = 86400000L;
	private List<Integer> groupCategoryList;
	private List<List<Map<Integer, Object>>> eachTableEDataList; // 每个table的数据，动态添加进去,除去头文件！！
	private List<Map<Integer, Object>> mtableList = new ArrayList<Map<Integer, Object>>(); // 每个table的数据，动态添加进去！！
	private  String mCurrencyString = Common.CURRENCY_SIGN[Common.CURRENCY];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export_flow_pdf);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		groupCategoryList = new ArrayList<Integer>();
		eachTableEDataList = new ArrayList<List<Map<Integer, Object>>>();

		typeSpinner = (Spinner) findViewById(R.id.type_spin);
		startButton = (Button) findViewById(R.id.start_btn);
		endButton = (Button) findViewById(R.id.end_btn);
		columnSpinner = (Spinner) findViewById(R.id.column_spin);

		ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter
				.createFromResource(ExportFlowPDFActivity.this,
						R.array.flow_type,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(adapterSpinner);
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				typeExport = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		ArrayAdapter<CharSequence> adapterSpinner1 = ArrayAdapter
				.createFromResource(ExportFlowPDFActivity.this,
						R.array.column_type,
						android.R.layout.simple_spinner_dropdown_item);
		adapterSpinner1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		columnSpinner.setAdapter(adapterSpinner1);
		columnSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				columnType = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

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
			case R.id.start_btn:

				DatePickerDialog DPD = new DatePickerDialog(
						new ContextThemeWrapper(ExportFlowPDFActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListenerStart, sYear, sMonth, sDay);
				DPD.setTitle("Start Date");
				DPD.show();

				break;
			case R.id.end_btn:

				DatePickerDialog DPD1 = new DatePickerDialog( // 锟侥憋拷theme
						new ContextThemeWrapper(ExportFlowPDFActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListenerEnd, eYear, eMonth, eDay);
				DPD1.setTitle("End Date");
				DPD1.show();
			}
		}
	};

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
		case android.R.id.home:
			finish();
			return true;

		case R.id.export:

			creatPDFAll();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message message) {

			switch (message.what) {

			case 1:
				dialog_loading.dismiss();

				Intent pdfemailIntent = new Intent(Intent.ACTION_SEND);
				pdfemailIntent.setType("text/html;charset=utf-8");
				pdfemailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CashFlow Report: " + getMilltoTimeStyle1(startDate)+ " - " + getMilltoTimeStyle1(endDate));
				pdfemailIntent.putExtra(Intent.EXTRA_TEXT, "The Report was generated by Pocket Expense.");
				pdfemailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+PDFPATH));
				startActivity(Intent.createChooser(pdfemailIntent, "Export"));

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

		dir = new File(path, "PocketExpenseReport_CashFlow.pdf");

		if (!dir.exists()) {
			try {
				dir.createNewFile();
			} catch (Exception e) {
			}
		} else {
			dir.delete();
		}

		dialog_loading = ProgressDialog.show(ExportFlowPDFActivity.this,
				"waiting...", "Hold on,Data is being exported ...", true);

		new Thread(new Runnable() {
			public void run() {
				try {

					doc = new Document(PageSize.A4, 40, 40, 20, 60);

					PdfWriter.getInstance(doc, new FileOutputStream(PDFPATH));
					doc.open();

					Font fontTitle = new Font(Font.FontFamily.HELVETICA, 30,
							Font.BOLD);
					fontTitle.setColor(60, 60, 60);
					Paragraph title = new Paragraph("Cash Flow Report",
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
		cell.setBorderWidthBottom(0);
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

		if (typeExport == 0) {
			createFlow(0); 
			createFlow(1); 
		} else if (typeExport == 1) {
			createFlow(1); ;
		} else if (typeExport == 2) {
			createFlow(0); 
		}

	}

	private void createFlow(int inout) {
		
		/*
		 * 准备数据，title，每行amount，最后的total，所有的total
		 */
		List<Map<String, Object>> mDataList ;
		if (inout == 0) {
			 mDataList = ExportDao
					.selectTransactionByTimeBE(ExportFlowPDFActivity.this,
							startDate, endDate, 0);
		} else {
			mDataList = ExportDao
					.selectTransactionByTimeBE(ExportFlowPDFActivity.this,
							startDate, endDate, 1);
		}
		
		
		int columnBlock = 6; // 统计块周或者月
		if (columnType == 0) {
			columnBlock = 6;
		} else {
			columnBlock = 29;
		}

		getGroupCategory(mDataList);// 分类出CategoryId
		if (inout == 0) {
			getLittleTitle("Inflow");
		} else {
			getLittleTitle("Outflow");
		}
		
	if (mDataList.size()  > 0) {
			

		eachTableEDataList.clear();
		for (int i = 0; i < (groupCategoryList.size() + 1); i++) { // 最后一项用来统计total
			List<Map<Integer, Object>> mTList = new ArrayList<Map<Integer, Object>>();
			Map<Integer, Object> tMap = new HashMap<Integer, Object>();
			tMap.put(1, "0");
			mTList.add(tMap);
			eachTableEDataList.add(mTList);
		}

		long theStartDate = startDate; // 每次table循环完后的时间起点
		List<Map<String, Object>> mDateRanteList = new ArrayList<Map<String, Object>>(); // 所有的时间段集合包括旗帜时间
		int Atg = 0; // 标示位
		while (theStartDate <= endDate) {

			long initStartDate = theStartDate;
			Map<String, Object> mMap = new HashMap<String, Object>();
			String everySEDateString = getMilltoTimeStyle2(theStartDate);
			mMap.put("startDate", theStartDate);
			
			theStartDate = theStartDate + (columnBlock) * DAYMILLIS;
			long initEndDate = theStartDate;

			List<Map<Integer, Object>> mTList;
			Map<Integer, Object> tMap;
			
			if (theStartDate > endDate ) {// 别看了，坑爹的算法
				
				if ( ( (theStartDate - columnBlock * DAYMILLIS) < endDate) ) {
					
				initEndDate = endDate;
				everySEDateString = everySEDateString + " ~ " + getMilltoTimeStyle2(endDate);
				mMap.put("endDate", endDate);
				mMap.put("ecachDate", everySEDateString);
				mDateRanteList.add(mMap);
				
				for (int i = 0; i < groupCategoryList.size(); i++) {

					int cId = groupCategoryList.get(i);

					mTList = new ArrayList<Map<Integer, Object>>();
					tMap = new HashMap<Integer, Object>();
					BigDecimal b0 = new BigDecimal(0); // 每个周期，并且对应category
					BigDecimal bt = new BigDecimal(0); // 每个周期。一列所有总和

					for (Map<String, Object> iMap : mDataList) {

						int category = (Integer) iMap.get("category");
						long dateTime = (Long) iMap.get("dateTime");
						String amount = (String) iMap.get("amount");
						BigDecimal b1 = new BigDecimal(amount);
						if ((cId == category) && (dateTime >= initStartDate)
								&& (dateTime < (initEndDate + DAYMILLIS - 1))) {

							b0 = b0.add(b1);
						}

						if ((dateTime >= initStartDate)
								&& (dateTime < (initEndDate + DAYMILLIS - 1))) {

							bt = bt.add(b1);
						}

					}

					tMap.put(1, mCurrencyString+b0.doubleValue());
					mTList.add(tMap);

					eachTableEDataList.get(i).add(tMap); // i代表category的种类顺序，atg代表每个category的金额属性

					if (i == (groupCategoryList.size() - 1)) {
						Map<Integer, Object> zMap = new HashMap<Integer, Object>();
						zMap.put(1, mCurrencyString+bt.doubleValue());
						eachTableEDataList.get(i + 1).add(zMap);
					}

				}
				Atg++;
				}
				break;
				
			} else {
				
				everySEDateString = everySEDateString + " ~ " + getMilltoTimeStyle2(theStartDate);
				mMap.put("endDate", theStartDate);
				mMap.put("ecachDate", everySEDateString);
				mDateRanteList.add(mMap);
				
				for (int i = 0; i < groupCategoryList.size(); i++) {

					int cId = groupCategoryList.get(i);

					mTList = new ArrayList<Map<Integer, Object>>();
					tMap = new HashMap<Integer, Object>();
					BigDecimal b0 = new BigDecimal(0); // 每个周期，并且对应category
					BigDecimal bt = new BigDecimal(0); // 每个周期。一列所有总和

					for (Map<String, Object> iMap : mDataList) {

						int category = (Integer) iMap.get("category");
						long dateTime = (Long) iMap.get("dateTime");
						String amount = (String) iMap.get("amount");
						BigDecimal b1 = new BigDecimal(amount);
						if ((cId == category) && (dateTime >= initStartDate)
								&& (dateTime < (initEndDate + DAYMILLIS - 1))) {

							b0 = b0.add(b1);
						}

						if ((dateTime >= initStartDate)
								&& (dateTime < (initEndDate + DAYMILLIS - 1))) {

							bt = bt.add(b1);
						}

					}

					tMap.put(1, mCurrencyString+b0.doubleValue());
					mTList.add(tMap);

					eachTableEDataList.get(i).add(tMap); // i代表category的种类顺序，atg代表每个category的金额属性

					if (i == (groupCategoryList.size() - 1)) {
						Map<Integer, Object> zMap = new HashMap<Integer, Object>();
						zMap.put(1, mCurrencyString+bt.doubleValue());
						eachTableEDataList.get(i + 1).add(zMap);
					}

				}
				Atg++;
			}
			theStartDate = theStartDate +DAYMILLIS;
		}
		
		Log.v("mtag", "eachTableEDataList" + eachTableEDataList);
		Log.v("mtag", "mDateRanteList" + mDateRanteList);
		Log.v("mtag", "Atg" + Atg);
		
		
		
		List<String> allTotalList = new ArrayList<String>();
		for (int cId:groupCategoryList) { //所有行的总和
			
			BigDecimal b0 = new BigDecimal(0); 
			for (Map<String, Object> iMap : mDataList) {
				int category = (Integer) iMap.get("category");
				if ((cId == category)) {
					String amount = (String) iMap.get("amount");
					BigDecimal b1 = new BigDecimal(amount);
					b0 = b0.add(b1);
				}
			}
			allTotalList.add(mCurrencyString+b0.doubleValue());
		}
		
		BigDecimal bAll = new BigDecimal(0); 
		for (Map<String, Object> iMap : mDataList) { //最终总和
			
				String amount = (String) iMap.get("amount");
				BigDecimal b1 = new BigDecimal(amount);
				bAll = bAll.add(b1);
		}
		allTotalList.add(mCurrencyString+bAll.doubleValue());
		Log.v("mtag", "allTotalList"+allTotalList);
		
		/*
		 * 开始写入到PDF
		 */
		
		int tableSum = mDateRanteList.size()/5; //计算需要几个table
		if (mDateRanteList.size()%5 > 0) {
			tableSum = tableSum +1;
		}
		Log.v("mtag", "tableSum"+tableSum);
		
		for (int j = 0; j < tableSum; j++) {
			
			int leftCol = (mDateRanteList.size()-(j+1)*5);
			Log.v("mtag", "leftCol"+leftCol);
			if ( leftCol >= 0) {
				getDateTitle(5+1, 0, j, mDateRanteList);
				getContentRow(5+1, 0, j, eachTableEDataList, groupCategoryList, allTotalList);
			}else {
				int wide = mDateRanteList.size()%5;
				getDateTitle(wide, 1, j, mDateRanteList);
				getContentRow(wide, 1, j, eachTableEDataList, groupCategoryList, allTotalList);
			}
			
		}
		
	}
	}

	public PdfPCell getContentCell(String cellName) { // 内容的cell
		PdfPCell cell;
		Font fontHead = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
		fontHead.setColor(100, 100, 100);

		cell = new PdfPCell(new Phrase(cellName, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(0);
		cell.setBorderColorBottom(BaseColor.DARK_GRAY);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);

		return cell;
	}

	public void getContentRow(int tableWid,int islast,int jtg,List<List<Map<Integer, Object>>> eachTableEDataList ,List<Integer> groupCategoryList,List<String> allTotalList) {// 设内容

		for (int j = 0; j <eachTableEDataList.size(); j++) {
			
			if ( islast == 1) {
				
				PdfPTable table = new PdfPTable(6);
				table.setWidthPercentage(100);
				table.setSpacingBefore(2f);
				
				if (j == eachTableEDataList.size()-1) {
					table.addCell(getContentCell("Total") );
				}else {
					table.addCell(getContentCell (ExportDao.selectCategoryName(ExportFlowPDFActivity.this, groupCategoryList.get(j))) );
				}
				
				
				for (int i = 0; i < tableWid; i++) {
					table.addCell( getContentCell( (String)eachTableEDataList.get(j).get(jtg*5+i+1).get(1) ));
				}
				
				//开始加total
				table.addCell(getContentCell (allTotalList.get(j)) );
				
				for (int k = 0; k < 5 - tableWid; k++) {
					table.addCell(getContentCell( " " ));
				}
				
				try {
					doc.add(table);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else {
				
			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			table.setSpacingBefore(2f);
			
			if (j == eachTableEDataList.size()-1) {
				table.addCell(getContentCell("Total") );
			}else {
				table.addCell(getContentCell (ExportDao.selectCategoryName(ExportFlowPDFActivity.this, groupCategoryList.get(j))) );
			}
			
			
			for (int i = 0; i < 5; i++) {
				table.addCell(getContentCell( (String)eachTableEDataList.get(j).get(jtg*5+i+1).get(1) ));
			}
			
			
			try {
				doc.add(table);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		}
		
		
	}

	public void getGroupCategory(List<Map<String, Object>> mList) { // 筛选category
		HashSet<Integer> groupSet = new HashSet<Integer>();
		groupCategoryList.clear();
		for (Map<String, Object> iMap : mList) {

			int category = (Integer) iMap.get("category");
			if (groupSet.add(category)) {
				groupCategoryList.add(category);
			}

		}
	}

	public int getTableSum(long sDate, long eDate, int columnBlock) { // 计算需要多少个table

		int TableSum = (int) ((eDate - sDate) / DAYMILLIS / columnBlock / 5);

		if (((eDate - sDate) / DAYMILLIS) % (columnBlock * 5) >= 0) {
			TableSum = TableSum + 1;
		}
		return TableSum;
	}

	public PdfPCell getDateTitleCell(String cellName) { // Category小标题的cell
		PdfPCell cell;
		Font fontHead = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
		fontHead.setColor(60, 60, 60);

		cell = new PdfPCell(new Phrase(cellName, fontHead));
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setBorderWidthBottom(1);
		cell.setBorderColorBottom(BaseColor.BLACK);
		cell.setPaddingBottom(12);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setMinimumHeight(27);

		return cell;
	}

	public void getDateTitle(int tableWid,int islast,int jtg,List<Map<String, Object>> mDateRanteList) {// 设置第一行,多少列，是否为最后一个table,处于第几个table

		PdfPCell cell;
		PdfPTable table ;
			 table = new PdfPTable(6);
			 table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setSpacingBefore(13f);

		table.addCell(getDateTitleCell("Category"));

		if (islast == 1) {
			
			for (int i = 0; i < tableWid; i++) {
				table.addCell(getDateTitleCell( (String)mDateRanteList.get((jtg)*5+i).get("ecachDate") ));
			}
			
			table.addCell(getDateTitleCell("Total"));
			
			for (int i = 0; i < (5 - tableWid); i++) {
				table.addCell(getDateTitleCell(" "));
			}
			
		}else {
			
			for (int i = 0; i < 5; i++) {
				table.addCell(getDateTitleCell( (String)mDateRanteList.get( (jtg)*5+i ).get("ecachDate") ));
			}
			
		}

		try {
			doc.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getLittleTitle(String categoryName) {

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.export, menu);
		return true;
	}

	public String getMilltoTimeStyle1(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String getMilltoTimeStyle2(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		
	}

}
