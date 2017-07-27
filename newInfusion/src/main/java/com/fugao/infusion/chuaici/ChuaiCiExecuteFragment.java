package com.fugao.infusion.chuaici;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class ChuaiCiExecuteFragment extends BaseFragmentV4 implements SwipeRefreshLayout.OnRefreshListener {


//    @InjectView(R.id.title_text_view)
//    TextView mTitleTextView;
//    @InjectView(R.id.listView)
//    AnimatedExpandableListView mListView;
//    @InjectView(R.id.un_complete_refresh_layout)
//    SwipeRefreshLayout mUnCompleteRefreshLayout;
//
//    private List<GroupBottleModel> groupBottleModels = new ArrayList<GroupBottleModel>();
//    private ChuanCiGroupBottleAdapter bottleAdapter;
//    private String patientId;

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chuai_ci, container, false);
    }

    @Override
    public void initView(View currentView) {
//        mUnCompleteRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void initData() {
//        bottleAdapter = new ChuanCiGroupBottleAdapter(fatherActivity, groupBottleModels);
    }


    public void getData(String patientId){
//        this.patientId = patientId;
//        showLoadingDialog("正在查找瓶贴...");
//        String url = ChuanCiApi.Url_GetBottlesByPid(patientId);
//        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                mUnCompleteRefreshLayout.setRefreshing(true);
//                super.onStart();
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                mUnCompleteRefreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            public void onSuccess(int i, String s) {
//                dismissLoadingDialog();
//                List<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
//                if(bottleModels.size()>0) {
//                    groupBottleModels.clear();
//                    groupBottleModels.addAll(InfoUtils.toGroup(bottleModels));
//                }else {
//                    groupBottleModels.clear();
//                    UIHepler.showToast(fatherActivity, "没有数据");
//                    bottleAdapter.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onFailure(int i, Throwable throwable, String s) {
//                Log.e("扫描瓶贴返回失败");
//                dismissLoadingDialog();
//                UIHepler.showToast(fatherActivity, "查找失败");
//            }
//        });
    }
    @Override
    public void initListener() {

    }

    @Override
    public void onRefresh() {
//        if(patientId!=null&!"".equals(patientId)){
//            getData(patientId);
//        }
    }
}
