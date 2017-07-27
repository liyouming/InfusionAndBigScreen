package com.fugao.infusion.peiye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fugao.infusion.R;
import com.fugao.infusion.chuaici.DrugBatchAdapter;
import com.fugao.infusion.model.BottleModel;
import com.jasonchen.base.utils.ListViewUtils;

import java.util.ArrayList;

/** 新华配液适配
 * Created by li on 2016/12/21.
 */

public class XinhuaPeiyeAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<BottleModel> bottleModels;
    private LayoutInflater inflater;
    private PeiYeActivity activity;
    public XinhuaPeiyeAdapter(Context context,ArrayList<BottleModel> bottleModels){
        this.context=context;
        this.bottleModels=bottleModels;
        activity= (PeiYeActivity) context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return bottleModels.size();
    }

    @Override
    public Object getItem(int position) {
        return bottleModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void notify(ArrayList<BottleModel> bottleModels){
        this.bottleModels=bottleModels;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView = inflater.inflate(R.layout.xinhuapeiye_item, parent, false);
            viewHolder.itemList= (ListView) convertView.findViewById(R.id.itemList);
            viewHolder.item_excute= (Button) convertView.findViewById(R.id.item_excute);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final BottleModel bottleModel=bottleModels.get(position);
        viewHolder.itemList.setAdapter(new DrugBatchAdapter(context, bottleModel.DrugDetails));
        ListViewUtils.setListViewHeightBasedOnChildren(viewHolder.itemList);
        if(bottleModel.BottleStatus<=2){
//            viewHolder.item_excute.setBackgroundResource(R.drawable.fugao_blue_bg);
//            viewHolder.item_excute.setClickable(true);
            viewHolder.item_excute.setVisibility(View.VISIBLE);
            viewHolder.item_excute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.xinhuaPeiyeFragment.bottleIdExecuteBySd(bottleModel);
                }
            });
        }else if(bottleModel.BottleStatus>2){
            viewHolder.item_excute.setVisibility(View.GONE);
//            viewHolder.item_excute.setBackgroundResource(R.color.black);
//            viewHolder.item_excute.setClickable(false);
        }

        return convertView;
    }
    class ViewHolder{
        private ListView itemList;
        private Button item_excute;
    }
}
