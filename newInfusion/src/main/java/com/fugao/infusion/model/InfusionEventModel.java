package com.fugao.infusion.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.model.InfusionEventModel
 * @Description: TODO 主要是当PDA端扫描病人信息核对错误时记录错误发送给服务器
 * @author: 胡乐    hule@fugao.com
 * @date: 2015/3/3 17:44
 * @version: V1.0
 */

public class InfusionEventModel {
    private static final String TAG = "Fugao-InfusionEventModel";

    @JsonProperty
    /**
     * 序号
     */
    public int Id;

    @JsonProperty
    /**
     * 项目
     */
    public String Item;

    @JsonProperty
    /**
     * 操作人姓名
     */
    public String OperateName;

    @JsonProperty
    /**
     * 操作人ID
     */
    public String OperateId;

    @JsonProperty
    /**
     * 附加字段
     */
    public String AdditionId;

    @JsonProperty
    /**
     * 目标ID
     */
    public String TargetId;

    @JsonProperty
    /**
     * 日期
     */
    public String Date;

    @JsonProperty
    /**
     * 时间
     */
    public String Time;

    @JsonProperty
    /**
     * 状态
     */
    public int Status;

    @JsonProperty
    /**
     * 备注
     */
    public String Memo;

}
