package com.fugao.infusion.model;

import org.codehaus.jackson.annotate.JsonProperty;


public class WorkloadModel {
  @JsonProperty
  public String Date;  //日期
  @JsonProperty
  public String PeopleId;  //人员ID
  @JsonProperty
  public int DjCount;  //等级次数
  @JsonProperty
  public int DoseCount;  //冲配次数
  @JsonProperty
  public int PaiYaoCount;  //排药次数
  @JsonProperty
  public int InfusionCount;  //输液次数 x
  @JsonProperty
  public int PatrolCount;  //巡视次数
  @JsonProperty
  public int PunctureCount;  //穿刺次数 留置针
    @JsonProperty
  public int PunctureCountN;  //穿刺次数 非留置针
  @JsonProperty
  public int CallOver;  //处理呼叫次数 x
  @JsonProperty
  public int DoneCount;  //完成次数-- 接瓶次数
  @JsonProperty
  public int InvalidOver;  //作废次数 x
  @JsonProperty
  public String SiginInTime;  //签到时间
  @JsonProperty
  public String SiginOutTime;  //签出时间

  public String Catalog;  //分类  一天 一周 一月
}
