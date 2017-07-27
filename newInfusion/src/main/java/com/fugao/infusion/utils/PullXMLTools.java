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

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * @ClassName: PullXMLTools
 * @Description: TODO
 * @author: 陈亮    chenliang@fugao.com
 * @date: 2013年12月13日 下午3:38:18
 */
public class PullXMLTools {
  /**
   * 解析xml文件
   *
   * @param xmlID xml文件的int值
   * @param tagIDNam 节点中 属性为id的值
   * @Title: parseXml
   * @Description: TODO
   */
  public static String parseXml(Context context, int xmlID, String tagIDNam) {
    String response = "";
    XmlResourceParser xrp = context.getResources().getXml(xmlID);
    try {
      // 直到文档的结尾处
      while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
        // 如果遇到了开始标签
        if (xrp.getEventType() == XmlResourceParser.START_TAG) {
          String tagName = xrp.getName();// 获取标签的名字
          if ("module".equals(tagName)) {
            String id = xrp.getAttributeValue(null, "id");// 通过属性名来获取属性值
            if (tagIDNam.equals(id)) {
              response = xrp.nextText();
            }
          }
        }
        xrp.next();// 获取解析下一个事件
      }
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return response;
  }
}
