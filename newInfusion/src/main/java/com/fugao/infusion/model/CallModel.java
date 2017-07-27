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

package com.fugao.infusion.model;

import org.codehaus.jackson.annotate.JsonProperty;



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
