package com.fugao.infusion.model;

import org.codehaus.jackson.annotate.JsonProperty;


public class UserDetailTimelineModel extends BottleModel {
    private static final String TAG = "Fugao-TimeTwoStatusModel";
    /**
     * 登记工号
     */
    @JsonProperty
    public String RegistrationCore;
    /**
     * 登记日期
     */
    @JsonProperty
    public String RegistrationDate;
    /**
     * 登记时间
     */
    @JsonProperty
    public String RegistrationTime;
    /**
     * 排药日期
     */
    @JsonProperty
    public String PillDate;
    /**
     * 排药时间
     */
    @JsonProperty
    public String PillTime;
    /**
     * 排药工号
     */
    @JsonProperty
    public String PillCore;
    /**
     * 配液日期
     */
    @JsonProperty
    public String LiquorDate;
    /**
     * 配液时间
     */
    @JsonProperty
    public String LiquorTime;
    /**
     * 配液工号
     */
    @JsonProperty
    public String LiquorCore;
    /**
     * 输液日期
     */
    @JsonProperty
    public String InfusionDate;
    /**
     * 输液时间
     */
    @JsonProperty
    public String InfusionTime;
    /**
     * 输液工号
     */
    @JsonProperty
    public String InfusionCore;
    /**
     * 结束日期
     */
    @JsonProperty
    public String EndDate;
    /**
     * 结束时间
     */
    @JsonProperty
    public String EndTime;
    /**
     * 结束工号
     */
    @JsonProperty
    public String EndCore;

    /* 状态名称 */
    private String statusName;
    /* 预计完成时间 */
    private String completeTime;
    /* 是否已完成 */
    private boolean isfinished; //0为未完成，1为已完成

    public String getStatusName() {
        return statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    public String getCompleteTime() {
        return completeTime;
    }
    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }
    public boolean isIsfinished() {
        return isfinished;
    }
    public void setIsfinished(boolean isfinished) {
        this.isfinished = isfinished;
    }



}
