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

package com.fugao.infusion.peiye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.chuaici.DrugBatchAdapter;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugsModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.StringUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.adapter.BottlesExpandAdapter
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/2 23:31
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 *
 * 配液界面
 */

public class PeiYeGroupBottleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
  private static final String TAG = "Fast-BottlesExpandAdapter";

  private Context context;
  private List<GroupBottleModel> items;
  private LayoutInflater inflater;
  private List<BottleModel> newBottles ;//取消完成瓶贴集合
  private PeiYeActivity activity;
  public PeiYeGroupBottleAdapter(Context context, List<GroupBottleModel> items) {
      this.context = context;
      activity = (PeiYeActivity)context;
      this.items = items;
      inflater = LayoutInflater.from(context);
  }

  @Override public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild,
      View convertView, ViewGroup parent) {

    ChildHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.list_patients_item_son, parent, false);
      holder = new ChildHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ChildHolder) convertView.getTag();
    }
    final GroupBottleModel group = items.get(groupPosition);
    final BottleModel bottle = group.items.get(childPosition);
    holder.listView.setAdapter(new DrugBatchAdapter(context, bottle.DrugDetails));
    ListViewUtils.setListViewHeightBasedOnChildren(holder.listView);
    holder.txtSeatNo.setText(
        StringUtils.StringIsEmpty(bottle.SeatNo) ? "无" : bottle.PeopleInfo.SeatNo);

        getStatusAndShow(bottle,holder.txtSeatNo);
        holder.btnMore.setVisibility(View.VISIBLE);//穿刺界面按钮放开

    holder.btnMore.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {    //暂时注释
//            activity.peiyePeopleFragment.redirect2Execute(bottle);
        }
    });

    return convertView;
  }

    /**
     * 显示状态
     */
    private void getStatusAndShow(BottleModel bottle, TextView textview) {
        String status = null;
        if(BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus){
            status = "未排";
        }else if(BottleStatusCategory.HADHANDLE.getKey() == bottle.BottleStatus){
            status = "未配";
        }else if(BottleStatusCategory.WAITINGINFUSE.getKey() == bottle.BottleStatus){
            status = "执行";
        }else {
            textview.setVisibility(View.GONE);
        }
        textview.setText(status);
    }

  /**
   * 跳转到批量执行界面
   *
   */
 /* private void redirect2ExecuteBatch(BottleModel bottle) { 暂时注释
    Intent intent = new Intent(context, ExecuteBatchActivity.class);
    intent.putExtra("pid", bottle.PeopleInfo.PatId);
    intent.putExtra("bid", bottle.BottleId);
    intent.putExtra("iid", bottle.InfusionId);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
    activity.overridePendingTransition(R.anim.dialog_in, R.anim.dialog_out);
  }
*/
  @Override public int getRealChildrenCount(int groupPosition) {
    return items.get(groupPosition).items.size();
  }

  @Override public int getGroupCount() {
    return items.size();
  }

  @Override public GroupBottleModel getGroup(int groupPosition) {
    return items.get(groupPosition);
  }

  @Override public BottleModel getChild(int groupPosition, int childPosition) {
    return items.get(groupPosition).items.get(childPosition);
  }

  @Override public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override public boolean hasStableIds() {
    return true;
  }

  @Override public View getGroupView(int groupPosition, boolean b, View convertView,
      ViewGroup parent) {
    FatherHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.list_patients_item_father, parent, false);
      holder = new FatherHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (FatherHolder) convertView.getTag();
    }
    GroupBottleModel item = getGroup(groupPosition);
    /**
     * 姓名
     */
    holder.patients_head.setText(item.Name);
    if (1 == item.Sex) {
      holder.patients_head.setBackgroundResource(R.drawable.patients_man_head);
    } else {
      holder.patients_head.setBackgroundResource(R.drawable.patients_woman_head);
    }

    if (StringUtils.StringIsEmpty(item.SeatNo)) {
      holder.patient_content_seatno.setVisibility(View.INVISIBLE);
    } else {
        holder.patient_content_seatno.setVisibility(View.VISIBLE);
        if(!StringUtils.getString(item.SeatNo).contains("none")){
            holder.patient_content_seatno.setText(item.SeatNo);
        }else {
            holder.patient_content_seatno.setText("");
        }
    }
    holder.patient_content_old.setText(item.Age);
    holder.patient_content_mzh.setText(item.PatId);
    holder.patient_content_lsh.setText(item.Lsh);
    holder.patient_content_tz.setText(item.Weight);

    if (StringUtils.StringIsEmpty(item.DrugAllergy)) {
      holder.patient_content_gms.setText("无");
      holder.patient_content_gms.setTextColor(context.getResources().getColor(R.color.darkgray));
    } else {
      holder.patient_content_gms.setText(item.DrugAllergy);
      holder.patient_content_gms.setTextColor(context.getResources().getColor(R.color.holo_red));
    }
    if (isFinished(item.items)) {
      holder.patients_sign.setVisibility(View.VISIBLE);
    } else {
      holder.patients_sign.setVisibility(View.GONE);
    }
    return convertView;
  }

  /**
   * 是否完成
   */
  private boolean isFinished(List<BottleModel> items) {
      if (RoleCategory.PEIYE.getKey() == LocalSetting.RoleIndex) {
          for (BottleModel item : items) {
              if (QueueStatusCategory.FINISHED.getKey() == item.PeopleInfo.Status) {
                  return true;
              }
          }
          return false;
      }
    return true;
  }

  @Override public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }

  public void updateDrugs(String pid, List<DrugsModel> drugs) {
    for (GroupBottleModel item : items) {
      if (pid.equals(item.PatId)) {
        for (int index = 0; index < item.items.size(); index++) {
          for (DrugsModel each : drugs) {
            if (item.items.get(index).BottleId.equals(each.BottleId)) {
              item.items.get(index).DrugDetails = each.Drugs;
            }
          }
        }
      }
    }
    notifyDataSetChanged();
  }

  static class ChildHolder {
    @InjectView(R.id.txtSeatNo) TextView txtSeatNo;
    @InjectView(R.id.sonList) ListView listView;
    @InjectView(R.id.btnMore) Button btnMore;

    public ChildHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  static class FatherHolder {
    @InjectView(R.id.patients_head) TextView patients_head;
    @InjectView(R.id.patient_content_seatno) TextView patient_content_seatno;
    @InjectView(R.id.patient_content_old) TextView patient_content_old;
    @InjectView(R.id.patient_content_mzh) TextView patient_content_mzh;
    @InjectView(R.id.patient_content_lsh) TextView patient_content_lsh;
    @InjectView(R.id.patient_content_tz) TextView patient_content_tz;
    @InjectView(R.id.patient_content_gms) TextView patient_content_gms;
    @InjectView(R.id.patients_sign) ImageView patients_sign;

    public FatherHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
