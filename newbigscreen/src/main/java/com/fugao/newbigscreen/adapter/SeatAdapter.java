package com.fugao.newbigscreen.adapter;

import android.app.Activity;
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
public class SeatAdapter extends BaseAdapter{
    private List<String> seats;
    private Activity activity;
    private LayoutInflater layoutInflater;
    public SeatAdapter(Activity activity,List<String> seats){
        this.activity=activity;
        this.seats=seats;
        this.layoutInflater = LayoutInflater.from(activity);
    }
    @Override
    public int getCount() {
        return seats.size();
    }

    @Override
    public Object getItem(int position) {
        return seats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SeatHolder seatHolder;
        if(convertView==null){
            seatHolder=new SeatHolder();
            convertView=layoutInflater.inflate(R.layout.seat_layout,null);
            seatHolder.seats= (TextView) convertView.findViewById(R.id.seats);
            convertView.setTag(seatHolder);
        }else{
            seatHolder= (SeatHolder) convertView.getTag();
        }
        seatHolder.seats.setText(seats.get(position));
        return convertView;
    }
    class SeatHolder{
        private TextView seats;
    }
}
