package com.appxy.pocketexpensepro.bills;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.Sampler;
import android.util.Log;

public class RecurringEventBE {

	public final static long DAYMILLIS = 86400000L;

	public static List<Map<String, Object>> recurringData(Context context,
			long start, long end) { // 重复事件的模板处理，生成虚拟事件(根据日期段)
		 long a = System.currentTimeMillis();
		List<Map<String, Object>> finalDataList = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> tDataList = BillsDao.selectTemplateBillRuleByBE(context);
		for (Map<String, Object> iMap : tDataList) {

			int _id = (Integer) iMap.get("_id");
			long bk_billDuedate = (Long) iMap.get("ep_billDueDate"); // 相当于事件的开始日期
			long bk_billEndDate = (Long) iMap.get("ep_billEndDate"); // 重复事件的截止日期
			int bk_billRepeatType = (Integer) iMap.get("ep_recurringType");

			long startDate = 0; // 进一步精确判断日记起止点，保证了该段时间断获取的数据不未空，减少不必要的处理
			long endDate = 0;

			startDate = (bk_billDuedate <= start) ? start : bk_billDuedate; // 进一步判断日记起止点，这样就保证了该段时间断获取的数据不未空
			if (bk_billEndDate == -1) { // 永远重复事件的处理

				if (end >= bk_billDuedate) {
					endDate = end;
				}

			} else {

				if (start <= bk_billEndDate && end >= bk_billDuedate) { // 首先判断起止时间是否落在重复区间，表示该段时间有重复事件
					endDate = (bk_billEndDate >= end) ? end : bk_billEndDate;
				}
			}

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(bk_billDuedate); // 设置重复的开始日期

			long virtualLong = bk_billDuedate; // 虚拟时间，后面根据规则累加计算
			List<Map<String, Object>> virtualDataList = new ArrayList<Map<String, Object>>();// 虚拟事件

			if (virtualLong == startDate) { // 所要求的时间,小于等于父本时间，说明这个是父事件数据，即第一条父本数据

				Map<String, Object> bMap = new HashMap<String, Object>();
				bMap.putAll(iMap);
				bMap.put("indexflag", 1); // 1表示父本事件
				virtualDataList.add(bMap);
			}

			long before_times = 0; // 计算从要求时间start到重复开始时间的次数,用于定位第一次发生在请求时间段落的时间点
			long remainder = -1;
			if (bk_billRepeatType == 1) {

				before_times = (startDate - bk_billDuedate) / (7 * DAYMILLIS);
				remainder = (startDate - bk_billDuedate) % (7 * DAYMILLIS);

			} else if (bk_billRepeatType == 2) {

				before_times = (startDate - bk_billDuedate) / (14 * DAYMILLIS);
				remainder = (startDate - bk_billDuedate) % (14 * DAYMILLIS);

			} else if (bk_billRepeatType == 3) {

				before_times = (startDate - bk_billDuedate) / (28 * DAYMILLIS);
				remainder = (startDate - bk_billDuedate) % (28 * DAYMILLIS);

			} else if (bk_billRepeatType == 4) {

				before_times = (startDate - bk_billDuedate) / (15 * DAYMILLIS);
				remainder = (startDate - bk_billDuedate) % (15 * DAYMILLIS);

			} else if (bk_billRepeatType == 5) {

				do { // 该段代码根据日历处理每天重复事件，当事件比较多的时候效率比较低

					Calendar calendarCloneCalendar = (Calendar) calendar
							.clone();
					int currentMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);
					calendarCloneCalendar.add(Calendar.MONTH, 1);
					int nextMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);

					if (currentMonthDay > nextMonthDay) {
						calendar.add(Calendar.MONTH, 1 + 1);
						virtualLong = calendar.getTimeInMillis();
					} else {
						calendar.add(Calendar.MONTH, 1);
						virtualLong = calendar.getTimeInMillis();
					}

				} while (virtualLong < startDate);

			} else if (bk_billRepeatType == 6) {

				do { // 该段代码根据日历处理每天重复事件，当事件比较多的时候效率比较低

					Calendar calendarCloneCalendar = (Calendar) calendar
							.clone();
					int currentMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);
					calendarCloneCalendar.add(Calendar.MONTH, 2);
					int nextMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);

