//package com.appxy.pocketexpensepro.bills;
//
//import java.net.ContentHandler;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import com.appxy.pocketexpensepro.db.ExpenseDBHelper;
//
//import android.R.integer;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.renderscript.Sampler;
//import android.util.Log;
//
//
//public class RecurringEvent {
//	
//	public  List<Map<String, Object>> oDataList = new ArrayList<Map<String, Object>>();
//	public  List<Map<String, Object>> virtualDataList; 
//	public final static long DAYMILLIS = 86400000L;
//	
//	public static SQLiteDatabase getConnection(Context context) {
//		ExpenseDBHelper helper = new ExpenseDBHelper(context);
//		SQLiteDatabase db = helper.getWritableDatabase();
//		return db;
//	}
//	
//	public static List<Map<String, Object>> recurringData(Context context,long start,long end) { //�ظ��¼���ģ�崦�?��������¼�(������ڶ�)
//		
//		getTemplateDate(context);
//		List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();
//		
//		for ( Map<String, Object> iMap : tDataList) {
//			
//			 int BK_Bill_Id = (Integer)iMap.get("BK_Bill_Id");
//	         double billamount = (Double)iMap.get("nbillamount");
//	         int bk_billAmountUnknown = (Integer)iMap.get("nbk_billAmountUnknown");
//	         int bk_billAutoPay = (Integer)iMap.get("nbk_billAutoPay");
//	         long bk_billDuedate = (Long)iMap.get("nbk_billDuedate"); //�൱���¼��Ŀ�ʼ����
//	         long bk_billEndDate =  (Long)iMap.get("nbk_billEndDate"); //�ظ��¼��Ľ�ֹ����
//	         int bk_billisReminder = (Integer)iMap.get("nbk_billisReminder");
//	         int bk_billisRepeat = (Integer)iMap.get("nbk_billisRepeat");
//	         int bk_billisVariaable = (Integer)iMap.get("bk_billisVariaable");
//	         long bk_billReminderDate = (Long)iMap.get("nbk_billReminderDate");
//	         long bk_billReminderTime = (Long)iMap.get("bk_billReminderTime");
//	         int bk_billRepeatNumber = (Integer)iMap.get("nbk_billRepeatNumber"); 
//	         int bk_billRepeatType = (Integer)iMap.get("nbk_billRepeatType"); 
//	         int billhasAccount = (Integer)iMap.get("billhasAccount");
//	         String bk_accountName = (String)iMap.get("bk_accountName");
//	         int accounthasCategory = (Integer)iMap.get("accounthasCategory");
//	         int payState = (Integer)iMap.get("payState");
//	         int bk_categoryIconName = (Integer)iMap.get("bk_categoryIconName");
//	         String bk_categoryName = (String)iMap.get("bk_categoryName");
//	         int BK_CategoryId = (Integer)iMap.get("BK_CategoryId");
//	         
//	         long startDate = 0 ;  //��һ����ȷ�ж��ռ���ֹ�㣬��֤�˸ö�ʱ��ϻ�ȡ����ݲ�δ�գ����ٲ���Ҫ�Ĵ���
//    		 long endDate = 0;
//
//	         if ( bk_billEndDate == -1) { //��Զ�ظ��¼��Ĵ���
//	        	 
//	        	 if (end >= bk_billDuedate) {
//	        		  startDate = (bk_billDuedate <= start)? start:bk_billDuedate;  //��һ���ж��ռ���ֹ�㣬����ͱ�֤�˸ö�ʱ��ϻ�ȡ����ݲ�δ��
//	        		  endDate = end;
//				}
//				
//			} else {
//				
//				 if (start <= bk_billEndDate && end >= bk_billDuedate) { //�����ж���ֹʱ���Ƿ������ظ���䣬��ʾ�ö�ʱ�����ظ��¼�
//		        	 
//		        	  startDate = (bk_billDuedate <= start)? start:bk_billDuedate;  //��һ���ж��ռ���ֹ�㣬����ͱ�֤�˸ö�ʱ��ϻ�ȡ����ݲ�δ��
//		 	          endDate = (bk_billEndDate >= end )? end:bk_billEndDate; 
//				} 
//			}
//	         
//	         Calendar calendar = Calendar.getInstance();
// 	         calendar.setTimeInMillis(bk_billDuedate); //�����ظ��Ŀ�ʼ����
// 	         
// 	         long virtualLong = bk_billDuedate;  //����ʱ�䣬�����ݹ����ۼӼ���
// 	         virtualDataList = new ArrayList<Map<String, Object>>();
// 	         Map<String, Object> bMap; 
// 	         
// 	         if (virtualLong == startDate) {     //��Ҫ���ʱ��,С�ڵ��ڸ���ʱ�䣬˵������Ǹ��¼���ݣ�����һ���������
// 	        	 
// 	        	bMap = new HashMap<String, Object>();
// 	        	bMap.put("BK_Bill_Id", BK_Bill_Id);  
// 	            bMap.put("nbillamount", billamount);
// 	            bMap.put("nbk_billAmountUnknown", bk_billAmountUnknown);
// 	            bMap.put("nbk_billAutoPay", bk_billAutoPay);
// 	            bMap.put("nbk_billDuedate", bk_billDuedate);
// 	            bMap.put("nbk_billEndDate", bk_billEndDate);
// 	            bMap.put("nbk_billisReminder", bk_billisReminder);
// 	            bMap.put("nbk_billisRepeat", bk_billisRepeat);
// 	            bMap.put("bk_billisVariaable", bk_billisVariaable);
// 	            bMap.put("nbk_billReminderDate", bk_billReminderDate);
// 	            bMap.put("bk_billReminderTime", bk_billReminderTime);
// 	            bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
// 	            bMap.put("nbk_billRepeatType", bk_billRepeatType);
// 	            bMap.put("accounthasCategory", accounthasCategory);
// 	            bMap.put("billhasAccount", billhasAccount);
// 	            bMap.put("bk_accountName", bk_accountName);
// 	            bMap.put("bk_categoryIconName", bk_categoryIconName);
//	            bMap.put("bk_categoryName", bk_categoryName);
// 	            bMap.put("BK_CategoryId", BK_CategoryId);
// 	            bMap.put("payState", payState);
// 	            bMap.put("indexflag", 1);       //1��ʾ�����¼�
// 	            virtualDataList.add(bMap);
//			}
// 	       
// 	      long before_times =0; //�����Ҫ��ʱ��start���ظ���ʼʱ��Ĵ���,���ڶ�λ��һ�η���������ʱ������ʱ���
// 	      long remainder = -1;
// 	           if ( bk_billRepeatType == 1 ) {
//				 
// 	        	   if (bk_billRepeatNumber!=0) {
// 	        		  before_times = (startDate - bk_billDuedate)/(bk_billRepeatNumber*DAYMILLIS);
// 	 	    	     remainder = (startDate - bk_billDuedate)%(bk_billRepeatNumber*DAYMILLIS);
//				 }
// 	    	    
//				} else if (bk_billRepeatType == 2) {
//					
//					if (bk_billRepeatNumber!=0) {
//					 before_times = (startDate - bk_billDuedate)/(bk_billRepeatNumber*7*DAYMILLIS);
//					 remainder = (startDate - bk_billDuedate)%(bk_billRepeatNumber*7*DAYMILLIS);
//					}
//			     
//				} else if (bk_billRepeatType == 3) {
//					
//					 do { //�öδ�����������ÿ���ظ��¼������¼��Ƚ϶��ʱ��Ч�ʱȽϵ�
//						 
//						 Calendar calendarCloneCalendar =  (Calendar) calendar.clone();
//						 int currentMonthDay = calendarCloneCalendar.get(Calendar.DAY_OF_MONTH);
//						 calendarCloneCalendar.add(Calendar.MONTH,bk_billRepeatNumber);
//						 int nextMonthDay = calendarCloneCalendar.get(Calendar.DAY_OF_MONTH);
//						 
//						 if (currentMonthDay > nextMonthDay) {
//							 calendar.add(Calendar.MONTH, bk_billRepeatNumber+bk_billRepeatNumber);
//							 virtualLong = calendar.getTimeInMillis();
//						} else {
//							 calendar.add(Calendar.MONTH, bk_billRepeatNumber);
//							 virtualLong = calendar.getTimeInMillis();
//						}
//						
//					}while (virtualLong < startDate);
//					 
//				}else if (bk_billRepeatType == 4) {
//					
//					do { 
//						calendar.add(Calendar.YEAR, bk_billRepeatNumber);
//						virtualLong = calendar.getTimeInMillis();
//					}while (virtualLong < startDate);
//					
//				}
//				
// 	         if (remainder == 0 && virtualLong != startDate) { //������ʱ��˵�����µĵ�һ��Ҳ�������¼����ж��ų�Ϊ������Ȼ����ӡ�������,һ���µ�һ���¼��ᶪʧ
//  	    		 before_times = before_times -1;
// 			}
// 	         
// 	      
// 	      if (bk_billRepeatType == 1 ) { //�����������¼����������һ�γ�����ʱ��ε��¼�ʱ��
// 	    	  
// 	    	 virtualLong = bk_billDuedate + (before_times+1)*bk_billRepeatNumber*(DAYMILLIS) ;
// 	    	 calendar.setTimeInMillis(virtualLong);
// 	    	 
//		 }else if (bk_billRepeatType == 2) {
//			 
//			 virtualLong = bk_billDuedate + (before_times+1)*(bk_billRepeatNumber*7)*DAYMILLIS;
//			 calendar.setTimeInMillis(virtualLong);
//		}
// 	       
// 	        while(startDate <=virtualLong  &&  virtualLong <= endDate){  //���������¼�
// 	        	bMap = new HashMap<String, Object>();
// 	        	
// 	        	if (bk_billisVariaable == 1) { //�ظ�����Ƿ�̶�
// 	        		 bMap.put("nbillamount", 0.00);
// 	 	             bMap.put("nbk_billAmountUnknown", 1);
//				} else {
//					 bMap.put("nbillamount", billamount);
//		 	         bMap.put("nbk_billAmountUnknown", bk_billAmountUnknown);
//				}
// 	        	
// 	        	bMap.put("BK_Bill_Id", BK_Bill_Id);  
// 	            bMap.put("nbk_billAutoPay", bk_billAutoPay);
// 	            bMap.put("nbk_billDuedate", virtualLong);
// 	            bMap.put("nbk_billEndDate", bk_billEndDate);
// 	            bMap.put("nbk_billisReminder", bk_billisReminder);
// 	            bMap.put("nbk_billisRepeat", bk_billisRepeat);
// 	            bMap.put("bk_billisVariaable", bk_billisVariaable);
// 	            bMap.put("nbk_billReminderDate", bk_billReminderDate);
// 	            bMap.put("bk_billReminderTime", bk_billReminderTime);
// 	            bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
// 	            bMap.put("nbk_billRepeatType", bk_billRepeatType);
// 	            bMap.put("accounthasCategory", accounthasCategory);
// 	            bMap.put("billhasAccount", billhasAccount);
// 	            bMap.put("bk_accountName", bk_accountName);	 
// 	            bMap.put("bk_categoryIconName", bk_categoryIconName);
//	            bMap.put("bk_categoryName", bk_categoryName);
//	            bMap.put("BK_CategoryId", BK_CategoryId);
// 	            bMap.put("payState", payState);
// 	            bMap.put("indexflag", 2);       //2��ʾ�����¼�
// 	            virtualDataList.add(bMap);
// 	            
// 	           if ( bk_billRepeatType == 1 ) {
//					 
//				 	calendar.add(Calendar.DAY_OF_MONTH, bk_billRepeatNumber);
//				 
//				} else if (bk_billRepeatType == 2) {
//					
//					calendar.add(Calendar.DAY_OF_MONTH, bk_billRepeatNumber*7);
//			     
//				} else if (bk_billRepeatType == 3) {
//					
//					 Calendar calendarCloneCalendar =  (Calendar) calendar.clone();
//					 int currentMonthDay = calendarCloneCalendar.get(Calendar.DAY_OF_MONTH);
//					 calendarCloneCalendar.add(Calendar.MONTH,bk_billRepeatNumber);
//					 int nextMonthDay = calendarCloneCalendar.get(Calendar.DAY_OF_MONTH);
//					 
//					 if (currentMonthDay > nextMonthDay) {
//						 calendar.add(Calendar.MONTH, bk_billRepeatNumber+bk_billRepeatNumber);
//					} else {
//						 calendar.add(Calendar.MONTH, bk_billRepeatNumber);
//					}
//					
//				} else if (bk_billRepeatType == 4) {
//					
//					calendar.add(Calendar.YEAR, bk_billRepeatNumber);
//					
//				} 
//			 virtualLong = calendar.getTimeInMillis();
//			 
// 	        }
// 	       
//	         finalDataList.addAll(virtualDataList);
//	         
//		}//����ģ����������Ϊһ���������������¼���list
//		
//		   /*
//	        * ��ʼ�����ظ������¼������¼���������ʱ�ϲ�
//	        */
//	       getObjectDate(context,start,end);
//	       Log.v("mtest", "�������С"+oDataList.size());
//	       
//	       List<Map<String, Object>> delectDataListf = new ArrayList<Map<String, Object>>(); //finalDataListҪɾ��Ľ��
//	       List<Map<String, Object>> delectDataListO = new ArrayList<Map<String, Object>>(); //oDataListҪɾ��Ľ��
//	       
//	       for(Map<String, Object> fMap:finalDataList){ //���������¼�
//	    	   
//	    	   int pflag = (Integer) fMap.get("indexflag");
//	    	   int pbill_id = (Integer) fMap.get("BK_Bill_Id");
//	    	   long pdue_date =  (Long) fMap.get("nbk_billDuedate");
////	    	   if (pflag == 2) { //ֻ����flagΪ2�������¼�
//	    		   
//	    		   for (Map<String, Object> oMap:oDataList) {
//		    		   
//			    	   int cbill_id = (Integer) oMap.get("billObjecthasBill");
//			    	   long cdue_date =  (Long) oMap.get("nbk_billDuedate");
//			    	   int bk_billsDelete = (Integer)oMap.get("bk_billsDelete");
//			    	   
//			    	   if (cbill_id == pbill_id) {
//			    		   
//			    		   if (bk_billsDelete == 2) {//�ı���duedate�������¼�
//				    		   long old_due = (Long) oMap.get("bk_billDuedateNew");
//				    		   
//				    		   if ( old_due==pdue_date) {
//				    			   
//				    			   delectDataListf.add(fMap);
////				    			   delectDataListO.add(oMap); //����object�е����
//				    			   
//						 	    }
//				    		   
//			    		   }else if(bk_billsDelete == 1){
//			    			   
//								if (cdue_date==pdue_date) {
//									
//									   delectDataListf.add(fMap);
//					    			   delectDataListO.add(oMap);
//					    			   
//								}
//								
//							}else {
//								
//								if (cdue_date==pdue_date){
//									  delectDataListf.add(fMap);
//								}
//								
//							}
//								
//					} 
//	    		   }//���������¼�����
//	    		   
//	    		   
////	    	  }//if flag= 2 ����
//	  }//���������¼�����
////	       Log.v("mtest", "delectDataListf�Ĵ�С"+delectDataListf.size());
////	       Log.v("mtest", "delectDataListO�Ĵ�С"+delectDataListO.size());
//	       finalDataList.removeAll(delectDataListf);
//	       oDataList.removeAll(delectDataListO);
//	       finalDataList.addAll(oDataList);
////	       Log.v("mtest", "finalDataList�Ĵ�С"+finalDataList.size());
//	       
//		return finalDataList;
//	}
//	
//	public static List<Map<String, Object>> getTemplateDate(Context context){ //��ȡ�ظ��¼���ģ��Bill����
//		
//		List<Map<String, Object>> tDataList = new ArrayList<Map<String, Object>>();
//	    Map<String, Object> bMap;
//	    bksql = new BillKeeperSql(context); 
//	  	SQLiteDatabase db = bksql.getReadableDatabase();
//	  	String  sql = " select BK_Bill.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName,BK_Category._id from BK_Bill,BK_Account,BK_Category where BK_Bill.bk_billisRepeat = 1 and BK_Category._id =BK_Account.accounthasCategory and BK_Bill.billhasAccount = BK_Account._id";
//	  	
//	    Cursor cursorEA = db.rawQuery(sql, null);
//	    while (cursorEA.moveToNext()) {
//	    	
//	     bMap = new HashMap<String, Object>();
//	     int BK_Bill_Id =  cursorEA.getInt(0); 
//	     double billamount = cursorEA.getDouble(2);
//	     int bk_billAmountUnknown = cursorEA.getInt(3);
//         int bk_billAutoPay = cursorEA.getInt(4);
//         long bk_billDuedate = cursorEA.getLong(8);
//         long bk_billEndDate = cursorEA.getLong(9);
//         int bk_billisReminder = cursorEA.getInt(10);
//         int bk_billisRepeat = cursorEA.getInt(11);
//         int bk_billisVariaable = cursorEA.getInt(12);
//         long bk_billReminderDate = cursorEA.getLong(14);
//         long bk_billReminderTime = cursorEA.getLong(15);
//         int bk_billRepeatNumber = cursorEA.getInt(16);
//         int bk_billRepeatType = cursorEA.getInt(17);
//         int billhasAccount = cursorEA.getInt(24);
//         String bk_accountName = cursorEA.getString(25);
//         int accounthasCategory = cursorEA.getInt(26);
//         int bk_categoryIconName = cursorEA.getInt(27);
//         String bk_categoryName  = cursorEA.getString(28);
//         int BK_CategoryId = cursorEA.getInt(29);
//         
//         bMap.put("BK_Bill_Id", BK_Bill_Id);  
//         bMap.put("nbillamount", billamount);
//         bMap.put("nbk_billAmountUnknown", bk_billAmountUnknown);
//         bMap.put("nbk_billAutoPay", bk_billAutoPay);
//         bMap.put("nbk_billDuedate", bk_billDuedate);
//         bMap.put("nbk_billEndDate", bk_billEndDate);
//         bMap.put("nbk_billisReminder", bk_billisReminder);
//         bMap.put("nbk_billisRepeat", bk_billisRepeat);
//         bMap.put("bk_billisVariaable", bk_billisVariaable);
//         bMap.put("nbk_billReminderDate", bk_billReminderDate);
//         bMap.put("bk_billReminderTime", bk_billReminderTime);
//         bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
//         bMap.put("nbk_billRepeatType", bk_billRepeatType);
//         bMap.put("billhasAccount", billhasAccount);
//         bMap.put("bk_accountName", bk_accountName);
//         bMap.put("accounthasCategory", accounthasCategory);
//         bMap.put("bk_categoryIconName", bk_categoryIconName);
//         bMap.put("bk_categoryName", bk_categoryName);
//         bMap.put("BK_CategoryId", BK_CategoryId);
//         bMap.put("payState", -2); //��ӵ�pay״̬���
//         bMap.put("indexflag", 1); 
//         
//         tDataList.add(bMap);
//	    }
//	    Log.v("mtest", "ģ���С��"+tDataList.size());
//	    cursorEA.close();  
//	   db.close();
//	   
//	return tDataList;
// }
//	
//public static List<Map<String, Object>> getObjectDate(Context context , long start ,long end){ //��ȡ�ظ��¼�������BillObject����
//		
//	    oDataList.clear();
//	    Map<String, Object> bMap;
//	    bksql = new BillKeeperSql(context); 
//	  	SQLiteDatabase db = bksql.getReadableDatabase();
//	  	String  sql = "select BK_BillObject.* ,BK_Account.bk_accountName ,BK_Account.accounthasCategory ,BK_Category.bk_categoryIconName ,BK_Category.bk_categoryName,BK_Category._id from BK_BillObject ,BK_Account,BK_Category where BK_BillObject.billObjecthasAccount = BK_Account._id and BK_Category._id =BK_Account.accounthasCategory and BK_BillObject.bk_billODueDate >="+start+" and BK_BillObject.bk_billODueDate <= "+end;
//	  	
//	    Cursor cursorEA = db.rawQuery(sql, null);
//	    Log.v("mted","�����¼��Ĵ�С"+cursorEA.getCount() );
//	    while (cursorEA.moveToNext()) {
//	    	
//	     bMap = new HashMap<String, Object>();
//	     
//	     int BK_Bill_Id =  cursorEA.getInt(0); 
//	     int bk_billsDelete = cursorEA.getInt(1); 
//	     double billamount = cursorEA.getDouble(2);
//	     int bk_billAmountUnknown = cursorEA.getInt(3);
//         int bk_billAutoPay = cursorEA.getInt(4);
//         long bk_billDuedate = cursorEA.getLong(7);
//         long bk_billDuedateNew = cursorEA.getLong(8);
//         long bk_billEndDate = cursorEA.getLong(9);
//         int bk_billisReminder = cursorEA.getInt(10);
//         int bk_billisRepeat = cursorEA.getInt(11);
//         int bk_billisVariaable = cursorEA.getInt(12);
//         long bk_billReminderDate = cursorEA.getLong(14);
//         long bk_billReminderTime = cursorEA.getLong(15);
//         int bk_billRepeatNumber = cursorEA.getInt(16);
//         int bk_billRepeatType = cursorEA.getInt(17);
//         int billObjecthasBill = cursorEA.getInt(23);
//         int billhasAccount = cursorEA.getInt(24);
//         String bk_accountName = cursorEA.getString(25);
//         int accounthasCategory = cursorEA.getInt(26);
//         int bk_categoryIconName = cursorEA.getInt(27);
//         String bk_categoryName  = cursorEA.getString(28);
//         int BK_CategoryId = cursorEA.getInt(29);
//         
//         bMap.put("BK_Bill_Id", BK_Bill_Id); 
//         bMap.put("bk_billsDelete", bk_billsDelete);  //ֻ�������¼����У����ж�indexflag��Ȼ�����жϸ�ֵ: ���¼�deleteΪ0��ɾ��Ϊ1��2�ı���duedate���¼������ø�λ�õ����Ϊ�գ��ȱ�ʾΪdelete��
//         bMap.put("nbillamount", billamount);
//         bMap.put("nbk_billAmountUnknown", bk_billAmountUnknown);
//         bMap.put("nbk_billAutoPay", bk_billAutoPay);
//         bMap.put("nbk_billDuedate", bk_billDuedate);
//         bMap.put("bk_billDuedateNew", bk_billDuedateNew); //duedate���޸Ĳ��У��������ظ��¼��е�ԭʼλ�ã������ж�
//         bMap.put("nbk_billEndDate", bk_billEndDate);
//         bMap.put("nbk_billisReminder", bk_billisReminder);
//         bMap.put("nbk_billisRepeat", bk_billisRepeat);
//         bMap.put("bk_billisVariaable", bk_billisVariaable);
//         bMap.put("nbk_billReminderDate", bk_billReminderDate);
//         bMap.put("bk_billReminderTime", bk_billReminderTime);
//         bMap.put("nbk_billRepeatNumber", bk_billRepeatNumber);
//         bMap.put("nbk_billRepeatType", bk_billRepeatType);
//         bMap.put("bk_accountName", bk_accountName);
//         bMap.put("billObjecthasBill", billObjecthasBill); //�����¼���bill���
//         bMap.put("accounthasCategory", accounthasCategory);
//         bMap.put("billhasAccount", billhasAccount);
//         bMap.put("bk_categoryIconName", bk_categoryIconName);
//         bMap.put("bk_categoryName", bk_categoryName);
//         bMap.put("BK_CategoryId", BK_CategoryId);
//         bMap.put("payState", -2); //pay״̬���
//         bMap.put("indexflag", 3); 
//         
//         Log.v("mtest", "BK_Bill_Id��ֵ��"+BK_Bill_Id);
//         Log.v("mtest", "bk_billsDelete��ֵ��"+bk_billsDelete);
//         
//         oDataList.add(bMap);
//	    }
//	    Log.v("mtest", "�����С��"+oDataList.size());
//	    cursorEA.close();  
//	   db.close();
//	   
//	return oDataList;
// }
//
//
//}
