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

package com.fugao.infusion.view.processbutton.iml;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.fugao.infusion.view.processbutton.ProcessButton;

public class SubmitProcessButton extends ProcessButton {

  public SubmitProcessButton(Context context) {
    super(context);
  }

  public SubmitProcessButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SubmitProcessButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void drawProgress(Canvas canvas) {
    float scale = (float) getProgress() / (float) getMaxProgress();
    float indicatorWidth = (float) getMeasuredWidth() * scale;

    getProgressDrawable().setBounds(0, 0, (int) indicatorWidth, getMeasuredHeight());
    getProgressDrawable().draw(canvas);
  }
}
