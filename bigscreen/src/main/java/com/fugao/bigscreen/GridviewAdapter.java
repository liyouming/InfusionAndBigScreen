package com.fugao.bigscreen;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fugao.bigscreen.model.BottleModel;
import com.fugao.bigscreen.model.People;

import java.util.ArrayList;

/**
 * Created by li on 2016/6/14.
 */
public class GridviewAdapter extends BaseAdapter{
    private ArrayList<BottleModel> bottleModels;
    private Activity activity;
    private GridviewHolder gridViewHolder;
    private LayoutInflater layoutInflater;
    public GridviewAdapter(Activity activity,ArrayList<BottleModel> bottleModels){
        this.activity=activity;
        this.bottleModels=bottleModels;
    }
    @Override
    public int getCount() {
        return bottleModels.size();
    }

    @Override
    public Object getItem(int i) {
        return bottleModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public void notifyadapter(ArrayList<BottleModel> bottleModels){
        this.bottleModels=bottleModels;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            gridViewHolder=new GridviewHolder();
            view=layoutInflater.inflate(R.layout.gridviewadapter_layout,null,false);
            gridViewHolder.gd_textview= (TextView) view.findViewById(R.id.gd_textview);
            view.setTag(gridViewHolder);
        }else{
            gridViewHolder= (GridviewHolder) view.getTag();
        }
        BottleModel bottleModel=bottleModels.get(i);
        gridViewHolder.gd_textview.setText(bottleModel.PeopleInfo.Name);
        return view;
    }
    class GridviewHolder{
        private TextView gd_textview;
    }
}
