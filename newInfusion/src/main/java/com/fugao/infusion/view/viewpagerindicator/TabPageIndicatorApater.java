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

package com.fugao.infusion.view.viewpagerindicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.adapter.TabPageIndicatorApater
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/9/26 1:31
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class TabPageIndicatorApater extends FragmentPagerAdapter {
  String[] tabTexts = null;
  List<Fragment> tabFragments = null;

  public TabPageIndicatorApater(FragmentManager fm, String[] tabTexts,
      List<Fragment> tabFragments) {
    super(fm);
    this.tabTexts = tabTexts;
    this.tabFragments = tabFragments;
  }

  @Override public Fragment getItem(int position) {
    return tabFragments.get(position);
  }

  @Override public CharSequence getPageTitle(int position) {
    return tabTexts[position];
  }

  @Override public int getCount() {
    return tabTexts.length > tabFragments.size() ? tabFragments.size() : tabTexts.length;
  }
}
