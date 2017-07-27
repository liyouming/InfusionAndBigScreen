package com.fugao.infusion.peiye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.model.DrugDetailModel;
import com.jasonchen.base.utils.StringUtils;

import java.util.List;


/** 药品适配
 * Created by li on 2016/9/28.
 */
public class NewPeiyeAdapter extends BaseAdapter{
    private List<DrugDetailModel> drugDetailModels;
    private Context context;
    public NewPeiyeAdapter( Context context,List<DrugDetailModel> drugDetailModels){
        this.context=context;
        this.drugDetailModels=drugDetailModels;
    }
    @Override
    public int getCount() {
        return drugDetailModels.size();
    }

    @Override
    public Object getItem(int position) {
        return drugDetailModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void change(List<DrugDetailModel> drugDetailModels){
        this.drugDetailModels=drugDetailModels;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.adapter_drugdetail_layout,null);
            viewHolder.linearlayout_drugdetail= (LinearLayout) convertView.findViewById(R.id.linearlayout_drugdetail);
            viewHolder.drug_name= (TextView) convertView.findViewById(R.id.drug_name);
            viewHolder.drug_unit= (TextView) convertView.findViewById(R.id.drug_unit);
            viewHolder.drug_quantity= (TextView) convertView.findViewById(R.id.drug_quantity);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        DrugDetailModel drugDetailModel=drugDetailModels.get(position);
        viewHolder.drug_name.setText(StringUtils.getString(drugDetailModel.ItemName+"("+drugDetailModel.Standard+")"));
        String string=StringUtils.getString(drugDetailModel.EveryAmount+"");
        string=string.replaceAll("0+?$", "");
        string = string.replaceAll("[.]$", "");
        string =string+StringUtils.getString(drugDetailModel.AmountUnit);
        viewHolder.drug_unit.setText(string);
        String a=StringUtils.getString(drugDetailModel.Amount+"");
        a=a.replaceAll("0+?$", "");
        a = a.replaceAll("[.]$", "");
        viewHolder.drug_quantity.setText(a);
        return convertView;
    }
    class ViewHolder{
        private LinearLayout linearlayout_drugdetail;
        private TextView drug_name;
        private TextView drug_unit;
        private TextView drug_quantity;
    }
}
