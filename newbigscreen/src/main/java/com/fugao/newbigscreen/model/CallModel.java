package com.fugao.newbigscreen.model;

import org.codehaus.jackson.annotate.JsonProperty;

/** 呼叫队列
 * Created by li on 2016/6/18.
 */
public class CallModel {
    /**
     * 流水号
     */
    @JsonProperty
    public String LSH_Call;

    /**
     * 呼叫类型
     */
    @JsonProperty
    public int CallType;

    /**
     * 呼叫座位号
     */
    @JsonProperty
    public String CallContent;

    /**
     * 呼叫日期
     */
    @JsonProperty
    public String CallDate;

    /**
     * 呼叫时间
     */
    @JsonProperty
    public String CallTime;

    /**
     * 备注
     */
    @JsonProperty
    public String Remark;

    /**
     * 状态
     */
    @JsonProperty
    public int Status;

    /**
     * 操作工号
     */
    @JsonProperty
    public String OperateId;

    /**
     * 关联ID
     */
    @JsonProperty
    public String RelevanceId;
    /**
     * 关联人姓名
     */
    @JsonProperty
    public String RelevanceName;

    /**
     * 显示呼叫时间秒
     */
    @JsonProperty
    public int TimeSecond;

    /**
     * 显示呼叫时间分
     */
    @JsonProperty
    public int TimeMinute;

    /**
     * 输液状态 0为未输液 1为正在输液
     */
    @JsonProperty
    public int InfusionStatus;

    @JsonProperty
    public int TransfusionBulk;
    /**
     * 输液滴速
     */
    @JsonProperty
    public int TransfusionSpeed;

    @JsonProperty
    public String InfusionId;
}
