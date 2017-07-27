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

package com.fugao.infusion.constant;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.infusion.constant.BottleStatusEmun
 * @Description: TODO 瓶贴状态枚举
 * @author: LoQua xiqiang@fugao.com
 * @date: 2014/6/18 11:44
 * @version: V1.0
 */

public enum BottleStatusCategory {

  ORIGINAL(0, "初始状态"), WAITINGHANDLE(1, "打印待排"), HADHANDLE(2, "已排待配"),

  WAITINGINFUSE(3, "已配待输"), INFUSIONG(4, "输液中"), HADINFUSE(5, "输液完成"),

  CANCEL(6, "作废");

  private int key;

  private String value;

  private BottleStatusCategory(int key, String value) {
    this.key = key;
    this.value = value;
  }

  public int getKey() {
    return key;
  }

  public void setKey(int key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  // 普通方法
  public static int getKeyByValue(String value) {
    for (BottleStatusCategory category : BottleStatusCategory.values()) {
      if (category.getValue().equals(value)) {
        return category.getKey();
      }
    }
    return -1;
  }

  public static String getValueByKey(int key) {

    for (BottleStatusCategory category : BottleStatusCategory.values()) {
      if (category.getKey() == key) {
        return category.getValue();
      }
    }
    return "无效类别";
  }
}
