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
import com.fugao.infusion.model.CallModel;
import com.jasonchen.base.utils.ViewUtils;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.infusion.adapter.CallQueueAdapter
 * @Description:
 * @author:
 * @date:
 * @version:
 */

public class CallQueueAdapter extends BaseAdapter {
  private static final String TAG = "CallQueueAdapter";

  private Context context = null;

  private List<CallModel> infusionCall = null;

  private LayoutInflater Inflater;

  public CallQueueAdapter(Context context, List<CallModel> infusionCall) {
    super();
    this.context = context;
    this.infusionCall = infusionCall;
    Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public void updateAdapter(List<CallModel> infusionCall) {
    this.infusionCall = infusionCall;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return infusionCall.size();
  }

  @Override
  public Object getItem(int position) {
    return infusionCall.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final Holder holder;
    if (convertView == null) {
      convertView = Inflater.inflate(R.layout.list_item_call, null);
      holder = new Holder();
     // holder.tvName=(TextView)convertView.findViewById(R.id.tvName);
      holder.callContentall = (TextView) convertView.findViewById(R.id.callContentall);
      holder.pastCallTime = (TextView) convertView.findViewById(R.id.pastCallTime);
      holder.callStatus = (TextView) convertView.findViewById(R.id.callStatus);
      holder.leftTime = (TextView) convertView.findViewById(R.id.leftTime);
      convertView.setTag(holder);
    } else {
      holder = (Holder) convertView.getTag();
    }

    CallModel infoCallBean = infusionCall.get(position);
    holder.callContentall.setText(infoCallBean.CallContent + "呼叫");
    holder.pastCallTime.setText("已过去" + infoCallBean.TimeMinute +
        "分" + infoCallBean.TimeSecond + "秒");

    if (infoCallBean.InfusionStatus == 4) {

      ViewUtils.setVisible(holder.leftTime);

      /**
       * 正在输液,并计算剩余事件
       */
      holder.callStatus.setText("正在输液中");
      double leftTime = 0;
      try {
        //leftTime = infoCallBean.TransfusionBulk / infoCallBean.TransfusionSpeed * 0.1;
      } catch (Exception e) {

      }

      holder.leftTime.setText("剩余时间:" + leftTime);
    } else if (infoCallBean.InfusionStatus == 0
        || infoCallBean.InfusionStatus == 1
        || infoCallBean.InfusionStatus == 2
        || infoCallBean.InfusionStatus == 3) {
      holder.callStatus.setText("还未开始输液");
      ViewUtils.setGone(holder.leftTime);
    } else if (infoCallBean.InfusionStatus == 5) {
      holder.callStatus.setText("已完成输液");
      ViewUtils.setGone(holder.leftTime);
    }

    return convertView;
  }

  class Holder {
    TextView callContentall;
    TextView pastCallTime;
    TextView callStatus;
    TextView leftTime;
    TextView tvName;
  }
}
