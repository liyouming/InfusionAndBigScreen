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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.utils.InfoDateUtils;
import com.jasonchen.base.utils.ResourceUtils;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.infusion.adapter.ArroundAdapter
 * @Description: TODO
 * @author: 陈亮    chenliang@fugao.com
 * @date: 2014/8/25 18:30
 * @version: V1.0
 *
 * 此界面对应巡视界面的巡视
 */

public class ArroundsAdapter extends BaseAdapter {
  private Context context;
  private List<BottleModel> bottleModels;
  private String moreThanOnDay;

  public ArroundsAdapter(Context context, List<BottleModel> bottleModels) {
    this.context = context;
    this.bottleModels = bottleModels;
    moreThanOnDay = ResourceUtils.getResouceString(context, R.string.more_than_one_day);
  }

  @Override
  public int getCount() {
    return bottleModels.size();
  }

  @Override
  public Object getItem(int position) {
    return bottleModels.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.list_item_arrounds, null, false);
      holder = new Holder();
      holder.index_transfusion_bedno_name =
          (TextView) convertView.findViewById(R.id.index_transfusion_bedno_name);
      holder.index_transufsion_last_arround_time =
          (TextView) convertView.findViewById(R.id.index_transufsion_last_arround_time);
      holder.arround_time = (TextView) convertView.findViewById(R.id.arround_time);
      convertView.setTag(holder);
    } else {
      holder = (Holder) convertView.getTag();
    }
    BottleModel bottle = bottleModels.get(position);
    List<PatrolModel> patrolDetailBeans = bottle.AboutPatrols;
    if (patrolDetailBeans != null && patrolDetailBeans.size() > 0) {
      String time = patrolDetailBeans.get(0).PatrolTime;
       if(time != null){
           holder.index_transufsion_last_arround_time.setText(time);
           String passTime = InfoDateUtils.getBetweenTimes(time, moreThanOnDay);
           holder.arround_time.setText(passTime);
       }
    }else{
        holder.index_transufsion_last_arround_time.setText("最近一次巡视的时间");
        holder.arround_time.setText("已执行时间");
    }

    holder.index_transfusion_bedno_name.setText(bottle.PeopleInfo.Name);
    return convertView;
  }

  class Holder {
    TextView index_transfusion_bedno_name;
    TextView index_transufsion_last_arround_time;
    TextView arround_time;
  }
}
