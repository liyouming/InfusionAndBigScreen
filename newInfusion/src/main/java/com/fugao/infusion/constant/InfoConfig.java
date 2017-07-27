
package com.fugao.infusion.constant;

import android.net.Uri;


public class InfoConfig {
    /**
     * 不能更改
     */
    public static final String VND = "vnd.android.cursor.dir";
    public static final String CONTENT = "content://";
    /**
     * 默认log标志
     */
    public static final String TAG = "Infusion";

    /**
     * 数据库名称
     */
    public static final String DB_NAME = "info.db";

    /**
     * 数据库版本
     */
    public static final int DB_VERSION = 8;

    /**
     * 瓶贴表名
     */
    public static final String TABLE_BOTTLE = "bottle";

    /**
     * 工作量表
     */
    public static final String TABLE_WORKLOAD = "workload";

    /**
     * 输液区域表
     */
    public static final String TABLE_INFUSIONAREA = "infoarea";

    /**
     * 主机名
     */
    public static final String AUTHORITY = "com.fugao.provider.infusion";

    /**
     * 返回瓶贴集合MIME类型字符串
     */
    public static final String CONTENT_TYPE_BOTTLE = VND + "/" + TABLE_BOTTLE;

    /**
     * 返回工作量MIME类型字符串
     */
    public static final String CONTENT_TYPE_WORKLOAD = VND + "/" + TABLE_WORKLOAD;
    /**
     * 返回区域MIME类型字符串
     */
    public static final String CONTENT_TYPE_INFUSIONAREA = VND + "/" + TABLE_INFUSIONAREA;

    /**
     * 瓶贴uri
     */
    public static final Uri CONTENT_URI_BOTTLE = Uri.parse(CONTENT + AUTHORITY + "/" + TABLE_BOTTLE);

    /**
     * 瓶贴uri
     */
    public static final Uri CONTENT_URI_WORKLOAD =
            Uri.parse(CONTENT + AUTHORITY + "/" + TABLE_WORKLOAD);


    /**
     * 区域uri
     */
    public static final Uri CONTENT_URI_INFUSIONAREA =
            Uri.parse(CONTENT + AUTHORITY + "/" + TABLE_INFUSIONAREA);

    /**
     * RestClient volley请求超时事件
     */
    public static final int TIME_OUT = 40 * 1000;

    /**
     * 在9秒之后刷新 以后每隔n7s轮训
     */
    public static final long AUTO_REFRESH_INITIALDELAY = 0L;

    public static final long AUTO_REFRESH_PERIOD_CALL = 10L;

    /**
     * 五分钟轮询座位信息
     */
    public static final long AUTO_REFRESH_PERIOD_SEAT = 12L;
}
