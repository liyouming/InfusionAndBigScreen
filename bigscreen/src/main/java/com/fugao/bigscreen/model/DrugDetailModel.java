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

package com.fugao.bigscreen.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;


public class DrugDetailModel implements Serializable {

    /**
     * 瓶贴ID
     */
    @JsonProperty
    public String BottleId;

    /**
     * 明细Id
     */
    public String DetailId;

    /**
     * 流水号
     */
    public int LSH;

    /**
     * 组号
     */
    public String GroupId;

    /**
     * 处方ID
     */
    public int PrescriptionId;

    /**
     * 序号
     */
    public int Number;

    /**
     * 项目ID
     */
    public int ItemId;

    /**
     * 项目名称
     */
    public String ItemName;

    /**
     * 规格
     */
    public String Standard;

    /**
     * 数量
     */
    public float Amount;

    /**
     * 单位
     */
    public String Unit;

    /**
     * 单次剂量
     */
    public float EveryAmount;

    /**
     * 剂量单位
     */
    public String AmountUnit;

    /**
     * 备注
     */
    public String Remark;

    /**
     * 退药
     */
    public int ReturnFlag;


}
