package com.fugao.newbigscreen.activitys;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fugao.newbigscreen.R;
import com.fugao.newbigscreen.adapter.GridViewSeatAdapter;
import com.fugao.newbigscreen.constant.InfoApi;
import com.fugao.newbigscreen.model.InfusionAreaBean;
import com.fugao.newbigscreen.model.RealseSeatModel;
import com.fugao.newbigscreen.utils.String2InfusionModel;
import com.fugao.newbigscreen.utils.XmlDB;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 座位界面
 * Created by li on 2016/6/23.
 */
public class SeatActivity extends BaseTempleActivity implements SwipeRefreshLayout.OnRefreshListener{
    private GridView gridView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner myspinner;
    private ArrayList<HashMap<String, String>> lstImageItem;
    private String deptId="100001";
    private ArrayList<InfusionAreaBean> infusionAreaBeans;
    /**
     * 区域集合
     */
    private ArrayList<String> infusionAreaNameList = null;
    private List<RealseSeatModel> realseSeatNoList;
    private GridViewSeatAdapter gridviewAdapter;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_seat_layout);
    }

    @Override
    public void initView() {
        gridView= (GridView) findViewById(R.id.gridView);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.un_complete_refresh_layout);
        myspinner= (Spinner) findViewById(R.id.realeseseat_spinner);
        swipeRefreshLayout.setOnRefreshListener(SeatActivity.this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
    }

    @Override
    public void initData() {
        infusionAreaBeans=new ArrayList<InfusionAreaBean>();
        realseSeatNoList=new ArrayList<>();
        infusionAreaNameList=new ArrayList<>();
        RestClient.BASE_URL="http://192.168.252.1:8011/";
//        getAreaList();
        getData(1);
    }

    @Override
    public void initListener() {

    }

    private void getAreaList(){

        RestClient.get(InfoApi.getInfusionAreaByDeptID(deptId), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                ArrayList<InfusionAreaBean> infusionAreaBeans1 = String2InfusionModel.string2InfusionAreaBeans(s);
                if(infusionAreaBeans1!=null&&infusionAreaBeans1.size()>0){
                    for(InfusionAreaBean each:infusionAreaBeans1){
                        if(each.Type==2)continue;
                        infusionAreaNameList.add(each.AreaName);
                        infusionAreaBeans.add(each);
                    }
                    showAreaName();
                }else{
                    UIHepler.showToast(SeatActivity.this, "没有加载的区域");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {

            }
        });
    }
    /**
     * 加载区域名称
     */
    private void showAreaName() {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(SeatActivity.this, android.R.layout.simple_spinner_dropdown_item, infusionAreaNameList);
        //下拉列表
        myspinner.setAdapter(stringArrayAdapter);
        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                textView.setTextColor(Color.WHITE);
                XmlDB.getInstance(SeatActivity.this).saveKey("areaId", infusionAreaBeans.get(position).Id);
                getData(infusionAreaBeans.get(position).Id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void getData(int id){
        swipeRefreshLayout.setRefreshing(true);
        String url = InfoApi.url_GetSeatAndInfo( deptId, String.valueOf(id));
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                swipeRefreshLayout.setRefreshing(false);
                List<RealseSeatModel> realseSeatNoListss = String2InfusionModel.string2RealseSeatModel(s);
                lstImageItem = new ArrayList<HashMap<String, String>>();
                realseSeatNoList.clear();
                realseSeatNoList.addAll(realseSeatNoListss);
                if(realseSeatNoListss.size()>0){
                    for(int j=0;j<realseSeatNoListss.size();j++){
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("itemSeatNo", realseSeatNoListss.get(j).Seat);
                        if (realseSeatNoListss.get(j).PeopleInfo != null) {
                            map.put("itemName", realseSeatNoListss.get(j).PeopleInfo.Name);
                        }
                        lstImageItem.add(map);
                    }
                    gridviewAdapter = new GridViewSeatAdapter(SeatActivity.this, lstImageItem);
                    gridView.setAdapter(gridviewAdapter);
                    gridviewAdapter.notifyDataSetChanged();
//                    setRefreshing(false);
                }else{
                    UIHepler.showToast(SeatActivity.this, "暂时没有座位可以释放，请稍等！");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                swipeRefreshLayout.setRefreshing(false);
                UIHepler.showToast(SeatActivity.this,"网络请求失败，请刷新！");
            }
        });
    }
    public void topRefresh(){
//        int areaId;
//        if(infusionAreaBeans !=null) {
//            areaId = XmlDB.getInstance(SeatActivity.this).getKeyIntValue("areaId", infusionAreaBeans.get(0).Id);
//        }else {
//            getAreaList();
//            areaId = XmlDB.getInstance(SeatActivity.this).getKeyIntValue("areaId", infusionAreaBeans.get(0).Id);
//        }
        getData(1);
    }
    @Override
    public void initIntent() {

    }

    @Override
    public void onRefresh() {
        topRefresh();
    }
}
