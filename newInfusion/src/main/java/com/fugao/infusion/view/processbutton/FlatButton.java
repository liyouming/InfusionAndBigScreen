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

package com.fugao.infusion.view.processbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.fugao.infusion.R;

public class FlatButton extends Button {

  private StateListDrawable mNormalDrawable;
  private CharSequence mNormalText;
  private float cornerRadius;

  public FlatButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs);
  }

  public FlatButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public FlatButton(Context context) {
    super(context);
    init(context, null);
  }

  private void init(Context context, AttributeSet attrs) {
    mNormalDrawable = new StateListDrawable();
    if (attrs != null) {
      initAttributes(context, attrs);
    }
    mNormalText = getText().toString();
    setBackgroundCompat(mNormalDrawable);
  }

  private void initAttributes(Context context, AttributeSet attributeSet) {
    TypedArray attr = getTypedArray(context, attributeSet, R.styleable.FlatButton);
    if (attr == null) {
      return;
    }

    try {

      float defValue = getDimension(R.dimen.corner_radius);
      cornerRadius = attr.getDimension(R.styleable.FlatButton_pb_cornerRadius, defValue);

      mNormalDrawable.addState(new int[] { android.R.attr.state_pressed },
          createPressedDrawable(attr));

      Drawable normalDefault = this.getResources().getDrawable(R.drawable.rect_normal);
      Drawable normalOwn = attr.getDrawable(R.styleable.FlatButton_pb_normalDrawable);

      mNormalDrawable.addState(new int[] { }, normalOwn == null ? normalDefault : normalOwn);
    } finally {
      attr.recycle();
    }
  }

  private Drawable createPressedDrawable(TypedArray attr) {
    GradientDrawable drawablePressed =
        (GradientDrawable) getDrawable(R.drawable.rect_pressed).mutate();
    drawablePressed.setCornerRadius(getCornerRadius());

    int blueDark = getColor(R.color.blue_pressed);
    int colorPressed = attr.getColor(R.styleable.FlatButton_pb_colorPressed, blueDark);
    drawablePressed.setColor(colorPressed);

    return drawablePressed;
  }

  protected Drawable getDrawable(int id) {
    return getResources().getDrawable(id);
  }

  protected float getDimension(int id) {
    return getResources().getDimension(id);
  }

  protected int getColor(int id) {
    return getResources().getColor(id);
  }

  protected TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
    return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
  }

  public float getCornerRadius() {
    return cornerRadius;
  }

  public StateListDrawable getNormalDrawable() {
    return mNormalDrawable;
  }

  public CharSequence getNormalText() {
    return mNormalText;
  }

  /**
   * Set the View's background. Masks the API changes made in Jelly Bean.
   */
  @SuppressWarnings("deprecation") @SuppressLint("NewApi")
  public void setBackgroundCompat(Drawable drawable) {
    int pL = getPaddingLeft();
    int pT = getPaddingTop();
    int pR = getPaddingRight();
    int pB = getPaddingBottom();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      setBackground(drawable);
    } else {
      setBackgroundDrawable(drawable);
    }
    setPadding(pL, pT, pR, pB);
  }
}
