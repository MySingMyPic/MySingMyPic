package com.ylsg365.pai.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by object1984 on 2015-03-15.
 */
public class DateUtil {
    /* 日志 */

    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_2 = "HH时mm分ss秒";
    public static final String FORMAT_3 = "yyyy年MM月dd日";
    public static final String FORMAT_4 = "yyyy-MM-dd";
    public static final String FORMAT_5 = "yyyy年MM月";
    public static final String FORMAT_6 = "MM.dd";
    public static final String FORMAT_7 = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String FORMAT_8 = "yyyyMMddHHmmss";
    public static final String FORMAT_9 = "yyyyMMdd";
    public static final String FORMAT_10 = "yyyyMMddHH";
    public static final String FORMAT_11 = "yyyy";

    /**
     * 将传入Date值转换为想要显示的字符串
     *
     * @param value 只允许java.util.Date 或 String 类型,其他类型不处理
     * @return 几秒前，几分钟前，几小时前，几天前，几个月前，几年前，很久以前（10年前）
     */
    public static String convertToShowStr(Object value) {
        String result = null;
        if (value instanceof Date) {
            result = convertDateToShowStr((Date) value);
        } else if (value instanceof String) {
            result = convertDateToShowStr(convertToDate((String) value));
        }
        return result;
    }


    /**
     * 将传入的时间字符串转换为Date
     *
     * @param value
     * @return
     */
    public static Date convertToDate(String value) {
        if(StringUtil.isNull(value)){
            return new Date();
        }
        Date result = null;
        try {
            if (value.indexOf(":") == -1) {
                result = sdfDate.parse(value);
            } else {
                result = sdfDateTime.parse(value);
            }
        } catch (ParseException e) {
        }
        return result;
    }


    /**
     * 转换日期到指定格式方便查看的描述说明
     *
     * @param date
     * @return 几秒前，几分钟前，几小时前，几天前，几个月前，几年前，很久以前（10年前）,如果出现之后的时间，则提示：未知
     */
    private static String convertDateToShowStr(Date date) {
        String showStr = "";
        long yearSeconds = 31536000L;//365 * 24 * 60 * 60;
        long monthSeconds = 2592000L;//30 * 24 * 60 * 60;
        long daySeconds = 86400L;//24 * 60 * 60;
        long hourSeconds = 3600L;//60 * 60;
        long minuteSeconds = 60L;


        long time = (new Date().getTime() - date.getTime()) / 1000;
        if (time <= 0) {
            showStr = "刚刚";
            return showStr;
        }
        if (time / yearSeconds > 0) {
            int year = (int) (time / yearSeconds);
            if (year > 10)
                showStr = "很久前";
            else {
                showStr = year + "年前";
            }
//        } else if (time / monthSeconds > 0) {
//            showStr = time / monthSeconds + "个月前";
        } else if (time / daySeconds > 0 && time / daySeconds <= 3) {
            showStr = time / daySeconds + "天前";
        } else if (time / hourSeconds > 0 && time / hourSeconds <= 23) {
            showStr = time / hourSeconds + "小时前";
        } else if (time / minuteSeconds > 0 && time / minuteSeconds <= 59) {
            showStr = time / minuteSeconds + "分钟前";
        } else if (time > 0 && time <= 59) {
            showStr = time + "秒前";
        } else {
            showStr = sdfDate.format(date);
        }


        return showStr;
    }

