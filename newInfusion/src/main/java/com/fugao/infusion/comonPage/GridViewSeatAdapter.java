package com.fugao.infusion.comonPage;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.fugao.infusion.R;
import com.jasonchen.base.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.ui.adapter.GridViewSeatAdapter
 * @Description: TODO
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/11/19 18:02
 * @version: V1.0
 */

public class GridViewSeatAdapter extends BaseAdapter {
    private static final String TAG = "Fugao-GridViewSeatAdapter";

    private ArrayList<HashMap<String, String>> lstImageItem;
    private LayoutInflater inflater;

    public GridViewSeatAdapter(Activity fatherActivity, ArrayList<HashMap<String, String>> lstImageItem) {
        this.lstImageItem = lstImageItem;
        inflater = LayoutInflater.from(fatherActivity);
    }

    @Override
    public int getCount() {
        return lstImageItem.size();
    }

    @Override
    public Object getItem(int position) {
        return lstImageItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_realse_item, null);
            holder = new Holder();
            holder.textSeatNo = (TextView) convertView.findViewById(R.id.setNo);
            holder.textName = (TextView) convertView.findViewById(R.id.patientDeatail);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.textName.setBackgroundResource((StringUtils.StringIsEmpty(lstImageItem.get(position).get("itemName"))
                 ? R.drawable.no_seat :R.drawable.has_seat));
        holder.textSeatNo.setText(StringUtils.getString(lstImageItem.get(position).get("itemSeatNo")));

//        if(lstImageItem.get(position).get("itemName")==null){
//            holder.textName.setVisibility(View.INVISIBLE);
//            holder.zuowei.setVisibility(View.GONE);
//        }else {
//
//            holder.textName.setVisibility(View.VISIBLE);
//
//        }

        String itemName = StringUtils.getString(lstImageItem.get(position).get("itemName"));
        holder.textName.setText(itemName);





        return convertView;
    }
    private static class Holder {
        TextView textSeatNo;
        TextView zuowei;
        TextView textName;

    }
}
