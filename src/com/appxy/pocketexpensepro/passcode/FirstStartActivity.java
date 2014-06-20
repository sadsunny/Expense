//package com.appxy.pocketexpensepro.passcode;
//
//import java.io.File;
//import java.io.FileInputStream;
//
//import org.apache.http.util.EncodingUtils;
//
//import com.appxy.pocketexpensepro.R;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnKeyListener;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.Menu;
//
//public class FirstStartActivity extends Activity{
//private DatabaseUtility DBUtility;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		
//		DBUtility = new DatabaseUtility(this);
//		
//			final Dialog alertDialog = new AlertDialog.Builder(this)
//					.setMessage("Thanks for using our Full Version! Please restart the Full Version to complete the migration.")
//					.setOnKeyListener(keylistener)
//					.setNegativeButton("OK",new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface arg0,
//										int arg1) {
//									// TODO Auto-generated method stub
//									arg0.dismiss();
//									
//									String res_category = "";
//									String[] array_category = null;
//									
//									try {
//										FileInputStream fin_acc = new FileInputStream(
//												"/sdcard/BillKeeperData/"
//														+ "billkeepercategory.txt");
//										int length = fin_acc.available();
//										
//										DBUtility.deleteCategory();
//										if (length>0) {
//											byte[] buffer = new byte[length];
//											fin_acc.read(buffer);
//											res_category = EncodingUtils.getString(
//													buffer, "UTF-8");
//											fin_acc.close();
//											array_category = res_category.split(",����@");
//											Log.v("mmes", "array_category "+array_category);
//											DBUtility.insertCategory(array_category);
//										}
//										
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									
//									String res_account = "";
//									String[] array_account = null;
//
//									try {
//										FileInputStream fin_acc = new FileInputStream(
//												"/sdcard/BillKeeperData/"
//														+ "billkeeperaccount.txt");
//										int length = fin_acc.available();
//										
//										DBUtility.deleteAccount();
//										if (length>0) {
//											byte[] buffer = new byte[length];
//											fin_acc.read(buffer);
//											res_account = EncodingUtils.getString(
//													buffer, "UTF-8");
//											fin_acc.close();
//											array_account = res_account.split(",����@");
//											Log.v("mmes", "array_acount "+array_account);
//											
//											DBUtility.insertAccount(array_account);
//										}
//										
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									
//									String res_bill = "";
//									String[] array_bill = null;
//									
//									try {
//										FileInputStream fin_acc = new FileInputStream(
//												"/sdcard/BillKeeperData/"
//														+ "billkeeperbill.txt");
//										int length = fin_acc.available();
//										
//										DBUtility.deleteBill();
//										if (length>0) {
//											byte[] buffer = new byte[length];
//											fin_acc.read(buffer);
//											res_bill = EncodingUtils.getString(
//													buffer, "UTF-8");
//											fin_acc.close();
//											array_bill = res_bill.split(",����@");
//											DBUtility.insertBill(array_bill);
//											
//											Log.v("mmes", "array_bill "+array_bill);
//										}
//										
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									
//									String res_billo = "";
//									String[] array_billo = null;
//									
//									try {
//										FileInputStream fin_acc = new FileInputStream(
//												"/sdcard/BillKeeperData/"
//														+ "billkeeperbillo.txt");
//										int length = fin_acc.available();
//										
//										DBUtility.deleteBillO();
//										if (length>0) {
//											byte[] buffer = new byte[length];
//											fin_acc.read(buffer);
//											res_billo = EncodingUtils.getString(
//													buffer, "UTF-8");
//											fin_acc.close();
//											array_billo = res_billo.split(",����@");
//											DBUtility.insertBillO(array_billo);
//										}
//										
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									
//									String res_pay = "";
//									String[] array_pay = null;
//									
//									try {
//										FileInputStream fin_acc = new FileInputStream(
//												"/sdcard/BillKeeperData/"
//														+ "billkeeperpay.txt");
//										int length = fin_acc.available();
//										
//										DBUtility.deletePayment();
//										if (length>0) {
//											byte[] buffer = new byte[length];
//											fin_acc.read(buffer);
//											res_pay = EncodingUtils.getString(
//													buffer, "UTF-8");
//											fin_acc.close();
//											array_pay = res_pay.split(",����@");
//											DBUtility.insertPay(array_pay);
//										}
//										
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									
//									
//									File file = new File(
//											"/sdcard/BillKeeperData/");
//									if (!file.exists()) {
//										try {
//											file.mkdirs();
//											System.out.println("OK");
//										} catch (Exception e) {
//											// TODO: handle exception
//											System.out.println(e + "asdasd");
//										}
//									}
//									File path;
//									path = new File("/sdcard/BillKeeperData/");
//									File dir = null;
//									File dir1 = null;
//									File dir2 = null;
//									File dir3 = null;
//									File dir4 = null;
//
//									dir = new File(path, "billkeeperaccount.txt");
//									dir1 = new File(path,"billkeeperbill.txt");
//									dir2 = new File(path, "billkeeperbillo.txt");
//									dir3 = new File(path,"billkeepercategory.txt");
//									dir4 = new File(path, "billkeeperpay.txt");
//									
//									if (!dir.exists()) {
//										try {
//
//										} catch (Exception e) {
//
//										}
//									} else {
//										dir.delete();
//
//									}
//									if (!dir1.exists()) {
//										try {
//
//										} catch (Exception e) {
//
//										}
//									} else {
//										dir1.delete();
//
//									}
//
//									if (!dir2.exists()) {
//										try {
//
//										} catch (Exception e) {
//
//										}
//									} else {
//										dir2.delete();
//
//									}
//									
//									if (!dir3.exists()) {
//										try {
//
//										} catch (Exception e) {
//
//										}
//									} else {
//										dir3.delete();
//
//									}
//
//									if (!dir4.exists()) {
//										try {
//
//										} catch (Exception e) {
//
//										}
//									} else {
//										dir4.delete();
//
//									}
//
//									finish();
//
//								}
//
//							}).create();
//			alertDialog.show();
//			alertDialog.setCanceledOnTouchOutside(false);
//		
//	}
//	
//	
//	
//	
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		getMenuInflater().inflate(R.menu.first_start_menu, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		
//		if (DBUtility != null) {
//			DBUtility.close();
//		}
//
//	}
//
//
//	OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
//		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//				return true;
//			} else {
//				return false;
//			}
//		}
//	};
//
//}
