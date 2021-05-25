package com.u8.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ant on 2015/4/24.
 */
public class TimeUtils {

    public static SimpleDateFormat FORMATER_1 = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat FORMATER_2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    public static SimpleDateFormat FORMATER_3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat FORMATER_4= new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat FORMATER_5= new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat FORMATER_6= new SimpleDateFormat("MM/dd");
    public static SimpleDateFormat FORMATER_7 = new SimpleDateFormat("yyyy-MM-dd");

    public static String format_yyyyMMdd(Date time){
        return FORMATER_4.format(time);
    }

    public static String format_yyyyMMddHHmmss(Date time){

        return FORMATER_1.format(time);
    }

    public static String format_yyyyMMddHHmmssSSS(Date time){
        return FORMATER_2.format(time);
    }


    public static String format_default(Date time){
        return FORMATER_3.format(time);
    }
    public static Object parse_default(String time){
        try {
            return FORMATER_3.parseObject(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean sameYear(Date date1, Date date2){

        if(date1 == null || date2 == null){
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar.setTime(date2);

        return calendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) == 0;
    }

    public static Date dateSub(Date date, int val){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -val);
        date = calendar.getTime();
        return date;
    }

    public static Date dateAdd(Date date, int val){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, val);
        date = calendar.getTime();
        return date;
    }

    public static Date minuteAdd(Date date, int val){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, val);
        date = calendar.getTime();
        return date;
    }

    public static Date lastDay(){
        return dateSub(new Date(), 1);
    }

    public static Date nextDay(){
        return dateAdd(new Date(), 1);
    }

    public static Date nextDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    public static Date dateBegin(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);

        return cal.getTime();
    }

    public static Date dateEnd(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);

        cal.setTimeInMillis(cal.getTimeInMillis()+23*60*60*1000 + 59*60*1000 + 59*1000);

        return cal.getTime();
    }
}
