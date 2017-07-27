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

package com.fugao.infusion.xunshi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugsModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.model.InfusioningModel;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

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

public class XunshiGroupBottleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
  private static final String TAG = "Fast-BottlesExpandAdapter";

  private Context context;
  private List<GroupBottleModel> items;
  private LayoutInflater inflater;
  private List<BottleModel> newBottles ;//取消完成瓶贴集合
  private XunshiActivity activity;
//  private Bitmap bitmapBoy =null;
//  private Bitmap bitmapGirl =null;
  public XunshiGroupBottleAdapter(Context context, List<GroupBottleModel> items) {
      this.context = context;
      activity = (XunshiActivity)context;
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
            String url = InfoApi.url_infusioningCount(bottle.BottleId, bottle.PeopleInfo.PatId);
            Log.e("=========================正在查找瓶贴", "");
            final long l1 = System.currentTimeMillis();
            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    long l2 = System.currentTimeMillis();
                    Log.d("查询瓶贴成功返回耗时================", "" + (l2 - l1));
                    long l3 = System.currentTimeMillis();
                    InfusioningModel infusioningModel = String2InfusionModel.string2InfusioningModel(s);
                    Log.d("客户端解析耗时耗时================", "" + (l3 - l2));
                    if (infusioningModel.bottle != null) {
                        long l4 = System.currentTimeMillis();
                        activity.xunshiPeopleFragment.redirect2ExecuteSingleByClick(bottle);
                        long l5 = System.currentTimeMillis();
                        Log.d("跳转到执行界面耗时================", "" + (l5 - l4));
                        LocalSetting.DoingCount = infusioningModel.DoingCount;
                        LocalSetting.TodoCount = infusioningModel.TodoCount;
                    } else {
                        UIHepler.showToast(activity, "没有查询到该瓶贴");
                    }
                    Log.e("查询瓶贴返回成功", "");

                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    Log.e("查询瓶贴返回失败", "");
                    UIHepler.showToast(activity, "查找失败");
                }
            });
        }
    });

    return convertView;
  }

    /**
     * 显示状态
     */
    private void getStatusAndShow(BottleModel bottle, TextView textview) {
        String status = null;
        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "hospital");
        if(BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus){
            if(string.equals("zzey")){

            }else{
                status = "未排";
            }
        }else if(BottleStatusCategory.HADHANDLE.getKey() == bottle.BottleStatus){
            if(string.equals("zzey")){

            }else{
                status = "未配";
            }
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
        holder.patient_text_gms= (TextView) convertView.findViewById(R.id.patient_text_gms);
        holder.patients_sign = (ImageView) convertView.findViewById(R.id.patients_sign);
        holder.registerDate = (TextView) convertView.findViewById(R.id.registerDate);
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
      holder.patients_head.setBackgroundResource(R.drawable.patients_man_head);//会报内存溢出
//        holder.patients_head.setBackground(new BitmapDrawable(context.getResources(), bitmapBoy));
    } else {
      holder.patients_head.setBackgroundResource(R.drawable.patients_woman_head);
//        holder.patients_head.setBackground(new BitmapDrawable(context.getResources(), bitmapGirl));
    }
      String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "hospital");
      if(string.equals("zzey")){
          holder.patient_content_seatno.setVisibility(View.INVISIBLE);
          holder.patient_text_gms.setVisibility(View.INVISIBLE);
          holder.patient_content_gms.setVisibility(View.INVISIBLE);
      }else{
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
          holder.patient_text_gms.setVisibility(View.VISIBLE);
          holder.patient_content_gms.setVisibility(View.VISIBLE);
          if (StringUtils.StringIsEmpty(item.DrugAllergy)) {
              holder.patient_content_gms.setText("无");
              holder.patient_content_gms.setTextColor(context.getResources().getColor(R.color.darkgray));
          } else {
              holder.patient_content_gms.setText(item.DrugAllergy);
              holder.patient_content_gms.setTextColor(context.getResources().getColor(R.color.holo_red));
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
    } else if (RoleCategory.PEIYE.getKey() == LocalSetting.RoleIndex) {
      for (BottleModel item : items) {
        if (BottleStatusCategory.HADHANDLE.getKey() == item.BottleStatus
                || BottleStatusCategory.WAITINGHANDLE.getKey()== item.BottleStatus)
                return false;
      }
    } else if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
      for (BottleModel item : items) {
        if (QueueStatusCategory.FINISHED.getKey() == item.PeopleInfo.Status) {
          return true;
        }
      }
      return false;
    } else if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
      for (BottleModel item : items) {
        if (BottleStatusCategory.HADHANDLE.getKey() == item.BottleStatus
                || BottleStatusCategory.WAITINGHANDLE.getKey()== item.BottleStatus
                ||BottleStatusCategory.WAITINGINFUSE.getKey() == item.BottleStatus
                || BottleStatusCategory.INFUSIONG.getKey() == item.BottleStatus) {
          return false;
        }
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
    TextView patient_text_gms;
    ImageView patients_sign;
    TextView registerDate;
  }
//    public void cyleBitmap(){
//        if(bitmapBoy !=null && !bitmapBoy.isRecycled()){
//            bitmapBoy.recycle();
//            bitmapBoy = null;
//        }
//        if(bitmapGirl !=null && !bitmapGirl.isRecycled()){
//            bitmapGirl.recycle();
//            bitmapGirl = null;
//        }
//    }
}
