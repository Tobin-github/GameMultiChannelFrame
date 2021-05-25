/**
 * 
 */
package com.u8.server.sdk.coolpad.api;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

	// 注意：以下常量只能增加，不能修改  ////////////////////////////////////////////////
	//public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	//客户端生成的日期格式
	/**
	 * yyyy/MM/dd HH:mm:ss
	 */
	public static final String CLIENT_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	/**
	 * yyyy-MM-dd
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * yyyyMMdd
	 */
	public static final String FILE_DATE_FORMAT = "yyyyMMdd";
	/**
	 * HH:mm:ss
	 */
	public static final String TIME_FORMAT = "HH:mm:ss";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String STRING_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String STRING_DATE_FORMAT2 = "yyyy-MM-dd HH:mm";
	/**
	 * yyyy年MM月dd日 HH:mm:ss
	 */
	public static final String CHINESE_DATE_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
	
	public static final String CHINESE_DATE_FORMAT2 = "yyyy年MM月dd日";
	
	/**
	 * yyMMddHHmmss
	 */
	public static final String SEQUENCE_DATA_TIME = "yyMMddHHmmss";
	
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String RESP_DATE_FORMAT = "yyyyMMddHHmmss";//xujp,用于消息响应请求中的时间格式
	// 注意：以上常量只能增加，不能修改////////////////////////////////////////////////

	
	private DateUtil() {
	}

	public static Timestamp getSysTime() {
		Timestamp sys_time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return sys_time;
	}

	/** 
	 * 获取当前时间，格式2010-08-03 14:10:04
	 * @return
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(STRING_DATE_FORMAT);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * Convert string to Date
	 * 
	 * @return a java.util.Date object converted.
	 */
	public static Date stringToDate(String pstrValue, String pstrDateFormat) {
		if ((pstrValue == null) || (pstrValue.equals(""))) {
			return null;
		}
		Date dttDate = null;
		try {
			SimpleDateFormat oFormatter = new SimpleDateFormat(pstrDateFormat);
			dttDate = oFormatter.parse(pstrValue);
			oFormatter = null;
		} catch (Exception e) {
			return null;
		}

		return dttDate;
	}
	
	public static String dateToString(Date pdttValue){
		return dateToString(pdttValue,null);
	}

	/**
	 * Date convert to String.
	 * 
	 * @return String representation of the given Date and DateFormat.
	 */
	public static String dateToString(Date pdttValue, String pstrDateFormat) {
		String pstrDate = null; // return value
		if (pdttValue == null || "".equals(pdttValue)) {
			return null;
		}
		String formatStyle = null;
		if ((pstrDateFormat == null) || (pstrDateFormat.equals(""))) {
			formatStyle = DATE_FORMAT;
		} else {
			formatStyle = pstrDateFormat;
		}
		SimpleDateFormat oFormatter = new SimpleDateFormat(formatStyle);
		pstrDate = oFormatter.format(pdttValue);
		return pstrDate;
	}

	/***
	 * 生成现在的时间格式字体串
	 * @param pstrDateFormat
	 * @return
	 */
	public static String getCurDateToString(String pstrDateFormat) {
		String pstrDate = null; // return value
		Date curDate = new Date();
		String formatStyle = null;
		if ((pstrDateFormat == null) || (pstrDateFormat.equals(""))) {
			formatStyle = "yyyy-MM-dd";
		} else {
			formatStyle = pstrDateFormat;
		}
		SimpleDateFormat oFormatter = new SimpleDateFormat(formatStyle);
		pstrDate = oFormatter.format(curDate);
		return pstrDate;

	}

	/**
	 * DateTime convert to String.
	 * 
	 * @return String representation of the given DateTime and DateFormat.
	 */
	public static String dateTimeToString(Timestamp dt, String df) {
		String pstrDate = null; // return value
		if (dt == null) {
			return "";
		}
		String formatStyle = null;
		if ((df == null) || (df.equals(""))) {
			formatStyle = "yyyy-MM-dd HH:mm:ss";
		} else {
			formatStyle = df;
		}
		SimpleDateFormat oFormatter = null;
		try {
			oFormatter = new SimpleDateFormat(formatStyle);
			pstrDate = oFormatter.format(formatStyle);
		} catch (Exception e) {
			oFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			pstrDate = oFormatter.format(dt);
		}
		return pstrDate;
	}

	/**
	 * String convert to SQLDate.
	 * 
	 * @return a java.sql.Date representatio of the given date string and format
	 *         string.
	 */
	public static java.sql.Date stringToSQLDate(String pstrValue, String pstrDateFormat) throws ParseException {
		if ((pstrValue == null) || (pstrValue.equals(""))) {
			return null;
		}
		Date dttTempDate = stringToDate(pstrValue, pstrDateFormat);
		return new java.sql.Date(dttTempDate.getTime());
	}

	/**
	 * String convert to SQLTime.
	 * 
	 * @return a java.sql.Time representation of the given date string and
	 *         format string.
	 */
	public static java.sql.Time stringToSQLTime(String pstrValue, String pstrDateFormat) throws ParseException {
		if ((pstrValue == null) || (pstrValue.equals(""))) {
			return null;
		}
		Date dttTempDate = stringToDate(pstrValue, pstrDateFormat);
		return new java.sql.Time(dttTempDate.getTime());
	}

	/**
	 * String convert to SQLTimestamp.
	 * 
	 * @return a java.sql.Timestamp representation of the given date string and
	 *         format string.
	 */
	public static Timestamp stringToSQLTimestamp(String pstrValue, String pstrDateFormat)
			throws ParseException {
		if ((pstrValue == null) || (pstrValue.equals(""))) {
			return null;
		}
		Date dttTempDate = stringToDate(pstrValue, pstrDateFormat);
		return new Timestamp(dttTempDate.getTime());
	}

	/**
	 * Get current date in form of java.sql.Date.
	 * 
	 * @return java.sql.Date object of current date.
	 */
	public static java.sql.Date getCurSQLDate() {
		return new java.sql.Date(new Date().getTime());
	}

	/**
	 * Get current date in form of java.sql.Timestamp.
	 * 
	 * @return java.sql.Timestamp object of current date.
	 */
	public static Timestamp getCurSQLDateTime() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * Get current date in form of java.sql.Date and then convert it into
	 * String. The return date string follow that of the input param DateFormat.
	 * 
	 * @return String representation of current date.
	 */
	public static String getCurSQLDateInString(String pstrDateFormat) {
		String pstrDateTime = null;
		if ((pstrDateFormat != null) && (!pstrDateFormat.equals(""))) {
			Date curDateTime = new Date();
			java.sql.Date curSqlDateTime = new java.sql.Date(curDateTime.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat(pstrDateFormat);
			pstrDateTime = sdf.format(curSqlDateTime);
		}
		return pstrDateTime;
	}

	/**
	 * Get current date in form of java.sql.Timestamp and then convert it into
	 * String. The return date string follow that of the input param DateFormat.
	 * 
	 * @return String representation of current date.
	 */
	public static String getCurSQLDateTimeInString(String pstrDateFormat) {
		String pstrDateTime = null;
		if ((pstrDateFormat != null) && (!pstrDateFormat.equals(""))) {
			Date curDateTime = new Date();
			Timestamp curSqlDateTime = new Timestamp(curDateTime.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat(pstrDateFormat);
			pstrDateTime = sdf.format(curSqlDateTime);
		}
		return pstrDateTime;
	}

	/**
	 * check the date by pattern
	 * 
	 * @param sDateValue
	 * @param sDateFormat
	 * @return boolean,if check the date ok,then return true
	 * @throws java.text.ParseException
	 */
	public static boolean checkDateByMask(String sDateValue, String sDateFormat) throws ParseException {
		boolean isTrue = false;
		if (sDateValue == null || sDateFormat == null || sDateValue.equals(""))
			return false;
		if (sDateValue.length() != sDateFormat.length())
			return false;
		Date date = stringToDate(sDateValue, sDateFormat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (sDateFormat.indexOf("yyyy") > -1) {
			isTrue = (cal.get(Calendar.YEAR) == Integer.parseInt(sDateValue.substring(sDateFormat.indexOf("yyyy"),
					sDateFormat.indexOf("yyyy") + 4))
					&& cal.get(Calendar.MONTH) == (Integer.parseInt(sDateValue.substring(sDateFormat.indexOf("MM"),
							sDateFormat.indexOf("MM") + 2)) - 1) && cal.get(Calendar.DATE) == Integer
					.parseInt(sDateValue.substring(sDateFormat.indexOf("dd"), sDateFormat.indexOf("dd") + 2)));
		} else {
			isTrue = (cal.get(Calendar.YEAR) == Integer.parseInt(sDateValue.substring(sDateFormat.indexOf("yy"),
					sDateFormat.indexOf("yy") + 2))
					&& cal.get(Calendar.MONTH) == (Integer.parseInt(sDateValue.substring(sDateFormat.indexOf("MM"),
							sDateFormat.indexOf("MM") + 2)) - 1) && cal.get(Calendar.DATE) == Integer
					.parseInt(sDateValue.substring(sDateFormat.indexOf("dd"), sDateFormat.indexOf("dd") + 2)));
		}
		return isTrue;
	}

	/**
	 * get current date by local pattern
	 * 
	 * @param ifdatetime
	 * @return String
	 */
	public static String getNowWithLocal(boolean ifdatetime) {
		Date dd = new Date();
		if (ifdatetime) {
			return java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.LONG, java.text.DateFormat.LONG)
					.format(dd);
		} else {
			return java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(dd);
		}
	}

	// 取得当前系统时间with yyyy-mm-dd 格式
	public static String getNow(boolean ifdatetime) {
		Date dd = new Date();
		SimpleDateFormat df = null;
		String rtn = "";
		if (ifdatetime) {
			df = new SimpleDateFormat(STRING_DATE_FORMAT);
		} else {
			df = new SimpleDateFormat(DATE_FORMAT);
		}
		rtn = df.format(dd);
		return rtn;
	}

	public static String getNowFormatLog() {
		String LOG_DATE_FORMAT = "yyyyMMdd HH:mm:ss";
		Date dd = new Date();
		SimpleDateFormat df = null;
		String rtn = "";
		df = new SimpleDateFormat(LOG_DATE_FORMAT);
		rtn = df.format(dd);
		return rtn;
	}

	

	/**
	 * 用于从datetime中取得date
	 * 
	 * @param strDate
	 *            String 从数据库中取得的日期型数据
	 * @return String 以yyyy-mm-dd返回日期
	 */
	public static String subDate(String strDate) {
		if (strDate.length() > 10) {
			int pos = strDate.indexOf(" ");
			if (pos > 0) {
				return strDate.substring(0, pos);
			} else {
				return strDate;
			}
		} else {
			return strDate;
		}
	}

	// 用于从datetime中取得time
	public static String subTime(String strDate) {
		if (strDate.length() >= 8) {
			int pos = strDate.indexOf(" ");
			int pos1 = strDate.indexOf(".");
			if (pos1 <= 0)
				pos1 = strDate.length();
			if (pos > 0) {
				return strDate.substring(pos + 1, pos1);
			} else {
				if (strDate.indexOf("-") > 0)
					return "";
				else
					return strDate;
			}
		} else {
			return strDate;
		}
	}

	// 用于从datetime中取得datetime
	public static String subDateTime(String strDate) {
		if (strDate.length() > 10) {
			int pos = strDate.indexOf(".");
			if (pos > 0) {
				return strDate.substring(0, pos);
			} else {
				return strDate;
			}
		} else {
			return strDate;
		}
	}

	/**
	 * 给定两个日期，返回日期的天数差
	 * 
	 * @param startDate
	 * @param endDate
	 * @return int
	 */
	public static int getDayNumber(Date startDate, Date endDate) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		if (startCal.after(endCal)) {
			return 0;
		}
		int num = 1;
		while (startCal.before(endCal)) {
			startCal.add(Calendar.DATE, 1);
			num++;
		}
		return num;
	}

	public static String getDuration(Timestamp createTime, Timestamp finishTime) {
		String rtn = "0小时";
		if (createTime == null) {
			return rtn;
		}
		long ctime = createTime.getTime();
		long ftime = System.currentTimeMillis();
		if (finishTime != null) {
			ftime = finishTime.getTime();
		}
		int dur = (int) (((ftime - ctime) / 1000) / 60);// min
		double dur_day = (double) dur / (60 * 24);
		if (dur_day > 1) {
			// rtn="1天以上";
			DecimalFormat format = new DecimalFormat("###.##");
			rtn = format.format(dur_day) + "天";
		} else {
			double dur_hour = (double) dur / 60;
			DecimalFormat format = new DecimalFormat("###.##");
			rtn = format.format(dur_hour) + "小时";
		}

		return rtn;

	}

	public static String getDurationMinute(Timestamp createTime, Timestamp finishTime) {
		String rtn = "0min";
		if (createTime == null) {
			return rtn;
		}
		long ctime = createTime.getTime();
		BigDecimal ct = new BigDecimal(Long.toString(ctime));
		long ftime = System.currentTimeMillis();
		if (finishTime != null) {
			ftime = finishTime.getTime();
		}
		BigDecimal ft = new BigDecimal(Long.toString(ftime));
		BigDecimal a = ft.subtract(ct);
		BigDecimal o = new BigDecimal("0");
		if (a.compareTo(o) != 0) {
			BigDecimal thonsand = new BigDecimal("1000");
			BigDecimal b = a.divide(thonsand, 2);
			BigDecimal sixty = new BigDecimal("60");
			double du = b.divide(sixty, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
			DecimalFormat format = new DecimalFormat("###.##");
			rtn = format.format(du) + "min";
		}
		return rtn;
	}

	public static String getDurationHour(Timestamp time1, Timestamp time2, String direction) {
		String f = direction;
		if (f == null) {
			f = "+";
		}
		long ctime1 = System.currentTimeMillis();
		long ctime2 = System.currentTimeMillis();
		if (time1 != null) {
			ctime1 = time1.getTime();
		}
		if (time2 != null) {
			ctime2 = time2.getTime();
		}
		int dur = 0;
		if ("+".equals(f)) {// time2-time1
			dur = (int) (((ctime2 - ctime1) / 1000) / 60);// min
		} else {
			dur = (int) (((ctime1 - ctime2) / 1000) / 60);// min
		}

		double dur_hour = (double) dur / 60;
		DecimalFormat format = new DecimalFormat("###.##");
		return format.format(dur_hour) + "hr";

	}

	/**
	 * 给定结束时间和天数,返回开始时间
	 * 
	 * @param Date
	 *            endDate
	 * @param int days
	 * @return String
	 */
	public static String getStartDate(int days, String strEndDate) {
		Calendar endCal = Calendar.getInstance();
		Date endDate = stringToDate(strEndDate, STRING_DATE_FORMAT);
		endCal.setTime(endDate);
		endCal.add(Calendar.DATE, -days);
		SimpleDateFormat sdf = new SimpleDateFormat(STRING_DATE_FORMAT);
		return sdf.format(endCal.getTime());

	}

	/**
	 * 获取当日开始时间
	 * 
	 * @return
	 */
	public static Date getDayStartDate(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static void main(String[] arge) throws ParseException {
		/*String a = getNow(false);
		
		getBeforeDate(8);
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("时间"+form.format(getBeforeDate(365)));
		System.out.println(getCurSQLDateTimeInString("yyyy-MM-dd HH:mm:ss"));
		*/
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		System.out.println(form.format(getNextMonthDay(form.parse("2011-04-23"))));
//		System.out.println(compareDate(form.parse("2011-02-02"), getNextMonthDay(form.parse("2011-01-30"))));
//		System.out.println(getApartDate(form.parse("2011-03-01"), getNextMonthDay(new Date())));
		Date curr = new Date();
		System.out.println(form.format(curr));
		Date finDate = getNextDaysByStartDate(curr,1);
		System.out.println(form.format(finDate));
		boolean flag = curr.after(finDate);
		System.out.println(form.format(new Date()));
		System.out.println(flag);
		
		System.out.println("==============");
		System.out.println(getCurDateToString("HHmm"));
		
		System.out.println(dateToString(getWeekkStartDate( stringToDate("2012-10-21",DateUtil.DATE_FORMAT))));
		System.out.println("100后的今天： ——>"+form.format(getNextYearsByStartDate(new Date(),100)));
	}

	public static Timestamp getDayStartTimestamp(Date date) {
		Date start = getDayStartDate(date);
		return new Timestamp(start.getTime());
	}

	/**
	 * 获取下一日开始时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextDayStartDate(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	
	/**
	 * 几天后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getNextDaysByStartDate(Date date,int day) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
	
	/**
     * 获取指定日期的周的开始第一天
     * @param date
     * @return
     */
     public static Date getWeekkStartDate(Date date) {
          GregorianCalendar calendar = new GregorianCalendar();
          calendar.setTime(date);
           //int min = calendar.getActualMinimum(Calendar.DAY_OF_WEEK); // 获取周开始基准
           //int current = calendar.get(Calendar.DAY_OF_WEEK)-1; // 获取当天周内天数
          calendar.setFirstDayOfWeek(Calendar. MONDAY);
          calendar.set(Calendar. DAY_OF_WEEK,calendar.getFirstDayOfWeek() ); // 当天-基准，获取周开始日期
          
           return calendar.getTime();
    }

	
	/**
	 * 几周后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getNextWeeksByStartDate(Date date,int week) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, week*7);
		return calendar.getTime();
	}
	
	/**
	 * 几月后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getNextMonthsByStartDate(Date date,int month) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}
	
	/**
	 * 几年后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getNextYearsByStartDate(Date date,int year) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.YEAR, year);
		return calendar.getTime();
	}
	
	
	
	/**
	 * 几小时后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getNextHoursByStartDate(Date date,int hour) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		return calendar.getTime();
	}
	
	/**
	 * 几分钟后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getNextMinutesByStartDate(Date date,int minute) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}
	
	/**
	 * 几秒后的时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getNextSecondsByStartDate(Date date,int second) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.SECOND, second);
		return calendar.getTime();
	}

	public static Timestamp getNextDayStartTimestamp(Date date) {
		Date start = getNextDayStartDate(date);
		return new Timestamp(start.getTime());
	}

	/**
	 * 获取月开始时间
	 * 
	 * @return
	 */
	public static Date getMonthStartDate(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Timestamp getMonthStartTimestamp(Date date) {
		Date start = getMonthStartDate(date);
		return new Timestamp(start.getTime());
	}

	/**
	 * 获取下月开始时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextMonthStartDate(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.add(Calendar.MONTH, 1);

		return calendar.getTime();
	}

	public static Timestamp getNextMonthStartTimestamp(Date date) {
		Date start = getNextMonthStartDate(date);
		return new Timestamp(start.getTime());
	}

	/**
	 * 获取当年开始时间
	 * 
	 * @return
	 */
	public static Date getYearStartDate(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Timestamp getYearStartTimestamp(Date date) {
		Date start = getYearStartDate(date);
		return new Timestamp(start.getTime());
	}

	/**
	 * 获取下一年开始时间
	 * 
	 * @return
	 */
	public static Date getNextYearStartDate(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.add(Calendar.YEAR, 1);

		return calendar.getTime();
	}

	public static Timestamp getNextYearStartTimestamp(Date date) {
		Date start = getNextYearStartDate(date);
		return new Timestamp(start.getTime());
	}

	/**
	 * 获取N天之后的具体时间;
	 * @param dateNum
	 * @return
	 */
	public static Date getAfterDate(int dateNum) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.set(Calendar.DAY_OF_YEAR, day + dateNum);
		Date beforeDate = calendar.getTime();
		return beforeDate;
	}

	/**
	 * 获取N天之后的具体时间;
	 * @param dateNum
	 * @return
	 */
	public static Date getAfterDateByDate(Date date, int dateNum) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.set(Calendar.DAY_OF_YEAR, day + dateNum);
		Date beforeDate = calendar.getTime();
		return beforeDate;
	}

	/**
	 * 获取N天之前的具体时间;
	 * @param dateNum
	 * @return
	 */
	public static Date getBeforeDate(int dateNum) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.set(Calendar.DAY_OF_YEAR, day - dateNum);
		Date beforeDate = calendar.getTime();
		return beforeDate;
	}

	/**
	 * 比较两个时间是否大于要比较时间；
	 * @param compareDate
	 * @param currentDate
	 * @return
	 */
	public static boolean isLargeCurrentDate(Date compareDate, Date currentDate) {

		// long ms = compareDate.getTime()-currentDate.getTime();
		if (compareDate.after(currentDate)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当年的月份
	 * @param currentdate
	 * @return
	 */
	public static int getMonthOfThisYear(Date currentdate) {
		Calendar cal = Calendar.getInstance();
		if (currentdate != null) {
			cal.setTime(currentdate);
		} else {
			cal.setTime(new Date());
		}
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当月的天份
	 * @param currentdate
	 * @return
	 */
	public static int getDayOfThisYear(Date currentdate) {
		Calendar cal = Calendar.getInstance();
		if (currentdate != null) {
			cal.setTime(currentdate);
		} else {
			cal.setTime(new Date());
		}
		return cal.get(Calendar.DATE);
	}

	/**
	 * 获取当年的时间
	 * @param currentdate
	 * @return
	 */
	public static int getYearOfThisYear(Date currentdate) {
		Calendar cal = Calendar.getInstance();
		if (currentdate != null) {
			cal.setTime(currentdate);
		} else {
			cal.setTime(new Date());
		}
		return cal.get(Calendar.YEAR);
	}

	/***
	 * 两个日期比较，返回相差天数
	 * @param sDateValue1
	 * @param sDateValue2
	 * @return
	 * @throws java.text.ParseException
	 */
	public static long getCompareDateNum(String sDateValue1, String sDateValue2) throws ParseException {
		long DAY = 24L * 60L * 60L * 1000L;
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		Date d1 = df.parse(sDateValue1);
		Date d2 = df.parse(sDateValue2);

		return ((d2.getTime() - d1.getTime()) / DAY);
	}

	/**    
	 * 得到本月的今天    
	 *     
	 * @return    
	 */
	public static int getMonthOfToday() {
		GregorianCalendar vTodayCal = new GregorianCalendar();
		return vTodayCal.get(GregorianCalendar.DAY_OF_MONTH);
	}

	/**    
	 * 得到本月的最后一天    
	 *     
	 * @return    
	 */
	public static String getMonthLastDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return sdf.format(calendar.getTime());
	}

	/**
	 * 获取下个月的今天
	 * @param currDate
	 * @return
	 */
	public static Date getNextMonthDay(Date currDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(currDate);
		c.add(Calendar.MONTH, 1);
		return c.getTime();
	}

	/**
	 * 比较天数
	 * endDate >= startDate为true,否则false
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean compareDate(Date startDate, Date endDate) {
		Calendar sd = Calendar.getInstance();
		Calendar ed = Calendar.getInstance();
		sd.setTime(startDate);
		int day1 = sd.get(Calendar.DAY_OF_YEAR);
		ed.setTime(endDate);
		int day2 = ed.get(Calendar.DAY_OF_YEAR);
		sd = null;
		ed = null;
		return (day1 - day2 > 0) ? true : false;
	}

	/**
	 * 获取两个相隔天数
	 * endDate > startDate为true,否则false
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getApartDate(Date startDate, Date endDate) {
		Calendar sd = Calendar.getInstance();
		Calendar ed = Calendar.getInstance();
		sd.setTime(startDate);
		int day1 = sd.get(Calendar.DAY_OF_YEAR);
		ed.setTime(endDate);
		int day2 = ed.get(Calendar.DAY_OF_YEAR);
		sd = null;
		ed = null;
		return (day2 - day1) + 1;
	}
	
	/*
	 * 获取当前天累加后的日期
	 * 如：今天是2011-4-9  传0:2011-4-9 传1:2011-4-10 传-1:2011-4-8
	 * formatStr如：yyyy-MM-dd
	 */
	public static String getDayStr(int i,String formatStr)
	{
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(new Date());
        gc.add(Calendar.DATE, i);
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        String dateString = formatter.format(gc.getTime());
		return dateString;
	}
	/*
	 *日期格式化
	 * @param time 参数格式为：20120515或201205，到月或到日 
	 * @return 返回为2012年05月15日或2012年05月
	 */
	public static String formatDate(String time){
		int length = time.length();
		String year = time.substring(0,4);
		String month = time.substring(4,6);
		String day = null;
		String date = null;
		if(length==6){
			date = year + "年" + month + "月";
		}else{
			day = time.substring(6);
			date = year + "年" + month + "月" + day + "日";
		}
		return date;
	}

	/**
	 * 计算 data2 月份减去data1的月份的差值
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static int getMonthBetween(Date data1,Date data2){
		Calendar ca1 = Calendar.getInstance();
		Calendar ca2 = Calendar.getInstance();
		ca1.setTime(data1);
		ca2.setTime(data2);
		return ca2.get(Calendar.MONTH) - ca1.get(Calendar.MONTH);
	}
	
}
