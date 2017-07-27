package com.fugao.newbigscreen.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fugao.newbigscreen.R;
import com.fugao.newbigscreen.model.BottleModel;
import com.fugao.newbigscreen.model.QueueModel;

import java.util.ArrayList;
import java.util.List;


/** 登记人员适配
 * Created by li on 2016/6/14.
 */
public class GridviewAdapter extends BaseAdapter{
    private List<QueueModel> WaitList;
    private Activity activity;
    private LayoutInflater layoutInflater;
    public GridviewAdapter(Activity activity, List<QueueModel> WaitList){
        this.activity=activity;
        this.WaitList=WaitList;
        this.layoutInflater = LayoutInflater.from(activity);
    }
    @Override
    public int getCount() {
        return WaitList.size();
    }

    @Override
    public Object getItem(int i) {
        return WaitList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public void notifyadapter(List<QueueModel> WaitList){
        this.WaitList=WaitList;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        GridviewHolder gridViewHolder;
        if(view==null){
            gridViewHolder=new GridviewHolder();
            view=layoutInflater.inflate(R.layout.gridviewadapter_layout,null);
            gridViewHolder.gd_textview= (TextView) view.findViewById(R.id.gd_textview);
            view.setTag(gridViewHolder);
        }else{
            gridViewHolder= (GridviewHolder) view.getTag();
        }
        gridViewHolder.gd_textview.setText(WaitList.get(i).Name);
        return view;
    }
    class GridviewHolder{
        private TextView gd_textview;
    }
}
