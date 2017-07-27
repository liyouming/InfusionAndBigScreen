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

import java.io.Serializable;



public class EmptySeatModel  implements Serializable{
    private static final String TAG = "Fugao-EmptySeatNoInfoBean";

    @JsonProperty
    public int Id;
    /**
     * 输液ID
     */
    @JsonProperty
    public int InfusionId;
    @JsonProperty
    public String PatId;
    @JsonProperty
    public String DepartmentId;
    /**
     * 座位号
     */
    @JsonProperty
    public String SeatNo;
    @JsonProperty
    public int naturece;
    @JsonProperty
    public String Remark;
    @JsonProperty
    public String ButtonId;
    @JsonProperty
    public String Area;
}
