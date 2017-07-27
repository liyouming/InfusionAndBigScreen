package com.fugao.infusion.chuaici;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.jasonchen.base.utils.StringUtils;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.ui.adapter.AreaEmptySeatAdapter
 * @Description: TODO
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/11/12 14:57
 * @version: V1.0
 */

public class AreaEmptySeatAdapter extends BaseAdapter {
    private static final String TAG = "Fugao-AreaEmptySeatAdapter";

    private List<String> emptySeats;

    private LayoutInflater inflater;

    public AreaEmptySeatAdapter(Activity fatherActivity, List<String> emptySeats) {
        this.emptySeats = emptySeats;
        this.inflater = LayoutInflater.from(fatherActivity);
    }

    @Override
    public int getCount() {
        return emptySeats.size();
    }

    @Override
    public Object getItem(int position) {
        return emptySeats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_item_area_seat, null);
            holder.emptySeat = (TextView) convertView.findViewById(R.id.showEmptySeat);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        holder.emptySeat.setText(StringUtils.getString(emptySeats.get(position)));
        return convertView;
    }

    class Holder{
        TextView emptySeat;
    }
}
