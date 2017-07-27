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

package com.fugao.newbigscreen.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 输液队列
 */

public class QueueModel {

    /**
     * 日期
     */
    @JsonProperty
    public String Date;
    /**
     * 流水号
     */
    @JsonProperty
    public String QueueNo;
    /**
     * 科室ID
     */
    @JsonProperty
    public String DepartmentId;
    /**
     * 门诊号
     */
    @JsonProperty
    public String PatId;
    /**
     * 姓名
     */
    @JsonProperty
    public String Name;

    /**
     * 年龄
     */
    @JsonProperty
    public String Age;

    /**
     * 座位号
     */
    @JsonProperty
    public String SeatNo;

    /**
     * 性别
     */
    @JsonProperty
    public int Sex;
    /**
     * 状态 等待0 呼叫1 结束2 全部结束3
     * fgv1|jh|pid
     */
    @JsonProperty
    public int Status;
    /**
     * 卡信息ID
     */
    @JsonProperty
    public int CardId;
    /**
     * 卡号
     */
    @JsonProperty
    public String CarCore;

    /**
     * 是否上传
     */
    public int IsUpload;

    /**
     * 插入队列时间
     */
    @JsonProperty
    public String InsertTime;

    /**
     * 输液表编号
     */
    @JsonProperty
    public String InfusionId;

    /**
     * 体重
     */
    @JsonProperty
    public String Weight;

    /**
     * 过敏史
     */
    @JsonProperty
    public String DrugAllergy;

    /**
     * 呼叫次数
     */
    @JsonProperty
    public int callCount;

    /**
     * 留观室标示 1-需要留观
     */
    @JsonProperty
    public int ObserveFlag;

    /**
     * 是否过号 默认为0 1为过号
     */
    @JsonProperty
    public int OverCall;

    /**
     * 发票号
     */
    @JsonProperty
    public String Ticket;

    /**
     * 门诊诊断
     */
    @JsonProperty
    public String Detective;
}