    public static String getFriendlyDate(String date){
        return convertDateToShowStr(convertToDate(date));
    }
    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat(FORMAT_8);
        return f.format(date);
    }
    /**
     * 取得当前时间的string 形式,样式yyyy-MM-dd hh:mm:ss
     *
     * @return
     */
    public static String getDate() {
        return format(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回给给定日期的string形式,格式yyyy-MM-dd hh:mm:ss
     *
     * @param d
     * @return
     */
    public static String getDate(Date d) {
        return format(d, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 取得当前日期,年月日
     *
     * @return
     */
    public static String getDateyyyyMMdd() {
        return format(new Date(), "yyyyMMdd");
    }

    public static String getDateyyyyMMdd(Date d) {
        return format(d, "yyyyMMdd");
    }

    public static String getDateyyyy_MM_dd(Date d) {
        return format(d, "yyyy-MM-dd");
    }

    /**
     * 取得当前日期,年月日,格式yyyy-MM-dd
     *
     * @return
     */
    public static String getDateY_M_D() {
        return format(new Date(), "yyyy-MM-dd");
    }

    /**
     * 取得当前时间,时分秒
     *
     * @return
     */
    public static String getDateHMS() {
        return format(new Date(), "HH:mm:ss");
    }

    /**
     * 将date类型格式化为style类型的字符串
     *
     * @param date
     * @param style
     * @return
     */
    public static String format(Date date, String style) {
        DateFormat df = new SimpleDateFormat(style);
        return df.format(date);
    }

    /**
     * 将dateStr解析为date类型
     *
     * @param dateStr
     * @return
     */
    public static Date parse(String dateStr, String style) {
        if (dateStr == null || style == null)
            return null;
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(style);
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
        }
        return date;
    }
    /**
     * 将dateStr解析为date类型
     *
     * @param dateStr
     * @return
     */
    public static Date parse(String dateStr) {
        if(dateStr == null || "".equals(dateStr))
            return null;
        return parse(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parseDate(String dateStr) {
        if(dateStr == null || "".equals(dateStr))
            return null;
        return parse(dateStr, "yyyy-MM-dd");
    }

    /**
     * 返回英文格式 like Jan 01,2009
     *
     * @param
     *
     * @param date
     * @return
     */
    public static String format2English(Date date) {
        return format("MMM dd,yyyy", Locale.ENGLISH, date);
    }

    public static String format(String format, Locale locale, Date date) {
        DateFormat df = new SimpleDateFormat(format, locale);
        return df.format(date);
    }

    public static Date getMonthFirst(String str, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse(str, pattern));
            calendar.set(Calendar.DAY_OF_MONTH, calendar
                    .getActualMinimum(Calendar.DAY_OF_MONTH));
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }

    }
    public static Date getMonthEnd(String str, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse(str, pattern));
            calendar.set(Calendar.DAY_OF_MONTH, calendar
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }
    public static String getMonthFirstDay(String str, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse(str, pattern));
            calendar.set(Calendar.DAY_OF_MONTH, calendar
                    .getActualMinimum(Calendar.DAY_OF_MONTH));
            return getDateyyyy_MM_dd(calendar.getTime());
        } catch (Exception e) {
            return null;
        }

    }

    public static String getMonthEndDay(String str, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse(str, pattern));
            calendar.set(Calendar.DAY_OF_MONTH, calendar
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            return getDateyyyy_MM_dd(calendar.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getNextZeroDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getZeroDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static int getIntevelDays(Date beginDate, Date endDate) {
        Calendar c = Calendar.getInstance();
        int day = 0;
        Date bDate = getNextZeroDate(beginDate);
        Date eDate = getZeroDate(endDate);
        for(c.setTime(bDate); c.getTime().before(eDate); c.add(Calendar.DAY_OF_MONTH, 1)) {
            day ++;
        }
        return day;
    }

    public static int getIntervalMonths(Date date) {
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        return now.get(Calendar.MONTH) - start.get(Calendar.MONTH)
                + 12 * (now.get(Calendar.YEAR) - start.get(Calendar.YEAR));
//				+ (now.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH) + 1)/now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * DateFormat df = DateFormat.getDateInstance(DateFormat.FULL,
     * Locale.CHINA); String s = df.format(new Date()); System.out.println(s);
     * Full Thursday, July 29, 2010 2010年7月29日 星期四 long July 29, 2010 2010年7月29日
     * medium Jul 29, 2010 2010-7-29 short 7/29/10 10-7-29
     *
     * @param args
     */
    public static void main(String[] args) {
        //	System.out.println(DateUtil.getMonthFirstDay("2011-0AA19", "yyyy-MM"));
        //	System.out.println(DateUtil.getMonthEndDay("2011-02", "yyyy-MM"));

//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss MMM d,yyyy z", Locale.ENGLISH);
//		try {
//			System.out.println(sdf.parse("18:31:30 Jan 1,2000 PST"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(DateUtils.getDate(new Date(0)));
//		Date beginDate = DateUtil.parse("2012-06-29 00:00:00");
//		System.out.println(getIntervalMonths(beginDate)%3 == 0);
//		Calendar c = Calendar.getInstance();
//		c.set(2012, 10, 5, 12, 30, 0);
//		System.out.println(DateUtil.getDate(c.getTime()));
//		c.set(2012, 10, 5, 13, 30, 0);
//		System.out.println(DateUtil.getDate(c.getTime()));
    }
    /**
     * gc.add(1,-1)表示年份减一.
     *gc.add(2,-1)表示月份减一.
     *gc.add(3.-1)表示周减一.
     *gc.add(5,-1)表示天减一.
     * @return
     */
    public static List<String> getChargeMonth(){
        List<String> strList = new ArrayList<String>();
        Date startDate = parse("2011-10-01 00:00:00");

        GregorianCalendar gc =new GregorianCalendar();
        gc.setTime(startDate);
        while(gc.getTimeInMillis()<System.currentTimeMillis()){
            strList.add(format(gc.getTime(), "yyyy-MM"));
            gc.add(2, 1);
        }
        Collections.reverse(strList);
        return strList;

    }
    public static Date getFirstDayByNextMonth(int year, int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, 1);
        return c.getTime();
    }

    public static Date getFirstDayByMonth(int year, int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getMonthFirstDayByDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getNextDayByDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static Date getPreDayByDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }

    public static Date getPreDayByDate(Date date, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    public static Date getPreDay000ByDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, -1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);

        return c.getTime();
    }

    public static Date getLastDayByMonth(int year, int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }

    public static Date getDealDayByMonth(int year, int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 5, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, 1);
        return c.getTime();
    }

    public static Date getLastMonthByDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    public static Date get30DayZeroClock(Date date){
        return getDayZeroClock(date, 30);
    }
    /**
     * 给定时间之前多少 天的时间
     * @param date
     * @param beforeDay
     * @return
     */
    public static Date getDayZeroClock(Date date, int beforeDay){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -beforeDay);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    /**
     *
     * @param date
     * @param calendarType
     * @return
     */
    public static int getInt(Date date, int calendarType){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(calendarType);
    }
}