					if (currentMonthDay > nextMonthDay) {
						calendar.add(Calendar.MONTH, 2 + 2);
						virtualLong = calendar.getTimeInMillis();
					} else {
						calendar.add(Calendar.MONTH, 2);
						virtualLong = calendar.getTimeInMillis();
					}

				} while (virtualLong < startDate);

			} else if (bk_billRepeatType == 7) {

				do { // 该段代码根据日历处理每天重复事件，当事件比较多的时候效率比较低

					Calendar calendarCloneCalendar = (Calendar) calendar
							.clone();
					int currentMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);
					calendarCloneCalendar.add(Calendar.MONTH, 3);
					int nextMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);

					if (currentMonthDay > nextMonthDay) {
						calendar.add(Calendar.MONTH, 3 + 3);
						virtualLong = calendar.getTimeInMillis();
					} else {
						calendar.add(Calendar.MONTH, 3);
						virtualLong = calendar.getTimeInMillis();
					}

				} while (virtualLong < startDate);

			} else if (bk_billRepeatType == 8) {

				do {
					calendar.add(Calendar.YEAR, 1);
					virtualLong = calendar.getTimeInMillis();
				} while (virtualLong < startDate);

			}

			if (remainder == 0 && virtualLong != startDate) { // 当整除的时候，说明当月的第一天也是虚拟事件，判断排除为父本，然后添加。不处理,一个月第一天事件会丢失
				before_times = before_times - 1;
			}

			if (bk_billRepeatType == 1) { // 单独处理天事件，计算出第一次出现在时间段的事件时间

				virtualLong = bk_billDuedate + (before_times + 1) * 7
						* (DAYMILLIS);
				calendar.setTimeInMillis(virtualLong);

			} else if (bk_billRepeatType == 2) {

				virtualLong = bk_billDuedate + (before_times + 1) * (2 * 7)
						* DAYMILLIS;
				calendar.setTimeInMillis(virtualLong);
			} else if (bk_billRepeatType == 3) {

				virtualLong = bk_billDuedate + (before_times + 1) * (4 * 7)
						* DAYMILLIS;
				calendar.setTimeInMillis(virtualLong);
			} else if (bk_billRepeatType == 4) {

				virtualLong = bk_billDuedate + (before_times + 1) * (15)
						* DAYMILLIS;
				calendar.setTimeInMillis(virtualLong);
			}

			while (startDate <= virtualLong && virtualLong <= endDate) { // 插入虚拟事件
				Map<String, Object> bMap = new HashMap<String, Object>();
				bMap.putAll(iMap);
				bMap.put("ep_billDueDate", virtualLong);
				bMap.put("indexflag", 2); // 2表示虚拟事件
				virtualDataList.add(bMap);

				if (bk_billRepeatType == 1) {

					calendar.add(Calendar.DAY_OF_MONTH, 7);

				} else if (bk_billRepeatType == 2) {

					calendar.add(Calendar.DAY_OF_MONTH, 2 * 7);

				} else if (bk_billRepeatType == 3) {

					calendar.add(Calendar.DAY_OF_MONTH, 4 * 7);

				} else if (bk_billRepeatType == 4) {

					calendar.add(Calendar.DAY_OF_MONTH, 15);

				} else if (bk_billRepeatType == 5) {

					Calendar calendarCloneCalendar = (Calendar) calendar
							.clone();
					int currentMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);
					calendarCloneCalendar.add(Calendar.MONTH,
							1);
					int nextMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);

					if (currentMonthDay > nextMonthDay) {
						calendar.add(Calendar.MONTH, 1
								+ 1);
					} else {
						calendar.add(Calendar.MONTH, 1);
					}

				}else if (bk_billRepeatType == 6) {

					Calendar calendarCloneCalendar = (Calendar) calendar
							.clone();
					int currentMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);
					calendarCloneCalendar.add(Calendar.MONTH,
							2);
					int nextMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);

					if (currentMonthDay > nextMonthDay) {
						calendar.add(Calendar.MONTH, 2
								+ 2);
					} else {
						calendar.add(Calendar.MONTH, 2);
					}

				}else if (bk_billRepeatType == 7) {

					Calendar calendarCloneCalendar = (Calendar) calendar
							.clone();
					int currentMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);
					calendarCloneCalendar.add(Calendar.MONTH,
							3);
					int nextMonthDay = calendarCloneCalendar
							.get(Calendar.DAY_OF_MONTH);

					if (currentMonthDay > nextMonthDay) {
						calendar.add(Calendar.MONTH, 3
								+ 3);
					} else {
						calendar.add(Calendar.MONTH, 3);
					}

				} else if (bk_billRepeatType == 8) {

					calendar.add(Calendar.YEAR, 1);

				}
				virtualLong = calendar.getTimeInMillis();

			}

			finalDataList.addAll(virtualDataList);

		}// 遍历模板结束，产生结果为一个父本加若干虚事件的list

		/*
		 * 开始处理重复特例事件特例事件，并且来时合并
		 */
		List<Map<String, Object>>oDataList = BillsDao.selectBillItemByBE(context, start, end);
		Log.v("mtest", "特例结果大小" + oDataList.size());

		List<Map<String, Object>> delectDataListf = new ArrayList<Map<String, Object>>(); // finalDataList要删除的结果
		List<Map<String, Object>> delectDataListO = new ArrayList<Map<String, Object>>(); // oDataList要删除的结果

		for (Map<String, Object> fMap : finalDataList) { // 遍历虚拟事件

			int pbill_id = (Integer) fMap.get("_id");
			long pdue_date = (Long) fMap.get("ep_billDueDate");

			for (Map<String, Object> oMap : oDataList) {

				int cbill_id = (Integer) oMap.get("billItemHasBillRule");
				long cdue_date = (Long) oMap.get("ep_billDueDate");
				int bk_billsDelete = (Integer) oMap.get("ep_billisDelete");

				if (cbill_id == pbill_id) {

					if (bk_billsDelete == 2) {// 改变了duedate的特殊事件
						long old_due = (Long) oMap.get("ep_billItemDueDateNew");

						if (old_due == pdue_date) {

							delectDataListf.add(fMap);//该改变事件在时间范围内，保留oMap

						}

					} else if (bk_billsDelete == 1) {

						if (cdue_date == pdue_date) {

							delectDataListf.add(fMap);
							delectDataListO.add(oMap);

						}

					} else {

						if (cdue_date == pdue_date) {
							delectDataListf.add(fMap);
						}

					}

				}
			}// 遍历特例事件结束

		}// 遍历虚拟事件结束
		// Log.v("mtest", "delectDataListf的大小"+delectDataListf.size());
		// Log.v("mtest", "delectDataListO的大小"+delectDataListO.size());
		finalDataList.removeAll(delectDataListf);
		oDataList.removeAll(delectDataListO);
		finalDataList.addAll(oDataList);
		List<Map<String, Object>> mOrdinaryList = BillsDao.selectOrdinaryBillRuleByBE(context, start, end);
		finalDataList.addAll(mOrdinaryList);
		// Log.v("mtest", "finalDataList的大小"+finalDataList.size());
		long b = System.currentTimeMillis();
		Log.v("mtest", "算法耗时"+(b-a));
		
		return finalDataList;
	}

}
