package com.fugao.newbigscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fugao.newbigscreen.R;

import java.util.List;

/** 座位适配
 * Created by li on 2016/6/22.
 */
public class SeatGridviewAdapter extends BaseAdapter{
    private List<String> seatStrings;
    private Context context;
    private LayoutInflater layoutInflater;
    public SeatGridviewAdapter(Context context,List<String> seatStrings){
        this.context=context;
        this.seatStrings=seatStrings;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return seatStrings.size();
    }

    @Override
    public Object getItem(int position) {
        return seatStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SeatGridviewHolder seatGridviewHolder;
        if(convertView==null){
            seatGridviewHolder=new SeatGridviewHolder();
            convertView=layoutInflater.inflate(R.layout.seatadapter_layout,null);
            seatGridviewHolder.callseat_need= (TextView) convertView.findViewById(R.id.callseat_need);
            convertView.setTag(seatGridviewHolder);
        }else{
            seatGridviewHolder= (SeatGridviewHolder) convertView.getTag();
        }
        String seats=seatStrings.get(position);
        seatGridviewHolder.callseat_need.setText(seats);
        return convertView;
    }
    class SeatGridviewHolder{
        private TextView callseat_need;
    }
}
