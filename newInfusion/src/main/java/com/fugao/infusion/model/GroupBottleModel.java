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

import java.util.List;


public class GroupBottleModel {

  private static final String TAG = "Fugao-PeopleInfusionBean";

  /**
   * 门诊号
   */
  public String PatId;

  /**
   * 流水号
   */
  public String Lsh;

  /**
   * 座位号
   */
  public String SeatNo;

  /**
   * 病人姓名
   */
  public String Name;

  /**
   * 性别
   */
  public int Sex;

  /**
   * 年龄
   */
  public String Age;

  /**
   * 体重
   */
  public String Weight;

  /**
   * 过敏史
   */
  public String DrugAllergy;

  /**
   * 输液集合
   */
  public List<BottleModel> items;

  /**
   * 状态 等待0 呼叫1 结束2 全部结束3
   */
  public int Status;

  /**
   * 留观室标示 1-需要留观
   */
  public int ObserveFlag;

  /**
   * 瓶贴数量
   */
  public int bottletotle;

}
