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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.model.DrugDetailModel;
import com.jasonchen.base.utils.StringUtils;

import java.util.ArrayList;
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

public class PaiYaoDrugBatchAdapter extends BaseAdapter {
    private static final String TAG = "DetaiAdapter";

    private List<DrugDetailModel> drugs;
    private LayoutInflater inflater;
    private String originalBottleId;
    private Context context;
    private ArrayList<String> bottleIds;

    public PaiYaoDrugBatchAdapter(Context context, List<DrugDetailModel> drugs) {
        this.context = context;
        this.drugs = drugs;
        inflater = LayoutInflater.from(context);
        this.originalBottleId = "";
    }

    public PaiYaoDrugBatchAdapter(Context context, List<DrugDetailModel> drugs, ArrayList<String> bottleIds) {
        this.context = context;
        this.drugs = drugs;
        inflater = LayoutInflater.from(context);
//        this.originalBottleId = bid;
        this.bottleIds = bottleIds;
    }

    @Override
    public int getCount() {
        if (drugs == null) return 0;
        return drugs.size();
    }

    @Override
    public Object getItem(int position) {
        if (drugs == null) return null;
        return drugs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_detail2, null);
            holder = new Holder();
            holder.txt1 = (TextView) convertView.findViewById(R.id.drug_name);
            holder.txt2 = (TextView) convertView.findViewById(R.id.drug_unit);
//            holder.tui_sign = (ImageView) convertView.findViewById(R.id.tui_sign);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (drugs != null) {
            DrugDetailModel drug = drugs.get(position);
            if (drug.ReturnFlag == 1) {
                holder.txt1.setText(drug.ItemName +"("+drug.Standard+")" + "【退】");
            }else {
                holder.txt1.setText(drug.ItemName +"("+drug.Standard+")");
            }
            holder.txt2.setText(drug.EveryAmount+ StringUtils.getString(drug.AmountUnit));
            if (bottleIds.size()>0&&bottleIds.get(bottleIds.size()-1).equals(drug.BottleId)){
                holder.txt1.setTextColor(context.getResources().getColor(R.color.holo_red));
                holder.txt2.setTextColor(context.getResources().getColor(R.color.holo_red));
        }else {
                holder.txt1.setTextColor(context.getResources().getColor(R.color.black));
                holder.txt2.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
        return convertView;
    }

    class Holder {
        TextView txt1;
        TextView txt2;
        ImageView tui_sign;
    }
}
