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

import android.os.Parcel;
import android.os.Parcelable;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;


public class DepartmentModel implements Parcelable {
  /**
   * 父类的ID
   */
  @JsonProperty
  public String Parent;

  /**
   * 部门的序号
   */
  @JsonProperty
  public String Id;

  /**
   * 名称
   */
  @JsonProperty
  public String Name;

  /**
   * 子部门
   */
  @JsonProperty
  public ArrayList<DepartmentModel> ChildDepts;

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(Parent);
    dest.writeString(Id);
    dest.writeString(Name);
    dest.writeList(ChildDepts);
  }

  public static final Creator<DepartmentModel> CREATOR = new Creator<DepartmentModel>() {

    @Override
    public DepartmentModel createFromParcel(Parcel source) {
      DepartmentModel departmentModel = new DepartmentModel();
      departmentModel.Parent = source.readString();
      departmentModel.Id = source.readString();
      departmentModel.Name = source.readString();
      // departmentBean.ChildDepts=source.readArrayList(this.getClass().getClassLoader());
      return departmentModel;
    }

    @Override
    public DepartmentModel[] newArray(int size) {
      // TODO Auto-generated method stub
      return new DepartmentModel[size];
    }
  };
}
