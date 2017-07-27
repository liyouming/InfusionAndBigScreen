package com.fugao.infusion.chuaici;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.ui.fragment.InfusionAreaSeatDetailFragment
 * @Description: TODO  每个区域空座位详情
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/11/12 14:41
 * @version: V1.0
 */

public class InfusionAreaSeatDetailFragment extends BaseFragmentV4 implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, ChuancCiExecuteSingleActivity.RefreshFragment{
    private static final String EXTRA_CATEGORY = "AreaSeat_category";
    private SwipeRefreshLayout mUnCompleteRefreshLayout;
    private ListView mListView;

    private MenuItem refreshItem;
    private String catalog;
    private ActionBar actionbar;
    private List<String> emptySeats = new ArrayList<String>();
    private AreaEmptySeatAdapter areaEmptySeatAdapter;

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private String currentSeatNO = null;

    public static InfusionAreaSeatDetailFragment newInstance(String catalog) {
        InfusionAreaSeatDetailFragment fragment = new InfusionAreaSeatDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, catalog);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workload, container, false);
    }

    @Override
    public void initView(View view) {
        mUnCompleteRefreshLayout = (SwipeRefreshLayout) view;
        mListView = (ListView) mUnCompleteRefreshLayout.findViewById(R.id.listView);
        mListView.setEmptyView(mUnCompleteRefreshLayout.findViewById(R.id.nodata));
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        mListView.setOnItemClickListener(this);

    }

    @Override
    public void initData() {
        /**
         * 填充各控件的数据
         */
        catalog = getArguments().getString(EXTRA_CATEGORY);
        areaEmptySeatAdapter = new AreaEmptySeatAdapter(fatherActivity, emptySeats);
        mListView.setAdapter(areaEmptySeatAdapter);
        getData();
    }

    @Override
    public void initListener() {

    }

    /**
     * 刷新数据
     */
    public void getData() {
        mUnCompleteRefreshLayout.setRefreshing(true);
        String url = InfoApi.url_getEmptySeatsList(LocalSetting.DepartmentID, 1, catalog);
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                mUnCompleteRefreshLayout.setRefreshing(false);
                List<String> seats = new ArrayList<String>();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(s);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(n);
                        String seatNo = jsonObject.getString("SeatNo");
                        if (seatNo.startsWith("\"") && seatNo.endsWith("\"")) {
                            seatNo = seatNo.replaceAll("\"", "");
                        }
                        seats.add(seatNo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                emptySeats.clear();
                emptySeats.addAll(seats);
                areaEmptySeatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                mUnCompleteRefreshLayout.setRefreshing(false);
                UIHepler.showToast(fatherActivity, "加载空座位失败");
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.arround, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                getData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if(emptySeats.get(i).contains("重")||emptySeats.get(i).contains("留")){
//            ((AreaInfusionEmptyActivity) fatherActivity).sendBoradcastSetNo(emptySeats.get(i));
//        }else {
//            UIHelper.showToast(fatherActivity,"该区域座位功能尚未开放，敬请期待！");
//        }
//        ((AreaInfusionEmptyActivity)fatherActivity).updateSeatNo(emptySeats.get(i));
        ((AreaInfusionEmptyActivity) fatherActivity).sendBoradcastSetNo(emptySeats.get(i));
    }

    @Override
    public void topRefresh() {
        getData();
    }

    /**
     * 实现数据传递
     *
     * @param callback
     */
    public void getString(Callback callback) {
        String msg = currentSeatNO;
        callback.getString(msg);
    }

    @Override
    public void onRefresh() {
        getData();
    }

    /**
     * 创建接口在穿刺的时候回调
     */
    public interface Callback {
        public void getString(String msg);
    }
}
