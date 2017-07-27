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

import java.util.List;

/**
 * 账户基类
 *
 * @author findchen TODO 2013-8-7下午4:53:37
 */
// TODO 部分字段未对应
public class AccountModel {
  /**
   * 类型
   */
  public String RoleIds;
  /**
   * 序号
   */
  @JsonProperty
  public String Id;
  /**
   * 科室代码
   */
  @JsonProperty
  public String DeptCode;
  // 性别
  @JsonProperty
  private String Gender;
  // 出生日期
  @JsonProperty
  private String Birthday;
  /**
   * 姓名
   */
  @JsonProperty
  public String FullName;
  /**
   * 工号
   */
  @JsonProperty
  public String UserName;

  // 手机号码
  private String PhoneNumber;
  // 创建日期
  private String CreatedAt;
  /**
   * 当前服务器的时间，用来判断本地时间和服务器端时间的差异性
   */
  @JsonProperty
  public String NowDateTime;
  /**
   * 最新系统的信息
   */
  @JsonProperty
  public AppInfoModel AppInfo;
  @JsonProperty
  public List<UserRoleModel> Competence;
}
