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

package com.fugao.infusion.chuaici;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugDetailModel;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.infusion.adapter.DurgDetailsAdapter
 * @Description: TODO  主要是加载对应每个瓶贴所对应有几组药
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/8/28 10:25
 * @version: V1.0
 */

public class BottleAdapter extends BaseAdapter {

    private static final String TAG = "Fugao-DurgDetailsAdapter";

    private Context context;
    private List<BottleModel> bottleModels;
    private LayoutInflater inflater;
    private String originalBottleId;
    /**
     * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
     */
    private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

    public BottleAdapter(Context context, List<BottleModel> bottleModels, String bid ,Map<Integer, Boolean> isCheckMap) {
        this.context = context;
        this.bottleModels = bottleModels;
        inflater = LayoutInflater.from(context);
        this.originalBottleId = bid;
        this.isCheckMap=isCheckMap;
    }

    public Map<Integer,Boolean> getCheckMap() {
        return this.isCheckMap;
    }
    @Override
    public int getCount() {
        return bottleModels.size();
    }

    @Override
    public BottleModel getItem(int position) {
        return bottleModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DurgHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_patients_item, parent, false);
            holder = new DurgHolder();
            holder.listView = (ListView) convertView.findViewById(R.id.sonList);
            holder.drugsSign = (ImageView) convertView.findViewById(R.id.drugs_sign);
            holder.groupNum = (TextView) convertView.findViewById(R.id.groupNum);
            holder.cbBottle = (CheckBox) convertView.findViewById(R.id.cbBottle);
            holder.tvDate = (TextView) convertView.findViewById(R.id.Date);
            convertView.setTag(holder);
        } else {
            holder = (DurgHolder) convertView.getTag();
        }
        BottleModel bottle = bottleModels.get(position);
        if (RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex) {
            holder.drugsSign.setVisibility(
                    BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus ? View.GONE
                            : View.VISIBLE
            );
        } else if (RoleCategory.PEIYE.getKey() == LocalSetting.RoleIndex) {
            holder.drugsSign.setVisibility(
                    BottleStatusCategory.WAITINGINFUSE.getKey() == bottle.BottleStatus ? View.VISIBLE
                            : View.GONE
            );
        }
        String initDate=bottle.RegistrationDate;
        String date= DateUtils.getMMddHHmm(initDate);
        holder.groupNum.setText(bottle.PeopleInfo.QueueNo + "\r\r\r\r" + bottle.GroupId );
        holder.tvDate.setText(date+"\r");
        if(bottle.IsReturn){
            ViewUtils.setVisible(holder.cbBottle);
        }else{
            ViewUtils.setGone(holder.cbBottle);
            isCheckMap.put(position, false);
        }
        if(bottle.BottleStatus !=1){
            ViewUtils.setGone(holder.cbBottle);
        }
        holder.cbBottle.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                /*
                 * 将选择项加载到map里面寄存
				 */
                isCheckMap.put(position, isChecked);
            }
        });
        if (isCheckMap.get(position) == null) {
            isCheckMap.put(position, false);
        }
        List<DrugDetailModel> drugDetails = bottle.DrugDetails;
        holder.cbBottle.setChecked(isCheckMap.get(position));
        holder.listView.setAdapter(new DrugBatchAdapter(context, drugDetails, originalBottleId));
        ListViewUtils.setListViewHeightBasedOnChildren(holder.listView);
        return convertView;
    }

    class DurgHolder {
        ListView listView;
        ImageView drugsSign;
        TextView groupNum;
        CheckBox cbBottle;
        TextView tvDate;
    }
}
