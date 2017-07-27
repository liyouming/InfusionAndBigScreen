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
 * Created by Andy on 2014/7/3.
 */
public enum QueueStatusCategory {

  WAITING(0, "等待穿刺"), CALLED(1, "呼叫其来穿刺"), FINISHED(2, "穿刺完成"), GAMEOVER(3, "完成本次输液疗程");

  private int key;
  private String value;

  private QueueStatusCategory(int key, String value) {
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
    for (QueueStatusCategory category : QueueStatusCategory.values()) {
      if (category.getValue().equals(value)) {
        return category.getKey();
      }
    }
    return -1;
  }

  public static String getValueByKey(int key) {

    for (QueueStatusCategory category : QueueStatusCategory.values()) {
      if (category.getKey() == key) {
        return category.getValue();
      }
    }
    return "无效类别";
  }
}