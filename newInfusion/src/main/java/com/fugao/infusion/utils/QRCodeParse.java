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
package com.fugao.infusion.utils;

import com.fugao.infusion.constant.Constant;

import java.util.HashMap;

/**
 * @ClassName: QCCodeParse
 * @Description: TODO 二维码解析规则
 * @author: 陈亮 chenliang@fugao.com
 * @date: 2013年12月27日 上午9:10:09
 */
public class QRCodeParse {

  /**
   * 得到2维码的类型
   *
   * 1.病人唯一标示 2.输液标签
   *
   * @Title: getCodeTypeAndValue
   * @Description: TODO
   * @return: HashMap<String,String>
   */
  public static HashMap<String, String> getCodeTypeAndValue(String string) {

    String[] qrCode = string.split("\\|");
    HashMap<String, String> strs = new HashMap<String, String>();

    if (qrCode.length >= 2) {
      if (Constant.QRCODE_PATIENT.equals(qrCode[0])) {
        strs.put(Constant.QRCODE_PATIENT, qrCode[1]);
      } else if (Constant.QRCODE_SY_ADVICE.equals(qrCode[0])) {
        strs.put(Constant.QRCODE_SY_ADVICE, qrCode[1] + "_" + qrCode[2] + "_" + qrCode[3]);
      }
    }
    return strs;
  }
}
