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

/**
 * 应用程序信息
 *
 * @author findchen TODO 2013-9-16下午3:06:50
 */
public class AppInfoModel implements Parcelable {
  @JsonProperty
  public String Id;
  /**
   * 名称
   */
  @JsonProperty
  public String Name;
  /**
   * 代码
   */
  @JsonProperty
  public String ByName;
  /**
   * 版本号
   */
  @JsonProperty
  public String Version;
  /**
   * 服务器端地址
   */
  @JsonProperty
  public String Path;
  /**
   * 操作系统
   */
  @JsonProperty
  public String OperateSystem;
  /**
   * 启用科室
   */
  @JsonProperty
  public String EnableDept;
  /**
   * 状态
   */
  @JsonProperty
  public String Status;
  /**
   * 更新日志
   */
  @JsonProperty
  public String Note;
  /**
   * 更新时间
   */
  @JsonProperty
  public String UpdateDate;
  /**
   * 程序包名
   */
  @JsonProperty
  public String packageName;

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(Id);
    dest.writeString(Name);
    dest.writeString(ByName);
    dest.writeString(Version);
    dest.writeString(Path);
    dest.writeString(OperateSystem);
    dest.writeString(EnableDept);
    dest.writeString(Status);
    dest.writeString(Note);
    dest.writeString(UpdateDate);
  }

  public static final Creator<AppInfoModel> CREATOR = new Creator<AppInfoModel>() {

    @Override
    public AppInfoModel[] newArray(int size) {
      // TODO Auto-generated method stub
      return new AppInfoModel[size];
    }

    @Override
    public AppInfoModel createFromParcel(Parcel source) {
      AppInfoModel bean = new AppInfoModel();
      bean.Id = source.readString();
      bean.Name = source.readString();
      bean.ByName = source.readString();
      bean.Version = source.readString();
      bean.Path = source.readString();
      bean.OperateSystem = source.readString();
      bean.EnableDept = source.readString();
      bean.Status = source.readString();
      bean.Note = source.readString();
      bean.UpdateDate = source.readString();
      return bean;
    }
  };
}
