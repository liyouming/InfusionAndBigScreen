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
import com.fugao.infusion.model.PatrolModel;
import com.jasonchen.base.utils.StringUtils;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.infusion.adapter.AroundDetailAdapter
 * @Description: TODO
 * @author: 席强    xiqiang@fugao.com
 * @date: 2014/9/4 14:46
 * @version: V1.0
 * <p/>
 * PS:上面作者一栏需要修改成自己的信息
 * File-Setting-Sile and Code Templetes-Includes
 */

public class AroundDetailAdapter extends BaseAdapter {
    private static final String TAG = "Fugao-AroundDetailAdapter";
    private List<PatrolModel> patrolDetailBeans;
    private Context context;

    public AroundDetailAdapter(Context context, List<PatrolModel> patrolDetailBeans) {
        this.patrolDetailBeans = patrolDetailBeans;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (patrolDetailBeans == null) return 0;
        return patrolDetailBeans.size();
    }

    @Override
    public Object getItem(int position) {
        if (patrolDetailBeans == null) return null;
        return patrolDetailBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        if (convertView == null) {
            viewHold = new ViewHold();
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.list_item_arround_detail, null, false);
            viewHold.transfusion_detail_content =
                    (TextView) convertView.findViewById(R.id.transfusion_detail_content);
            viewHold.transfusion_detail_operator =
                    (TextView) convertView.findViewById(R.id.transfusion_detail_operator);
            viewHold.transfusion_detail_time =
                    (TextView) convertView.findViewById(R.id.transfusion_detail_time);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        if (patrolDetailBeans != null) {
            PatrolModel patrolDetailBean = patrolDetailBeans.get(position);
            viewHold.transfusion_detail_time.setText(patrolDetailBean.PatrolTime);
            viewHold.transfusion_detail_operator.setText(patrolDetailBean.PatrolerName);
            viewHold.transfusion_detail_content.setText(StringUtils.getString(patrolDetailBean.TargetContent) + "  "+StringUtils.getString(patrolDetailBean.Content));
        }
        return convertView;
    }

    class ViewHold {
        TextView transfusion_detail_time;
        TextView transfusion_detail_operator;
        TextView transfusion_detail_content;
    }
}
