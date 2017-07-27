package com.fugao.infusion.dao;

import android.provider.BaseColumns;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.dao.tableinfo.WorkloadColumns
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/23 16:05
 * @Modify-date: 修改日期
 * @Modify-description: TODO 工作量的字段
 * @Modify-author: 修改人
 * @version: V1.0
 */

public abstract class WorkloadColumns implements BaseColumns {
  public static final String Date = "Date";  //日期
  public static final String Catalog = "Catalog";  //分类
  public static final String PeopleId = "PeopleId";  //人员ID
  public static final String DjCount = "DjCount";  //等级次数
  public static final String DoseCount = "DoseCount";  //冲配次数
  public static final String PaiyaoCount = "PaiYaoCount";  //排药次数
  public static final String InfusionCount = "InfusionCount";  //输液次数
  public static final String PatrolCount = "PatrolCount";  //巡视次数
  public static final String PunctureCount = "PunctureCount";  //穿刺次数
  public static final String CallOver = "CallOver";  //处理呼叫次数
  public static final String DoneCount = "DoneCount";  //完成次数
  public static final String InvalidOver = "InvalidOver";  //作废次数
  public static final String SiginInTime = "SiginInTime";  //签到时间
  public static final String SiginOutTime = "SiginOutTime";  //签出时间
}
