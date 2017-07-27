package com.fugao.infusion.utils;

import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InfusionDateUtils extends DateUtils {
    /**
     * DatePicker获取日期进行格式转换
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 返回格式 YY-mm-dd
     */
    public static String getDate(int year, int month, int day) {
        String date = "" + year;
        month++;
        if (month < 10) {
            date = date + "-0" + month;
        } else {
            date = date + "-" + month;
        }
        if (day < 10) {
            date = date + "-0" + day;
        } else {
            date = date + "-" + day;
        }
        return date;
    }

    /**
     * 根据服务器时间和系统时间间隔是否在规定时间内,判断是否需要重新设定时间
     *
     * @param interval    时间间隔，单位分钟
     * @param nowDateTime 服务器时间
     * @return true设定时间
     */
    public static boolean isSetSystemDateTime(int interval, String nowDateTime) {
        boolean flag = false;
        if (StringUtils.StringIsEmpty(StringUtils.getString(nowDateTime))) {
            return flag;
        }
        long intervalTime = interval * 60 * 1000;
        long systemDateTime = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date begin = dateFormat.parse(nowDateTime);
            long serverDateTime = begin.getTime();
            long time = serverDateTime - systemDateTime;
            if (time < 0) {
                time = 0 - time;
            }
            if (time > intervalTime) {
                flag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public static String getCurrentDate(int diff) {
        String result;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, diff);
        int year = calendar.get(Calendar.YEAR);
        result = year + "";
        int mounth = calendar.get(Calendar.MONTH) + 1;
        if (mounth < 10) {
            result = result + "0" + mounth;
        } else {
            result = result + mounth;
        }
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            result = result + "0" + day;
        } else {
            result = result + day;
        }
        return result;
    }

    public static String getCurrentTime() {
        String result;
        Calendar calendar = Calendar.getInstance();
        int mounth = calendar.get(Calendar.HOUR_OF_DAY);
        if (mounth < 10) {
            result = "0" + mounth + "时";
        } else {
            result = mounth + "时";
        }
        int day = calendar.get(Calendar.MINUTE);
        if (day < 10) {
            result = result + "0" + day + "分";
        } else {
            result = result + day + "分";
        }
        return result;
    }

    public static String getCurrentTimeSec() {
        String result;
        Calendar calendar = Calendar.getInstance();
        int mounth = calendar.get(Calendar.HOUR_OF_DAY);
        if (mounth < 10) {
            result = "0" + mounth + "时";
        } else {
            result = mounth + "时";
        }
        int day = calendar.get(Calendar.MINUTE);
        if (day < 10) {
            result = result + "0" + day + "分";
        } else {
            result = result + day + "分";
        }
        int sec = calendar.get(Calendar.SECOND);
        if (sec < 10) {
            result = result + "0" + sec + "秒";
        } else {
            result = result + sec + "秒";
        }
        return result;
    }

    public static String getSectionTime() {
        Calendar calendar = Calendar.getInstance();
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        /*if (24 <= hh || hh < 4) {
            return "2";
		} else if (4 <= hh && hh < 8) {
			return "6";
		} else if (8 <= hh && hh < 12) {
			return "10";
		} else if (12 <= hh && hh < 16) {
			return "14";
		} else if (16 <= hh && hh < 20) {
			return "18";
		} else if (20 <= hh && hh < 24) {
			return "22";
		} else {
			return "2";
		}*/
        if (1 <= hh && hh <= 9) {
            return "0" + Integer.toString(hh);
        } else return Integer.toString(hh);
    }

    public static String getChooseTime(String time) {
        if(!StringUtils.StringIsEmpty(time)){
            int hh = Integer.parseInt(time);
            if (hh <= 4) {
                return "4";
            } else if (4 < hh && hh <= 8) {
                return "8";
            } else if (8 < hh && hh <= 12) {
                return "12";
            } else if (12 < hh && hh <= 16) {
                return "16";
            } else if (16 < hh && hh <= 20) {
                return "20";
            } else if (20 < hh && hh <= 24) {
                return "24";
            } else {
                return "4";
            }
        }
        return "";
    }

    /**
     * 体温默认录入时间
     * @param time
     * @param type
     * @return
     */
    public static String getCurrentChooseTimeBySingsInputTime(String time,int type) {
        String defaultTime="";
        if(!StringUtils.StringIsEmpty(time)){
            int hh = Integer.parseInt(time);
            switch (type){
                case 1:
                    defaultTime=hh+"";
                    break;
                case 2:
                    if (24 <= hh || hh < 4) {
                        defaultTime ="2";
                    } else if (4 <= hh && hh < 8) {
                        defaultTime= "6";
                    } else if (8 <= hh && hh < 12) {
                        defaultTime= "10";
                    } else if (12 <= hh && hh < 16) {
                        defaultTime= "14";
                    } else if (16 <= hh && hh < 20) {
                        defaultTime= "18";
                    } else if (20 <= hh && hh < 24) {
                        defaultTime= "22";
                    }
                    break;
                case 3:
                    if (hh <= 4) {
                        defaultTime= "4";
                    } else if (4 < hh && hh <= 8) {
                        defaultTime= "8";
                    } else if (8 < hh && hh <= 12) {
                        defaultTime= "12";
                    } else if (12 < hh && hh <= 16) {
                        defaultTime= "16";
                    } else if (16 < hh && hh <= 20) {
                        defaultTime= "20";
                    } else if (20 < hh && hh <= 24) {
                        defaultTime= "24";
                    }
                    break;

            }



        }
        return defaultTime;
    }
    /*  public static String getChooseTime(String time) {

        int hh = Integer.parseInt(time);

    }*/

    /**
     * 得到当前日期
     *
     * @return date yyyy-MM-dd
     */
    public static String getDate() {
        String date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(new Date());
        return date;
    }

    /**
     * 得到当前日期时间
     *
     * @return
     */
    public static String getDateTime() {
        String date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        date = dateFormat.format(new Date());
        return date;
    }

    /**
     * 得到当前小时数
     *
     * @return
     */
    public static String getHour() {
        String hour = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        hour = dateFormat.format(new Date());
        return hour;
    }

    /**
     * 得道距离当前时间 的时间小时间隔
     *
     * @param currentDate   上次时间
     * @param moreThanOnDay 超过一天 ，中英文区别
     * @return
     * @Title: getBetweenTime
     * @Description: TODO
     * @return: String
     */
    public static String getBetweenTime(String currentDate, String moreThanOnDay) {
        String currentTime = "";
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date currentD = dateFormat1.parse(currentDate);
            long longtiem = System.currentTimeMillis() - currentD.getTime();

            if (longtiem > DAY_MILLIS) {
                currentTime = moreThanOnDay;
            } else if (longtiem > HOUR_MILLIS) {
                int hourseTime = (int) (longtiem / HOUR_MILLIS);
                int minutsTime = (int) (longtiem % HOUR_MILLIS);
                currentTime = (minutsTime == 0) ? (convertDouble(hourseTime) + "-00")
                        : (convertDouble(hourseTime) + "-" + convertDouble((int) (minutsTime / MINUTE_MILLIS)));
            } else {
                currentTime = convertDouble((int) (longtiem / MINUTE_MILLIS))
                        + "";
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return currentTime;
    }

    public static String convertDouble(int num) {
        if (num < 10 && num >= 0) {
            return "0" + num;
        } else {
            return "" + num;
        }

    }



    public static String getTimeString(String dateString) {
        String resultString = "";
        Date date = null;
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("HH");
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

}
