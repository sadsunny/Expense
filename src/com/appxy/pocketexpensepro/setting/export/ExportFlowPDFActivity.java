package com.appxy.pocketexpensepro.setting.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.MainActivity;
import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.overview.OverViewDao;
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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class ExportFlowPDFActivity extends Activity{
	
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
	
	private int typeExport = 0; //导出类型
	private int columnType = 0; //排列类型
	
	private static final String PDFPATH = "/sdcard/PocketExpense/PocketExpenseReport_CashFlow.pdf";
	private Document doc;
	private Dialog dialog_loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export_flow_pdf);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
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
						new ContextThemeWrapper(
								ExportFlowPDFActivity.this,
								android.R.style.Theme_Holo_Light),
						mDateSetListenerStart, sYear, sMonth, sDay);
				DPD.setTitle("Start Date");
				DPD.show();

				break;
			case R.id.end_btn:

				DatePickerDialog DPD1 = new DatePickerDialog( // 锟侥憋拷theme
						new ContextThemeWrapper(
								ExportFlowPDFActivity.this,
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

				final Intent pdfemailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				pdfemailIntent.setType("text/html;charset=utf-8");
				pdfemailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"subject");
				pdfemailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"CashFlow Report: "+ getMilltoTimeStyle1(startDate)+" - "+ getMilltoTimeStyle1(endDate));
				pdfemailIntent.putExtra(Intent.EXTRA_TEXT, "These Report was generated by Pocket Expense.");
				pdfemailIntent.putExtra(
						Intent.EXTRA_STREAM,
						Uri.parse(PDFPATH));
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
					
					doc.addAuthor("Pocket Expense @Appxy");
					doc.addCreator("Pocket Expense");
					doc.addSubject("Cash Flow Export");
					doc.addKeywords("Transactions"); 
					doc.addTitle("Transactions Data");
				      
					PdfWriter.getInstance(doc,
							new FileOutputStream(PDFPATH));
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
			createInflow();
			createOutflow();
		} else if (typeExport == 1) {
			createOutflow();
		} else if (typeExport == 2) {
			createInflow();
		}
		
		
	}
	
	private void createInflow() {
		
		List<Map<String, Object>> mDataList = ExportDao.selectTransactionByTimeBE(ExportFlowPDFActivity.this,startDate, endDate,0);
		if (columnType == 0) {
			
		} else {

		}
		
	}
	
	private void createOutflow() {
		List<Map<String, Object>> mDataList = ExportDao.selectTransactionByTimeBE(ExportFlowPDFActivity.this,startDate, endDate,1);
		if (columnType == 0) {
			
			for (int i = 0; i < array.length; i++) {
				
			}
			
		} else {

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
	
}
