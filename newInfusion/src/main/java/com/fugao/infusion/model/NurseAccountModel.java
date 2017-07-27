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

import java.util.ArrayList;


public class NurseAccountModel extends AccountModel {

  /**
   * 病区 科室对应表
   */
  @JsonProperty
  public ArrayList<DepartmentModel> DeptPermits;

  public String DeptName;

  public String errorMsg;

  /**
   * 加入权限控制
   */
  public String Role;
}
