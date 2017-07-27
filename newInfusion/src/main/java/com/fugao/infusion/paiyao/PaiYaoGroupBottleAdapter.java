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

package com.fugao.infusion.paiyao;

import android.annotation.SuppressLint;
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
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugsModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.StringUtils;

import java.util.List;

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

public class PaiYaoGroupBottleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
  private static final String TAG = "Fast-BottlesExpandAdapter";

  private Context context;
  private List<GroupBottleModel> items;
  private LayoutInflater inflater;
  private List<BottleModel> newBottles ;//取消完成瓶贴集合
  private PaiYaoActivity activity;
//  private Bitmap bitmapBoy =null;
//  private Bitmap bitmapGirl =null;
  public PaiYaoGroupBottleAdapter(Context context, List<GroupBottleModel> items) {
      this.context = context;
      activity = (PaiYaoActivity)context;
      this.items = items;
      inflater = LayoutInflater.from(context);
//      bitmapBoy = BitmapFactory.decodeResource(context.getResources(), R.drawable.patients_man_head);
//      bitmapGirl = BitmapFactory.decodeResource(context.getResources(), R.drawable.patients_woman_head);
  }

  @Override public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild,
      View convertView, ViewGroup parent) {

    ChildHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.list_patients_item_son, parent, false);
      holder = new ChildHolder();
      holder.txtSeatNo = (TextView) convertView.findViewById(R.id.txtSeatNo);
      holder.listView = (ListView) convertView.findViewById(R.id.sonList);
      holder.btnMore = (Button) convertView.findViewById(R.id.btnMore);
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
            activity.paiYaoPeopleFragment.redirect2ExecuteSingleByClick(bottle);
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
//            textview.setBackgroundResource(R.drawable.fugao_blue_bg);
        }else if(BottleStatusCategory.WAITINGINFUSE.getKey() == bottle.BottleStatus){
            status = "执行";
//            textview.setBackgroundResource(R.drawable.fugao_blue_bg);
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

  @SuppressLint("NewApi")
  @Override public View getGroupView(int groupPosition, boolean b, View convertView,
      ViewGroup parent) {
    FatherHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.list_patients_item_father, parent, false);
      holder = new FatherHolder();
      holder.patients_head = (TextView) convertView.findViewById(R.id.patients_head);
      holder.patient_content_seatno = (TextView) convertView.findViewById(R.id.patient_content_seatno);
      holder.patient_content_old = (TextView) convertView.findViewById(R.id.patient_content_old);
      holder.patient_content_mzh = (TextView) convertView.findViewById(R.id.patient_content_mzh);
      holder.patient_content_lsh = (TextView) convertView.findViewById(R.id.patient_content_lsh);
      holder.patient_content_tz = (TextView) convertView.findViewById(R.id.patient_content_tz);
      holder.patient_content_gms = (TextView) convertView.findViewById(R.id.patient_content_gms);
      holder.registerDate = (TextView) convertView.findViewById(R.id.registerDate);
      holder.patients_sign = (ImageView) convertView.findViewById(R.id.patients_sign);
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
      holder.patients_head.setBackgroundResource(R.drawable.patients_man_head);//会造成OOM
//          holder.patients_head.setBackground(new BitmapDrawable(context.getResources(), bitmapBoy));//黑色PDA API版本太低，不支持这个方法
    } else {
      holder.patients_head.setBackgroundResource(R.drawable.patients_woman_head);//会造成OOM
//          holder.patients_head.setBackground(new BitmapDrawable(context.getResources(), bitmapGirl));
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
    String registrationDate = item.items.get(0).RegistrationDate;//怕会存在合并不同日期的情况
    String registrationTime = item.items.get(0).RegistrationTime;
    String date =registrationDate.substring(4,6)+"-"+registrationDate.substring(6,8)
            +"  "+registrationTime.substring(0,2)+":"+registrationTime.substring(2,4);
    holder.registerDate.setText(date);
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
    if (RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex) {
      for (BottleModel item : items) {
        if (BottleStatusCategory.WAITINGHANDLE.getKey() == item.BottleStatus) return false;
      }
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

  class ChildHolder {
    TextView txtSeatNo;
    ListView listView;
    Button btnMore;
  }

  class FatherHolder {
    TextView patients_head;
    TextView patient_content_seatno;
    TextView patient_content_old;
    TextView patient_content_mzh;
    TextView patient_content_lsh;
    TextView patient_content_tz;
    TextView patient_content_gms;
    ImageView patients_sign;
    TextView registerDate;
  }

//    /**
//     * 回收BItmap
//     */
//  public void cyleBitmap(){
//      if(bitmapBoy !=null && !bitmapBoy.isRecycled()){
//          bitmapBoy.recycle();
//          bitmapBoy = null;
//      }
//      if(bitmapGirl !=null && !bitmapGirl.isRecycled()){
//          bitmapGirl.recycle();
//          bitmapGirl = null;
//      }
//  }
}
