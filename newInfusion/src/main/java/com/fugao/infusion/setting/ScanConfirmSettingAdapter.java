/**
 * Copyright © 2014 上海复高计算机科技有限公司. All rights reserved.
 *
 * @Title: SettingDefaultDivisionAdapter.java
 * @Prject: FugaoEMR
 * @Package: com.fugao.emr.adapter
 * @Description: TODO
 * @author: 葛立平  geliping@fugao.com
 * @date: 2014年1月2日 下午5:04:19 
 * @version: V1.0
 */
package com.fugao.infusion.setting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugao.infusion.R;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: SettingDefaultDivisionAdapter
 * @Description: TODO
 * @author: 葛立平 geliping@fugao.com
 * @date: 2014年1月2日 下午5:04:19
 */
public class ScanConfirmSettingAdapter extends BaseAdapter {
    private Activity mactivity;
    private List<String> scanConfrimSettingList;
    private LayoutInflater layoutInflater;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    /**
     * 选中下标
     */
    private int index = -1;

    private String settingContent;//存放选中的设置

    /**
     * @Title:SettingDefaultDivisionAdapter
     * @Description:TODO
     */
    public ScanConfirmSettingAdapter(Activity activity,
                                     List<String> scanConfrimSettingList) {
        this.mactivity = activity;
        this.scanConfrimSettingList = scanConfrimSettingList;
        isSelected = new HashMap<Integer, Boolean>();
        this.layoutInflater = LayoutInflater.from(mactivity);
        // 初始化数据
        initDate();

    }
//    初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < scanConfrimSettingList.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return scanConfrimSettingList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return scanConfrimSettingList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
        public void selectItem(int index) {
            this.index = index;
            notifyDataSetChanged();
        }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHodler hodler;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = layoutInflater.inflate(
                    R.layout.setting_default_infusion_area_listview_item, null);
            hodler.setting_default_division_item_name = (TextView) convertView
                    .findViewById(R.id.setting_default_division_item_name);
            hodler.item_cb = (CheckBox) convertView
                    .findViewById(R.id.item_cb);
            hodler.setting_default_division_item_layout = (LinearLayout) convertView
                    .findViewById(R.id.setting_default_division_item_layout);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        String content = scanConfrimSettingList.get(position);
        hodler.setting_default_division_item_name.setText(content);
        // 根据isSelected来设置checkbox的选中状况
//        if(getIsSelected().size()!=0)hodler.item_cb.setChecked(getIsSelected().get(position));
        if(index == position){
            hodler.item_cb.setChecked(true);
        }else {
            hodler.item_cb.setChecked(false);
        }
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ScanConfirmSettingAdapter.isSelected = isSelected;
    }

    class ViewHodler {
        TextView setting_default_division_item_name;
        public CheckBox item_cb;
        LinearLayout setting_default_division_item_layout;
    }
}
