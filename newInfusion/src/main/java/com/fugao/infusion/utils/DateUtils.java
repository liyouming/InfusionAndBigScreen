/*
 * Copyright (C) 2014 loQua.Xee <loquaciouser@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fugao.infusion.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.jasonchen.base.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  public static SimpleDateFormat dateFormate = new SimpleDateFormat();
  /**
   * yyyyMMdd
   */
  public static final String DATE_DEFAULT_FORMATE = "yyyyMMdd";
  /**
   * 一秒的毫秒
   */
  public static final long SECOND_MILLIS = 1000;
  /**
   * 一分的毫秒
   */
  public static final long MINUTE_MILLIS = 60 * 1000;
  /**
   * 一小时的毫秒
   */
  public static final long HOUR_MILLIS = MINUTE_MILLIS * 60;
  /**
   * 一天的毫秒
   */
  public static final long DAY_MILLIS = HOUR_MILLIS * 24;
  /**
   * 一年的毫秒
   */
  private static final long YEAR_MILLIS = DAY_MILLIS * 365L;
  /**
   * 一月的毫秒
   */
  private static final long MONTH_MILLIS = DAY_MILLIS * 30L;

  /**
   * 得到明天的时间 （有缺陷，需完善）
   */
  public static String getTomorrow(String beginDate) {

    beginDate += " 00:00:00";
    String endDate = "";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date begin = dateFormat.parse(beginDate);
      long beginMilliSecond = begin.getTime();
      long endMilliSecond = beginMilliSecond + DAY_MILLIS;
      Date end = new Date(endMilliSecond);

      endDate = dateFormat1.format(end);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return endDate;
  }

  public static String ThreeDay() {

    String endDate = "";
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    long beginMilliSecond = date.getTime();
    long endMilliSecond = beginMilliSecond + DAY_MILLIS + DAY_MILLIS + DAY_MILLIS;
    Date end = new Date(endMilliSecond);
    endDate = dateFormat1.format(end);
    return endDate;
  }

  public static String showData(long milliseconds, String dateformat) {

    return toTime(new Date(milliseconds), dateformat);
  }

  /**
   *
   * @param date
   * @param pattern
   * @return
   */
  public static String toTime(Date date, String pattern) {
    if (TextUtils.isEmpty(pattern)) {
      pattern = DATE_DEFAULT_FORMATE;
    }
    dateFormate.applyPattern(pattern);
    if (date == null) {
      date = new Date();
    }
    try {
      return dateFormate.format(date);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 转换为制定格式
   *
   * @param dateType 例"yyyy-MM-dd HH:mm:ss"
   */
  public static String getPatternDate(long time, String dateType) {
    Date date = new Date(time);
    DateFormat dateFormat = new SimpleDateFormat(dateType);
    return dateFormat.format(date);
  }

  /**
   * 得到当前时间格式自己填写
   *
   * @param fromat 格式
   */
  public static String getCurrentDate(String fromat) {
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat(fromat);
    return dateFormat.format(date);
  }

  /**
   * 得到今天以前的几天时间
   *
   * @param currentDate 当前时间
   * @param days 相差几天
   */
  public static String getBetweenDate(String currentDate, int days) {

    currentDate += " 00:00:00";
    String endDate = "";
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
    try {
      Date begin = dateFormat.parse(currentDate);
      long beginMilliSecond = begin.getTime();
      long endMilliSecond = beginMilliSecond - (days * DAY_MILLIS);
      Date end = new Date(endMilliSecond);

      endDate = dateFormat1.format(end);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return endDate;
  }
    /**
     *
     *
     * @param
     * @param hour 相差小时然后改为yyyy-MM-dd HH:mm:ss格式
     */
    public static String getBetweenHour(long beginMilliSecond, int hour) {

//        currentDate += " 00:00:00";
           String endDate = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
         //   Date begin = dateFormat.parse(currentDate);
          //  long beginMilliSecond = begin.getTime();
            long endMilliSecond = beginMilliSecond - (hour * HOUR_MILLIS);
            Date end = new Date(endMilliSecond);
            endDate = dateFormat.format(end);
        return endDate;
    }
    /**
     * @param
    * @param
    */
    public static String getBetweenHour() {

        String endDate = "";
        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        long endMilliSecond = System.currentTimeMillis() - (2 * HOUR_MILLIS);
        Date end = new Date(endMilliSecond);
        endDate = dateFormat1.format(end);
        return endDate;
    }

  /**
   * 传入yyyy-MM-dd HH:mm:s 转换为 YYYY-MM-dd
   *
   * @param dateString YYYY-MM-dd
   */
  public static String getYYYYMMHH(String dateString) {
    String purposeData = "";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

    try {
      Date currentDate = dateFormat.parse(dateString);

      purposeData = dateFormat1.format(currentDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return purposeData;
  }

  /**
   * 传入yyyyMMdd 转换为 MMdd
   *
   * @param dateString MMdd
   */
  public static String getMMddHHmm(String dateString) {
    String purposeData = "";

    dateString = StringUtils.getString(dateString);
    DateFormat dateFormat;
    if (dateString.contains("-")) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    } else {
      dateFormat = new SimpleDateFormat("yyyyMMdd");
    }

    DateFormat dateFormat1 = new SimpleDateFormat("MM-dd");

    try {
      Date currentDate = dateFormat.parse(dateString);

      purposeData = dateFormat1.format(currentDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();
      purposeData = dateString;
    }
    return purposeData;
  }

  /**
   * 传入HHmmss 转换为 HHmm
   *
   * @param
   */
  public static String getHHmm(String timeString) {
    String purposeTime = "";
    timeString = StringUtils.getString(timeString);
    DateFormat dateFormat;
    if (timeString.contains("-")) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    } else if (timeString.contains(":")) {
      dateFormat = new SimpleDateFormat("HH:mm:ss");
    } else {
      dateFormat = new SimpleDateFormat("HHmmss");
    }
    DateFormat dateFormat1 = new SimpleDateFormat("HH:mm");

    try {
      Date currentDate = dateFormat.parse(timeString);

      purposeTime = dateFormat1.format(currentDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();
      purposeTime = timeString;
    }
    return purposeTime;
  }

  /**
   * 传入HHmmss 转换为 HHmm
   *
   * @param
   */
  public static String getHHmmss(String timeString) {
    String purposeTime = "";
    timeString = StringUtils.getString(timeString);
    DateFormat dateFormat;
    if (timeString.contains("-")) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    } else if (timeString.contains(":")) {
      dateFormat = new SimpleDateFormat("HH:mm:ss");
    } else {
      dateFormat = new SimpleDateFormat("HHmmss");
    }

    DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");

    try {
      Date currentDate = dateFormat.parse(timeString);

      purposeTime = dateFormat1.format(currentDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      //e.printStackTrace();
      purposeTime = timeString;
    }
    return purposeTime;
  }

  /**
   * 转换日期时间的格式
   *
   * @Title: changeDateFormat
   * @Description: TODO
   * @return: String
   */
  public static String changeDateFormat(String dateStr, String oldDateFormat,
      String newDateFormat) {
    String purposeData = "";
    DateFormat dateFormat = new SimpleDateFormat(oldDateFormat);
    DateFormat dateFormat1 = new SimpleDateFormat(newDateFormat);

    try {
      Date currentDate = dateFormat.parse(dateStr);

      purposeData = dateFormat1.format(currentDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return purposeData;
  }

  /**
   * 根据当前日期得到当天是星期几
   *
   * @param dateFormatStr （yyyyMMdd）
   * @param dateStr （20130922）
   */
  public static String getWeekDay(String dateFormatStr, String dateStr) {
    String str = "";
    DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
    try {
      Date begin = dateFormat.parse(dateStr);
      long serverDateTime = begin.getTime();
      Date date = new Date(serverDateTime);
      DateFormat df = new SimpleDateFormat("EEE");
      str = df.format(date);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return str;
  }

  public static String getCutLength(String Str, int length) {
    if (Str != null && Str.length() > length) {
      Str = Str.substring(0, length);
    } else {
      Str = "";
    }
    return Str;
  }

  /**
   * 得到当前小时数
   */
  public static String getHour() {
    String hour = "";
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
    hour = dateFormat.format(new Date());
    return hour;
  }

  public static long getDateToMills(String dateString) {
    Date date = new Date();
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd EEE");
    try {
      date = dateFormat1.parse(dateString);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      date = new Date();
    }
    return date.getTime();
  }

  public static int getDateSex() {
    int result = 1;
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    if (hour >= 0 && hour < 10) {
      result = 1;
    } else if (hour >= 10 && hour < 15) {
      result = 2;
    } else if (hour >= 15 && hour < 24) {
      result = 3;
    }
    return result;
  }

  /**
   * 划分订餐时间点 1.早餐 2.中餐3.晚餐
   */
  public static int getDateTimeSex() {
    int result = 1;
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    if (hour >= 0 && hour < 9) {
      result = 1;
    } else if (hour >= 9 && hour < 14) {
      result = 2;
    } else if (hour >= 14 && hour < 24) {
      result = 3;
    }
    return result;
  }

  public static int getKeyOfWeekDay(long longString) {
    int result = 0;
    String s2 = showData(longString, "yyyy-MM-dd");
    System.out.println(s2);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    try {
      date = dateFormat.parse(s2.toString());
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    result = calendar.get(Calendar.DAY_OF_WEEK);
    return result;
  }

  ;

  /**
   * 根据Sting时间转换成Date格式时间
   *
   * @param str yyyyMMdd
   */
  @SuppressLint("SimpleDateFormat")
  public static Date getDateTimeByStr(String str) {
    Date date = null;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    try {
      date = dateFormat.parse(str);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 根据Sting时间转换成Date格式时间
   *
   * @param  -MM-dd
   */
  public static Date getDateTimeByString(String str) {
    Date date = null;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    try {
      date = dateFormat.parse(str);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return date;
  }

  public static int daysBetween(Date smdate, Date bdate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(smdate);
    long time1 = cal.getTimeInMillis();
    cal.setTime(bdate);
    long time2 = cal.getTimeInMillis();
    long between_days = (time2 - time1) / (1000 * 3600 * 24);
    return Integer.parseInt(String.valueOf(between_days));
  }

  /**
   * 将时间回到0点
   */
  public static Date getStartDateTime(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    String str = dateFormat.format(date);
    try {
      return dateFormat.parse(str);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 得到明天时间
   */
  public static String getlastdata(String beginDate) {
    long HOUR_MILLI = 1000 * 60 * 60 * 24;
    beginDate += " 00:00:00";
    String endDate = "";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date begin = dateFormat.parse(beginDate);
      long beginMilliSecond = begin.getTime();
      long endMilliSecond = beginMilliSecond + HOUR_MILLI;
      Date end = new Date(endMilliSecond);

      endDate = dateFormat1.format(end);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return endDate;
  }

  /**
   * @param dateString 返回 yyyy-MM-dd HH:mm:ss的时间
   * @Title: getContainsTDateString
   * @Description: TODO 解析 "2014-01-04T16:48:10.71";格式的时间
   * @return: String
   */

  public static String getContainsTDateString(String dateString) {
    String resultString = "";
    Date date = null;
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      date = dateFormat1.parse(dateString);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      date = new Date();
    }
    resultString = dateFormat2.format(date);
    return resultString;
  }

  public static String getContainsTHourString(String dateString) {
    String resultString = "";
    Date date = null;
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    DateFormat dateFormat2 = new SimpleDateFormat("MM-dd HH:mm");
    try {
      date = dateFormat1.parse(dateString);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      date = new Date();
    }
    resultString = dateFormat2.format(date);
    return resultString;
  }

  /**
   * 标准的时间 yyyy-MM-dd HH:mm:ss
   * 转换为    MM-dd HH:mm
   *
   * @Title: getStanderMMDDHHMMString
   * @Description: TODO
   * @return: String
   */
  public static String getStanderMMDDHHMMString(String dateString) {
    String resultString = "";
    Date date = null;
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateFormat2 = new SimpleDateFormat("MM-dd HH:mm");
    try {
      date = dateFormat1.parse(dateString);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      date = new Date();
    }
    resultString = dateFormat2.format(date);
    return resultString;
  }

  /**
   * 得到标准格式的 日期毫秒值
   *
   * @param dateString "yyyy-MM-dd HH:mm:ss"
   * @return 如果传人的日期格式异常 则返回当前时间
   * @Title: getStandardDateToMills
   * @Description: TODO
   * @return: long
   */
  public static long getStandardDateToMills(String dateString) {
    Date date = new Date();
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      date = dateFormat1.parse(dateString);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      date = new Date();
    }
    return date.getTime();
  }

  /**
   * 得到当前时间的标准格式
   *
   * @Title: getStandarCurrentDate
   * @Description: TODO
   * @return: String
   */
  public static String getStandarCurrentDate() {
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return dateFormat.format(date);
  }

  /**
   * 得到当前的时间 里面包含  T，  服务器端限制引起  需要在返回结果中加T
   *
   * @Title: getContainerTCurrentDate
   * @Description: TODO
   * @return: String
   */
  public static String getContainerTCurrentDate() {
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    return dateFormat.format(date);
  }

  public static String getCurrentYearDate() {
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.format(date);
  }

  /**
   * 得到当前的时刻
   *
   * @Title: getCurrentHHDate
   * @Description: TODO
   * @return: String
   */
  public static String getCurrentHHDate() {
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("hh");
    return dateFormat.format(date);
  }

  /**
   * @Title: getCurrentDataMills
   * @Description: TODO
   * @return: long
   */
  public static long getCurrentDataMills() {
    Date date = new Date();
    return date.getTime();
  }

  /**
   * 根据时间字符串和时间格式得到时间毫秒级
   *
   * @param dateTimeStr 时间字符串
   * @param dateFormatStr 时间格式
   * @Title: getTimeInMillisByString
   * @Description: TODO
   * @return: long
   */
  public static long getTimeInMillisByString(String dateTimeStr, String dateFormatStr) {
    Calendar calendar = Calendar.getInstance();
    Date date = null;
    try {
      date = new SimpleDateFormat(dateFormatStr).parse(dateTimeStr);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    long timeInMillis = 0;
    if (date != null) {
      calendar.setTime(date);
      timeInMillis = calendar.getTimeInMillis();
    }
    return timeInMillis;
  }

  /**
   * 传入yyyy-MM-dd HH:mm:ss 转换为HH:mm:ss
   *
   * @param dateString yyyy-MM-dd HH:mm:ss
   * @return HH:mm:ss
   */
  public static String getContinerTHHmm(String dateString) {
    String purposeData = "";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");

    try {
      Date currentDate = dateFormat.parse(dateString);

      purposeData = dateFormat1.format(currentDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return purposeData;
  }

  //	public static String getHHmm(String dateString) {
  //		String purposeData = "";
  //		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  //		DateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
  //
  //		try {
  //			Date currentDate = dateFormat.parse(dateString);
  //
  //			purposeData = dateFormat1.format(currentDate);
  //
  //		} catch (ParseException e) {
  //			// TODO Auto-generated catch block
  //			e.printStackTrace();
  //		}
  //		return purposeData;
  //	}

  /**
   * 得到昨天的时间
   *
   * @return 昨天的时间
   */
  public static String getYesterdayData(String beginDate) {
    long HOUR_MILLI = 1000 * 60 * 60 * 24;
    beginDate += " 00:00:00";
    String endDate = "";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Date begin = dateFormat.parse(beginDate);
      long beginMilliSecond = begin.getTime();
      long endMilliSecond = beginMilliSecond - HOUR_MILLI;
      Date end = new Date(endMilliSecond);
      endDate = dateFormat1.format(end);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return endDate;
  }

  /**
   * @param millistime 传进来的毫秒
   * @param level 什么等级 ，如分钟级别 MINUTE_MILLIS
   * @Title: getStringTimeOfMillis
   * @Description: TODO
   * @return: String
   */
  @Deprecated
  public static String getStringTimeOfMillis(long millistime, long level) {
    StringBuffer string = new StringBuffer();
    long count = getStringTime(string, millistime, level);
    while (count > level) {
      count = getStringTime(string, count, level);
    }
    if (StringUtils.StringIsEmpty(string.toString())) {
      return "01";
    } else {
      return string.toString();
    }
  }

  /**
   * 根据毫秒得到时间
   *
   * @Title: getStringTime
   * @Description: TODO
   * @return: String
   */
  @Deprecated
  public static long getStringTime(StringBuffer string, long time, long level) {
    if (string == null) {
      string = new StringBuffer();
    }
    if (time > YEAR_MILLIS) {
      string.append(getSecondTime(time, YEAR_MILLIS) + "");
      return time % YEAR_MILLIS;
    } else if (time > MONTH_MILLIS && time > level) {
      string.append(getSecondTime(time, MONTH_MILLIS) + "");
      return time % MONTH_MILLIS;
    } else if (time > DAY_MILLIS && time > level) {
      string.append(getSecondTime(time, DAY_MILLIS) + "");
      return time % DAY_MILLIS;
    } else if (time > HOUR_MILLIS && time > level) {
      string.append(noSingle(getSecondTime(time, HOUR_MILLIS)));
      return time % HOUR_MILLIS;
    } else if (time > MINUTE_MILLIS && time > level) {
      string.append(noSingle(getSecondTime(time, MINUTE_MILLIS)));
      return time % MINUTE_MILLIS;
    } else if (time > SECOND_MILLIS && time > level) {
      string.append(noSingle(getSecondTime(time, SECOND_MILLIS)));
      return time % SECOND_MILLIS;
    } else {
      return 0;
    }
  }

  /**
   * 得到时间相除的商
   *
   * @Title: getSecondTime
   * @Description: TODO
   * @return: String
   */
  @Deprecated
  private static int getSecondTime(long sstime2, long divisor) {
    return (int) (sstime2 / divisor);
  }

  /**
   * 给个位数加十位
   *
   * @Title: noSingle
   * @Description: TODO
   * @return: String
   */
  private static String noSingle(int num) {
    if (num < 10 && num > 0) {
      return "0" + num;
    } else {
      return "" + num;
    }
  }
}
