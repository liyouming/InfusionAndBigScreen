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

package com.fugao.infusion.comonPage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.utils.InfoUtils;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.adapter.TabFragmentAdapter
 * @Description: TODO 主界面tab适配器 能够保存各fragment的状态（Hide,show）
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/9 13:39
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */
public class TabFragmentAdapter implements RadioGroup.OnCheckedChangeListener {
  /**
   * 一个tab页面对应多个Fragment
   */
  private List<Fragment> fragments;
  /**
   * 用于切换tab
   */
  private RadioGroup rgs;
  /**
   * Fragment所属的Activity
   */
  private FragmentActivity fragmentActivity;
  /**
   * Activity中所要被替换的区域的id
   */
  private int fragmentContentId;
  /**
   * 当前Tab页面索引
   */
  private int currentTab = 0;
    /**
     * 程序默认进来是 未完成 拼贴列表 0-全部 1-未完成 2-已完成
     */
    private static final int CATOGRY_ALL = 0;
    private static final int CATOGRY_UNDO = 1;
    private static final int CATOGRY_DONE = 2;
    private TextView fragmentTitle;

  /**
   * 用于让调用者在切换tab时候增加新的功能
   */
  private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener;

  public TabFragmentAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments,
      int fragmentContentId, RadioGroup rgs) {
    this.fragments = fragments;
    this.rgs = rgs;
    this.fragmentActivity = fragmentActivity;
    this.fragmentContentId = fragmentContentId;
//    fragmentTitle = ((PaiYaoPeopleFragment)fragments.get(0)).mChuanchiTitle;

      /**
     * 默认显示第一页
     */
    FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
    Fragment f = fragments.get(0);
    ft.add(fragmentContentId, f);
    ft.commit();

    /**
     * 第一项默认radioGro为选中状态
     */
    rgs.check(rgs.getChildAt(0).getId());
    rgs.setOnCheckedChangeListener(this);
  }

  @Override public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
    for (int i = 0; i < rgs.getChildCount(); i++) {
      if (rgs.getChildAt(i).getId() == checkedId) {
//          String str_actionBar = ((RadioButton)rgs.getChildAt(i)).getText().toString();
//          if("列表".equals(str_actionBar)){
//              loadData(((PaiYaoActivity)fragmentActivity).getDefaultCatogry());
//          }else {
//              fragmentTitle.setText(str_actionBar);
//          }
        Fragment fragment = fragments.get(i);
        FragmentTransaction ft = obtainFragmentTransaction(i);

        getCurrentFragment().onPause(); // 暂停当前tab
        //                getCurrentFragment().onStop(); // 暂停当前tab

        if (fragment.isAdded()) {
          //                    fragment.onStart(); // 启动目标tab的onStart()
          fragment.onResume(); // 启动目标tab的onResume()
        } else {
          ft.add(fragmentContentId, fragment);
        }
        showTab(i); // 显示目标tab
        ft.commit();

        // 如果设置了切换tab额外功能功能接口
        if (null != onRgsExtraCheckedChangedListener) {
          onRgsExtraCheckedChangedListener.OnRgsExtraCheckedChanged(radioGroup, checkedId, i);
        }
      }
    }
  }

//    private void  loadData(int position) {
//        fragmentTitle.setText(InfoUtils.getCurrentRoleName() + "-" + getCurrentCatogryText(position));
//    }
    /**
     * 得到当前所选分类
     */
    private String getCurrentCatogry(int position) {
        switch (position) {
            case CATOGRY_ALL:
                return InfoUtils.getAllStatusGroup(LocalSetting.RoleIndex);
            case CATOGRY_UNDO:
                return InfoUtils.getUndoStatusGroup(LocalSetting.RoleIndex);
            case CATOGRY_DONE:
                return InfoUtils.getDoneStatusGroup(LocalSetting.RoleIndex);
        }
        return "";
    }
    /**
     * 得到当前所选分类
     */
    private String getCurrentCatogryText(int position) {
        switch (position) {
            case CATOGRY_ALL:
                return "全部";
            case CATOGRY_UNDO:
                return "未完成";
            case CATOGRY_DONE:
                return "已完成";
        }
        return "";
    }

  /**
   * 切换tab
   */
  private void showTab(int idx) {
    for (int i = 0; i < fragments.size(); i++) {
      Fragment fragment = fragments.get(i);
      FragmentTransaction ft = obtainFragmentTransaction(idx);

      if (idx == i) {
        ft.show(fragment);
      } else {
        ft.hide(fragment);
      }
      ft.commit();
    }
    currentTab = idx; // 更新目标tab为当前tab
  }

  /**
   * 获取一个带动画的FragmentTransaction
   */
  private FragmentTransaction obtainFragmentTransaction(int index) {
    FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
    // 设置切换动画
    if (index > currentTab) {
      ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
    } else {
      ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
    }
    return ft;
  }

  public int getCurrentTab() {
    return currentTab;
  }

  public Fragment getCurrentFragment() {
    return fragments.get(currentTab);
  }

  public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
    return onRgsExtraCheckedChangedListener;
  }

  public void setOnRgsExtraCheckedChangedListener(
      OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
    this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
  }

  /**
   * 切换tab额外功能功能接口
   */
  public static class OnRgsExtraCheckedChangedListener {
    public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {

    }
  }
}
