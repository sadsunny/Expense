package com.appxy.pocketexpensepro.entity;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Log;

public class MEntity {

	private final static long DAYMILLIS = 86400000L;

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if ((cm == null)) {
			return false;
		} else {
			try {
				return cm.getActiveNetworkInfo().isAvailable();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public static boolean pingDropBox( )  {
		String ip = "www.dropbox.com";
		Process p;
		try {
			p = Runtime.getRuntime().exec("ping -c 1 -w 1 " + ip);
			int status;
			
			try {
				status = p.waitFor();
				if (status == 0) {
					return true;
				} else {
					return false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	public static int calculateInSampleSize( // 根据图片的尺寸，返回一个合适的大小
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(String picPath,
			int reqWidth, int reqHeight) { // 照相图片 缩放较大的图片
		/*
		 * 第一步先计算出图片的尺寸
		 */

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picPath, options);

		// First decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(picPath, options);
	}

	public static String turnMilltoDate(long milliSeconds) {// 将毫秒转化成固定格式的年月日
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public static String[] TransactionRecurring = { "Never", "Daily", "Weekly",
			"Every 2 Weeks", "Every 3 Weeks", "Every 4 Weeks", "Semimonthly",
			"Monthly", "Every 2 Months", "Every 3 Months", "Every 4 Months",
			"Every 5 Months", "Every 6 Months", "Every Year" };

	public static int positionTransactionRecurring(String transactionRecurring) {
		int position = 0;
		for (int i = 0; i < TransactionRecurring.length; i++) {
			if (TransactionRecurring[i].equals(transactionRecurring)) {
				return i;
			}
		}
		return position;
	}

	public static String[] reminderTypeStrings = { "None", "1 day before",
			"2 days before", "3 days before", "1 week before",
			"2 weeks before", "on date of event" };

	public static int positionReminder(String reminder) {
		int position = 0;
		for (int i = 0; i < reminderTypeStrings.length; i++) {
			if (reminderTypeStrings[i].equals(reminder)) {
				return i;
			}
		}
		return position;
	}

	public static String reminderDate(int remindDate) { // 转换提前提醒
		String before = "None";
		if (remindDate == 0) {
			before = "None";
		} else if (remindDate == 1) {
			before = "1 day before";
		} else if (remindDate == 2) {
			before = "2 days before";
		} else if (remindDate == 3) {
			before = "3 days before";
		} else if (remindDate == 4) {
			before = "1 week before";
		} else if (remindDate == 5) {
			before = "2 weeks before";
		} else if (remindDate == 6) {
			before = "on date of event";
		}
		return before;
	}

	public static int positionRecurring(String recurringtype) {
		int position = 0;
		for (int i = 0; i < recurringTypeStrings.length; i++) {
			if (recurringTypeStrings[i].equals(recurringtype)) {
				return i;
			}
		}
		return position;
	}

	public static String[] recurringTypeStrings = { "Never", "Weekly",
			"Every 2 Weeks", "Every 4 Weeks", "Semimonthly", "Monthly",
			"Every 2 Months", "Every 3 Months", "Every Year" };

	public static String turnTorecurring(int remindDate) { // 转换提前提醒
		String recurring = "Never";
		if (remindDate == 0) {
			recurring = "Never";
		} else if (remindDate == 1) {
			recurring = "Weekly";
		} else if (remindDate == 2) {
			recurring = "Every 2 Weeks";
		} else if (remindDate == 3) {
			recurring = "Every 4 Weeks";
		} else if (remindDate == 4) {
			recurring = "Semimonthly";
		} else if (remindDate == 5) {
			recurring = "Monthly";
		} else if (remindDate == 6) {
			recurring = "Every 2 Months";
		} else if (remindDate == 7) {
			recurring = "Every 3 Months";
		} else if (remindDate == 8) {
			recurring = "Every Year";
		}
		return recurring;
	}

	public static Date getMilltoDropBox(long milliSeconds) {// 将毫秒转化成固定格式的年月日
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EE MM dd yyyy HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		Date date = new Date();
		try {
			date = (Date) formatter.parseObject(formatter.format(calendar
					.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static Date getMilltoDateFormat(long milliSeconds) {// 将毫秒转化成Date

		if (milliSeconds == -1) {
			return null;
		}
		Date date = new Date();
		date.setTime(milliSeconds);
		return date;
	}

	public static String getUUID() { // 获取随机的唯一ID

		UUID uuid = UUID.randomUUID();
		String uniqueId = uuid.toString();
		return uniqueId;

	}

	public static String sqliteEscape(String keyWord) {
		keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		keyWord = keyWord.replace("[", "/[");
		keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}

	public static long getHMSMill() { // 获取当前时间的时分秒

		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minuts = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		long returnDate = hours * 60 * 60 * 1000 + minuts * 60 * 1000 + second
				* 1000;
		return returnDate;
	}

	public static class MapComparatorTime implements
			Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub

			long due1 = (Long) o1.get("dateTime");
			long due2 = (Long) o2.get("dateTime");
			if (due1 > due2) {
				return -1;
			} else if (due1 < due2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public static long getMilltoDate(String date) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(new SimpleDateFormat("MMMM, yyyy").parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return calendar.getTimeInMillis();
		}
		return calendar.getTimeInMillis();
	}

	public static class MapComparatorGroupTime implements // sort Group list
			Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			// TODO Auto-generated method stub

			long due1 = getMilltoDate(o1);
			long due2 = getMilltoDate(o2);
			if (due1 > due2) {
				return -1;
			} else if (due1 < due2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public static class MapComparatorTimeDesc implements
			Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			long due1 = (Long) o1.get("dateTime");
			long due2 = (Long) o2.get("dateTime");
			if (due1 > due2) {
				return 1;
			} else if (due1 < due2) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public static class MapComparator implements
			Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			long due1 = (Long) o1.get("ep_billDueDate");
			long due2 = (Long) o2.get("ep_billDueDate");
			if (due1 > due2) {
				return 1;
			} else if (due1 < due2) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public static class MapComparatorAmount implements
			Comparator<Map<String, Object>> {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			double due1 = (Double) o1.get("sum");
			double due2 = (Double) o2.get("sum");
			if (due1 > due2) {
				return -1;
			} else if (due1 < due2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static String turnToDateString(long mills) {

		Date date2 = new Date(mills);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		String theDate = sdf.format(date2);
		return theDate;
	}

	public static String doublepoint2str(String num) {
		BigDecimal bg = new BigDecimal(num);
		double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		DecimalFormat df = new DecimalFormat("###,##0.00");
		return df.format(f1);
	}

	public static String doubl2str(String num) {
		BigDecimal bg = new BigDecimal(num);
		double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(f1);
	}

	public static long getFirstDayOfMonthMillis(long mills) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(mills);

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());
		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	public static long getLastDayOfMonthMillis(long mills) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(mills);

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));

		String dateTime = new SimpleDateFormat("MM-dd-yyyy").format(cal
				.getTime());

		Calendar c = Calendar.getInstance();

		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c.getTimeInMillis();
	}

	public static String getMilltoDate(long milliSeconds) {// 将毫秒转化成固定格式的年月日
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public static long getFirstDayByOffset(int offset) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		long returnDate = calendar.getTimeInMillis();

		long offsetDate = offset * 7 * DAYMILLIS;
		calendar.setTimeInMillis(returnDate + offsetDate);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		returnDate = calendar.getTimeInMillis();

		return returnDate;
	}

	public static long getLastDayByFirst(long firstDayOfWeek) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(firstDayOfWeek);

		long returnDate = calendar.getTimeInMillis();

		long offsetDate = 7 * DAYMILLIS;
		calendar.setTimeInMillis(returnDate + offsetDate);

		return calendar.getTimeInMillis();
	}

	public static long getFirstDayByTime(long theDayTime) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(theDayTime);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

		return calendar.getTimeInMillis();
	}

	public static int getWeekOffsetByDay(long choosedTime, long beginTime) { // 根据beginTime计算到choosedTime的offset

		int offset = 0;
		long firstDayOfWeek = getFirstDayByTime(beginTime);
		long lastDayOfWeek = getFirstDayByTime(choosedTime);
		offset = (int) ((lastDayOfWeek - firstDayOfWeek) / (7 * DAYMILLIS));

		return offset;
	}

	public static long getMonthByOffset(int offset) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		long returnDate = calendar.getTimeInMillis();

		return MEntity.getFirstDayOfMonthMillis(returnDate);
	}

	public static int getMonthOffsetByDay(long beginTime, long endTime) { // 根据beginTime计算到choosedTime的offset

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(beginTime);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(endTime);

		int beginYear = calendar.get(Calendar.YEAR);
		int beginMonth = calendar.get(Calendar.MONTH);

		int endYear = calendar2.get(Calendar.YEAR);
		int endMonth = calendar2.get(Calendar.MONTH);
		Log.v("mtest", "endMonth" + endMonth);
		Log.v("mtest", "beginMonth" + beginMonth);
		int betMonth = (endYear - beginYear) * 12 + (endMonth - beginMonth);

		return betMonth;
	}

	public static long getNowMillis() {
		Date date1 = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String nowTime = formatter.format(date1);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("MM-dd-yyyy").parse(nowTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long nowMillis = c.getTimeInMillis();

		return nowMillis;
	}

	public static String turnMilltoMonthYear(long milliSeconds) {// 将毫秒转化成固定格式的年月日
		SimpleDateFormat formatter = new SimpleDateFormat("MMM, yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public static long getFirstMonthByOffset(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, -3 + offset * 7);

		return calendar.getTimeInMillis();
	}

	public static int getOffsetByMonth(long month) {
		Calendar calendar = Calendar.getInstance();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(month);

		int beginYear = calendar.get(Calendar.YEAR);
		int beginMonth = calendar.get(Calendar.MONTH);

		int endYear = calendar2.get(Calendar.YEAR);
		int endMonth = calendar2.get(Calendar.MONTH);

		if (endYear < beginYear) {
			calendar.add(Calendar.MONTH, 3);
		} else if (endYear > beginYear) {
			calendar.add(Calendar.MONTH, -3);
		} else {
			if (endMonth < beginMonth) {
				calendar.add(Calendar.MONTH, 3);
			} else if (endMonth > beginMonth) {
				calendar.add(Calendar.MONTH, -3);
			}
		}

		beginMonth = calendar.get(Calendar.MONTH);

		int offset = ((endYear - beginYear) * 12 + (endMonth - beginMonth)) / 7;

		return offset;
	}

}
