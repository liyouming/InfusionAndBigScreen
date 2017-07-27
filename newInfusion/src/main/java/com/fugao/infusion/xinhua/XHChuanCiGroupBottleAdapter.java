package com.fugao.infusion.xinhua;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.chuaici.ChuanCiActivity;
import com.fugao.infusion.chuaici.ChuanCiGroupBottleAdapter;
import com.fugao.infusion.chuaici.DrugBatchAdapter;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugsModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.StringUtils;

import java.util.List;

/** 新华新的穿刺适配
 * Created by li on 2017/3/21.
 */

public class XHChuanCiGroupBottleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter{
    private static final String TAG = "Fast-BottlesExpandAdapter";

    private Context context;
    private List<GroupBottleModel> items;
    private LayoutInflater inflater;
//    private List<BottleModel> newBottles ;//取消完成瓶贴集合
    private ChuanCiActivity activity;
    public XHChuanCiGroupBottleAdapter(Context context, List<GroupBottleModel> items) {
        this.context = context;
        activity = (ChuanCiActivity)context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }
    public void changeView(List<GroupBottleModel> items){
        this.items = items;
        notifyDataSetChanged();
    }
    @Override public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild,
                                           View convertView, ViewGroup parent) {

        XHChuanCiGroupBottleAdapter.ChildHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_patients_item_son, parent, false);
            holder = new XHChuanCiGroupBottleAdapter.ChildHolder();
            holder.txtSeatNo = (TextView) convertView.findViewById(R.id.txtSeatNo);
            holder.listView = (ListView) convertView.findViewById(R.id.sonList);
            holder.btnMore = (Button) convertView.findViewById(R.id.btnMore);
            convertView.setTag(holder);
        } else {
            holder = (XHChuanCiGroupBottleAdapter.ChildHolder) convertView.getTag();
        }
        final GroupBottleModel group = items.get(groupPosition);

        final BottleModel bottle = group.items.get(childPosition);

        holder.listView.setAdapter(new DrugBatchAdapter(context, bottle.DrugDetails));
        ListViewUtils.setListViewHeightBasedOnChildren(holder.listView);
        holder.txtSeatNo.setText(
                StringUtils.StringIsEmpty(bottle.SeatNo) ? "无" : bottle.PeopleInfo.SeatNo);

        getStatusAndShow(bottle,holder.txtSeatNo);
        holder.btnMore.setVisibility(View.VISIBLE);//穿刺界面按钮放开

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //暂时注释
                activity.xinHuaChuanCiPeopleFragment.redirect2ExecuteBottleId(bottle.BottleId);
            }
        });

        return convertView;
    }

    /**
     * 显示状态
     */
    private void getStatusAndShow(BottleModel bottle, TextView textview) {
        String status = null;
        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "hospital");
        if(BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus){
            textview.setVisibility(View.VISIBLE);
            textview.setTextColor(Color.BLACK);
            if(string.equals("zzey")){

            }else{
                status = "未排";
            }
        }else if(BottleStatusCategory.HADHANDLE.getKey() == bottle.BottleStatus){
            textview.setVisibility(View.VISIBLE);
            textview.setTextColor(Color.BLACK);
            if(string.equals("zzey")){

            }else{
                status = "未配";
            }
//            textview.setBackgroundResource(R.drawable.fugao_blue_bg);
        }else if(BottleStatusCategory.WAITINGINFUSE.getKey() == bottle.BottleStatus){
            textview.setVisibility(View.GONE);
            status = "执行";
//            textview.setBackgroundResource(R.drawable.fugao_blue_bg);
        }else if(BottleStatusCategory.INFUSIONG.getKey() == bottle.BottleStatus){
            textview.setVisibility(View.VISIBLE);
            textview.setTextColor(Color.RED);
            status="输液中";
        }else if(BottleStatusCategory.HADINFUSE.getKey() == bottle.BottleStatus){
            textview.setVisibility(View.VISIBLE);
            textview.setTextColor(Color.RED);
            status="已完成";
        }
        textview.setText(status);
    }

    @Override public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override public int getGroupCount() {
        return items.size();
    }

    @Override public GroupBottleModel getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override public BottleModel getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("NewApi")
    @Override public View getGroupView(int groupPosition, boolean b, View convertView,
                                       ViewGroup parent) {
        XHChuanCiGroupBottleAdapter.FatherHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_patients_item_father, parent, false);
            holder = new XHChuanCiGroupBottleAdapter.FatherHolder();
            holder.patients_head = (TextView) convertView.findViewById(R.id.patients_head);
            holder.patient_content_seatno = (TextView) convertView.findViewById(R.id.patient_content_seatno);
            holder.patient_content_old = (TextView) convertView.findViewById(R.id.patient_content_old);
            holder.patient_content_mzh = (TextView) convertView.findViewById(R.id.patient_content_mzh);
            holder.patient_content_lsh = (TextView) convertView.findViewById(R.id.patient_content_lsh);
            holder.patient_content_tz = (TextView) convertView.findViewById(R.id.patient_content_tz);
            holder.patient_content_gms = (TextView) convertView.findViewById(R.id.patient_content_gms);
            holder.patient_text_gms= (TextView) convertView.findViewById(R.id.patient_text_gms);
            holder.patients_sign = (ImageView) convertView.findViewById(R.id.patients_sign);
            holder.registerDate = (TextView) convertView.findViewById(R.id.registerDate);
            convertView.setTag(holder);
        } else {
            holder = (XHChuanCiGroupBottleAdapter.FatherHolder) convertView.getTag();
        }
        GroupBottleModel item = getGroup(groupPosition);
        /**
         * 姓名
         */
        holder.patients_head.setText(item.Name);
        if (1 == item.Sex) {
            holder.patients_head.setBackgroundResource(R.drawable.patients_man_head);//会导致OOM
//        holder.patients_head.setBackground(new BitmapDrawable(context.getResources(), bitmapBoy));
        } else {
            holder.patients_head.setBackgroundResource(R.drawable.patients_woman_head);
//        holder.patients_head.setBackground(new BitmapDrawable(context.getResources(), bitmapGirl));
        }

        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "hospital");
        if(string.equals("zzey")){
            holder.patient_content_seatno.setVisibility(View.INVISIBLE);
            holder.patient_text_gms.setVisibility(View.INVISIBLE);
            holder.patient_content_gms.setVisibility(View.INVISIBLE);
        }else{
            if (StringUtils.StringIsEmpty(item.SeatNo)) {
                holder.patient_content_seatno.setVisibility(View.INVISIBLE);
            } else {
                holder.patient_content_seatno.setVisibility(View.VISIBLE);
                if(!StringUtils.getString(item.SeatNo).contains("none")){
                    holder.patient_content_seatno.setText(item.SeatNo);
                }else {
                    holder.patient_content_seatno.setText("");
                }
            }
            holder.patient_text_gms.setVisibility(View.VISIBLE);
            holder.patient_content_gms.setVisibility(View.VISIBLE);
            if (StringUtils.StringIsEmpty(item.DrugAllergy)) {
                holder.patient_content_gms.setText("无");
                holder.patient_content_gms.setTextColor(context.getResources().getColor(R.color.darkgray));
            } else {
                holder.patient_content_gms.setText(item.DrugAllergy);
                holder.patient_content_gms.setTextColor(context.getResources().getColor(R.color.holo_red));
            }
        }
        String registrationDate = item.items.get(0).RegistrationDate;//怕会存在合并不同日期的情况
        String registrationTime = item.items.get(0).RegistrationTime;
        String date =registrationDate.substring(4,6)+"-"+registrationDate.substring(6,8)
                +"  "+registrationTime.substring(0,2)+":"+registrationTime.substring(2,4);
        holder.registerDate.setText(date);
        holder.patient_content_old.setText(item.Age);
        holder.patient_content_mzh.setText(item.PatId);
        holder.patient_content_lsh.setText(item.Lsh);
        holder.patient_content_tz.setText(item.Weight);


        if (isFinished(item.items)) {
            holder.patients_sign.setVisibility(View.VISIBLE);
        } else {
            holder.patients_sign.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 是否完成
     */
    private boolean isFinished(List<BottleModel> items) {
        if (RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex) {
            for (BottleModel item : items) {
                if (BottleStatusCategory.WAITINGHANDLE.getKey() == item.BottleStatus) return false;
            }
        } else if (RoleCategory.PEIYE.getKey() == LocalSetting.RoleIndex) {
            for (BottleModel item : items) {
                if (BottleStatusCategory.HADHANDLE.getKey() == item.BottleStatus
                        || BottleStatusCategory.WAITINGHANDLE.getKey()== item.BottleStatus)
                    return false;
            }
        } else if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            for (BottleModel item : items) {
                if (QueueStatusCategory.FINISHED.getKey() <= item.PeopleInfo.Status
                        ||BottleStatusCategory.INFUSIONG.getKey()<=item.BottleStatus){
                    return true;
                }
            }
            return false;
        } else if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
            for (BottleModel item : items) {
                if (BottleStatusCategory.HADHANDLE.getKey() == item.BottleStatus
                        || BottleStatusCategory.WAITINGHANDLE.getKey()== item.BottleStatus
                        ||BottleStatusCategory.WAITINGINFUSE.getKey() == item.BottleStatus
                        || BottleStatusCategory.INFUSIONG.getKey() == item.BottleStatus) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateDrugs(String pid, List<DrugsModel> drugs) {
        for (GroupBottleModel item : items) {
            if (pid.equals(item.PatId)) {
                for (int index = 0; index < item.items.size(); index++) {
                    for (DrugsModel each : drugs) {
                        if (item.items.get(index).BottleId.equals(each.BottleId)) {
                            item.items.get(index).DrugDetails = each.Drugs;
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class ChildHolder {
        TextView txtSeatNo;
        ListView listView;
        Button btnMore;

    }

    class FatherHolder {
        TextView patients_head;
        TextView patient_content_seatno;
        TextView patient_content_old;
        TextView patient_content_mzh;
        TextView patient_content_lsh;
        TextView patient_content_tz;
        TextView patient_content_gms;
        TextView patient_text_gms;
        ImageView patients_sign;
        TextView registerDate;
    }
}
