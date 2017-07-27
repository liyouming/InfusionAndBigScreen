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

package com.fugao.infusion.constant;

import com.fugao.infusion.R;
import com.fugao.infusion.model.InfusionAreaBean;

import java.util.ArrayList;

/**
 * 扫描头
 *
 * @ClassName: NurseCanstant
 * @Description: TODO 系统常量
 * @author: 陈亮    chenliang@fugao.com
 * @date: 2014年5月19日 下午3:43:11
 */
public class Constant {
    public static final  int INDEX_1=0;
    public static final  int INDEX_2=1;
    public static final  int INDEX_3=2;
    public static final  int INDEX_4=3;
    public static final  int INDEX_5=4;
    public static final String LastVersionKey="last_version_key";

  /**
   * 收到扫描头扫描的结果
   */
  public static String RECEIVER_SCAN_RESULT = "com.android.scanner.result";

  public static String RECEIVER_SCAN_RESULT_EMH = "com.ge.action.barscan";

  public static String RECEIVER_SCAN_RESULT_MC = "action.barcode.reader.value";

  /**
   * 联新扫描头的结果
   */
  public static String RECEIVER_SCAN_RESULT_LACH =
      "lachesis_barcode_value_notice_broadcast_data_string";
  /**
   * honeywell扫描头的结果
   */
  public static String RECEIVER_SCAN_RESULT_HONEYWELL = "com.honeywell.tools.action.scan_result";

    /**
     * 中标软件医信通扫描头结果
     */
  public static String getRECEIVER_SCAN_RESULT_ECHART = "cs2c.com.cn.serialscan";

  /**
   * 识凌科技升级Action
   */
  public static String SLKJU = "SYSTEM_BAR_READ";

  /**
   * 易买海扫描Action
   */
   public static String YMH = "com.ge.action.barscan";

  /**
   * 摩托扫描Action
   */
  public static  String ACTION_SOFTSCANTRIGGER = "com.motorolasolutions.emdk.infusion.broadcast";
  /**
   * 富利叶广播
   */
  public static String M_BROADCASTNAME = "com.barcode.sendBroadcast";
  /**
   * s200扫描头
   */
  public static String RECEIVER_SCAN_RESULT_S200 = "com.sim.action.SIMSCAN";

  public static String QCOM_MX50 = "com.android.server.scannerservice.broadcast";
  public static String QCOM_PEIYE = "com.peiye.action.broadcast";

  /**
   * 联新pda
   */
  public static String LACH_SIS = "lachesis_barcode_value_notice_broadcast";

  /**
   * 开启日志
   */
  public static boolean LOG_ENABLE = true;
  /**
   * 病人的2维码
   */
  public static String QRCODE_PATIENT = "wd";
  /**
   * 配液的2维码
   */
  public static String QRCODE_SY_ADVICE = "sy";
  /**
   * 叫号的一维码
   */
  //    public static String QRCODE_JH = "jh";

  /**
   * 服务器端拿到的数据
   */
  public static int IS_FROM_SERVER = -1;
  /**
   * 未上传
   */
  public static int UN_UPLOAD = 0;
  /**
   * 已上传
   */
  public static int UPLOADED = 1;

    /**
     * 正在上传中。。
     */
  public static int UPLOADEDING = 2;

  public static final String CLICK_EVENT = "click";

  public static final String SCAN_EVENT = "scan";

  /**
   * 默认的配置文件
   */
  public static int DEFAUL_CONFIG_XML = R.xml.app_config;

  /**
   * 默认的设置配置文件名称
   */
  public static int DEFAUL_SEETING_XML = R.xml.pref_general;

  /**
   * 初始值
   */
  public static int INIT_VALUE = -1;

  /**
   * 初始值
   */
  public static String PID_INIT_VALUE = "";

  /**
   * 蓝牙打印机MAC地址
   */

  public static  String BlueToothAddress = "BE:03:00:1F:00:17";

  public static final String RETURNSUCCESS = "com.fugao.android.success";

  public static final String NOTIFYADAPTER="com.fugao.android.notifyadapter";

  public static final String SETNO = "com.fugao.android.setNo";

  public static final String REFRESHSEATNO = "com.fugao.android.refreshSeatNo";



  public static boolean flag=true;
  public static boolean flag2=true;

  public static ArrayList<InfusionAreaBean> infusionAreaBeanList =null;
    /**
     * 科室方法名
     */
  public static String GETDEPARTMENTS = "getDepartments";
    /**
     * 区域列表方法名
     */
    public static String GETAREALIST = "getAreaList";
    public static String LASTUPDATETIME = "lastUpdateTime";
    public static String LASTUPDATETIMEFLAG = "0";




    public static final String SETTIMERECIVER = "com.fugao.android.setTime";

    public static String lastUpdateTime="";
  /**
   * 当前病人门诊号
   */
  public static String CURRENTPATIENT="";
}
