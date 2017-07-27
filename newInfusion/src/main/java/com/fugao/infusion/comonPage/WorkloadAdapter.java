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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.model.CounterModel;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.infusion.activity.DetaiAdapter
 * @Description: TODO
 * @author: LoQua xiqiang@fugao.com
 * @date: 2014/6/10 14:04
 * @version: V1.0
 */

public class WorkloadAdapter extends BaseAdapter {
  private static final String TAG = "DetaiAdapter";

  private List<CounterModel> counters;

  private LayoutInflater inflater;

  public WorkloadAdapter(Context context, List<CounterModel> counters) {
    this.counters = counters;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return counters.size();
  }

  @Override
  public Object getItem(int position) {
    return counters.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.list_item_workload, null);
      holder = new Holder();
      holder.workName = (TextView) convertView.findViewById(R.id.work_name);
      holder.workCount = (TextView) convertView.findViewById(R.id.work_count);
      convertView.setTag(holder);
    } else {
      holder = (Holder) convertView.getTag();
    }

    CounterModel counter = counters.get(position);
    holder.workName.setText(counter.name);
    holder.workCount.setText(counter.count + "");
    return convertView;
  }

  private static class Holder {
    TextView workName;
    TextView workCount;
  }
}
