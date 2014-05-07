package com.appxy.pocketexpensepro.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class MEntity {

	private final static long DAYMILLIS = 86400000L;

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
		long lastDayOfWeek = getLastDayByFirst(firstDayOfWeek);

		if (choosedTime < firstDayOfWeek) {
			
			offset = (int) ((choosedTime - firstDayOfWeek) / (7 * DAYMILLIS));

			long remainder = (firstDayOfWeek - choosedTime) % (7 * DAYMILLIS);
			if (remainder > 0) {
				offset = offset - 1;
			}
			
		} else if (choosedTime > lastDayOfWeek) {

			offset = (int) ((choosedTime - lastDayOfWeek) / (7 * DAYMILLIS));

			long remainder = (choosedTime - lastDayOfWeek) % (7 * DAYMILLIS);
			if (remainder > 0) {
				offset = offset + 1;
			}

		} else {
			offset = 0;
		}

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
        Log.v("mtest", "endMonth"+endMonth);
        Log.v("mtest", "beginMonth"+beginMonth);
        int betMonth = (endYear-beginYear)*12+(endMonth-beginMonth);
        
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

}
