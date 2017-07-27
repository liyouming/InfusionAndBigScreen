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

package com.fugao.infusion.view;

import com.fugao.infusion.view.processbutton.ProcessButton;

public class ProgressGenerator {

  private ProcessButton mButton;

  public ProgressGenerator(ProcessButton button) {
    this.mButton = button;
  }

  /**
   * 正在刷新
   */
  public void start() {
    mButton.setProgress(50);
  }

  /**
   * 请求成功
   */
  public void success() {
    mButton.setProgress(100);
  }

  /**
   * 请求失败
   */
  public void fail() {
    mButton.setProgress(0);
  }
}
