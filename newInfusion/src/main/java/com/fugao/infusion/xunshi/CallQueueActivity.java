/*
 * Copyright (C) 2014 loQua.Xee <loquaciouser@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fugao.infusion.xunshi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseScanTestTempleActivity;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.CallModel;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import org.codehaus.jackson.type.TypeReference;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.activity.CallQueueActivity
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/12 20:38
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class CallQueueActivity extends BaseScanTestTempleActivity implements
        SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "Fast-CallQueueActivity";

    @InjectView(R.id.un_complete_refresh_layout)
    SwipeRefreshLayout mUnCompleteRefreshLayout;
    @InjectView(R.id.title_text_view)
    TextView mTitleTextView;
    @InjectView(R.id.listView)
    ListView mListView;
    private MenuItem refreshItem;
    private View view;

    private List<CallModel> callQueues;
    private CallQueueAdapter callQueueAdapter;

    private static final int UPDATE_TIME = 1;

    private TimeThread timeThread;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
                    callQueueAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private SimpleDateFormat simpleDateFormat;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_queue);
    }

    @Override
    public void initView() {
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
    }

    @Override
    public void initData() {
        callQueues = new ArrayList<CallModel>();
        callQueueAdapter = new CallQueueAdapter(context, callQueues);
        mListView.setAdapter(callQueueAdapter);
        getData();
    }

    @Override
    public void initListener() {
        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showEditTextDialog(position);
            }
        });
    }

    @Override
    public void initIntent() {

    }

    /**
     * 取消呼叫点击事件
     * @param position
     */
    private void showEditTextDialog(final int position) {
//       final EditText editText = new EditText(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.view_edittext_layout, null);
//        final EditText editText = (EditText) view.findViewById(R.id.dialog_edittext);
//        LinearLayout view_height = (LinearLayout) view.findViewById(R.id.dialog_view);
//        view_height.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50));
        new AlertDialog.Builder(context).setTitle("是否取消呼叫")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        String inputText = StringUtils.getStringContainSpecialFlag(editText.getText().toString());

                          String inputText = "手动执行取消呼叫";
                        if (StringUtils.StringIsEmpty(inputText)) {
//                          editText.setError("不能输入为空!");
                            dialogIsClose(dialog, false);
                        } else {
                            dialogIsClose(dialog, true);
                            String url ="";
                            String callContent ="";
                            String callMessage ="";
                            if(callQueues.size()>0 && callQueues.size()>=position){
                                callContent = callQueues.get(position).CallContent;
                            }
                            if(StringUtils.getString(callContent).contains("+")){
                                callMessage= callContent.replace("+","_");
                                url= ChuanCiApi.Url_CancelCallBySeat(callMessage,LocalSetting.CurrentAccount.Id,
                                        inputText);
                            }else {
                                url= ChuanCiApi.Url_CancelCallBySeat(callContent,LocalSetting.CurrentAccount.Id,
                                        inputText);
                            }
                            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, String s) {
                                    if(i ==200){
                                        UIHepler.showToast(CallQueueActivity.this, "取消呼叫成功");
                                        if(callQueues.size()>=position)callQueues.remove(position);
                                        callQueueAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onFailure(int i, Throwable throwable, String s) {
                                    UIHepler.showToast(CallQueueActivity.this, "取消呼叫失败");
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogIsClose(dialog, true);
                    }
                })
                .create()
                .show();
    }

    public void dialogIsClose(DialogInterface dialog, boolean isClose) {
        try {
            Field field = null;
            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, isClose); // 设定为false,则不可以关闭对话框
            dialog.dismiss();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

//    private void setRefreshing(boolean refreshing) {
//        if (mPullToRefreshLayout == null) return;
//        mPullToRefreshLayout.setRefreshing(refreshing);
//        if (refreshItem == null) return;
//
//        if (refreshing) {
//            refreshItem.setActionView(R.layout.actionbar_refresh_progress);
//        } else {
//            refreshItem.setActionView(null);
//            mPullToRefreshLayout.setRefreshComplete();
//        }
//    }

    /**
     * 刷新数据
     */
    private void getData() {
        String areaID = XmlDB.getInstance(context).getKeyString("areaID", "1");
        String url = ChuanCiApi.Url_GetCall(1,areaID);
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mUnCompleteRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int arge, String s) {
                mUnCompleteRefreshLayout.setRefreshing(false);
                List<CallModel> callModels = JacksonHelper.getObjects(s,new TypeReference<List<CallModel>>() {});
                if (callModels.size() > 0) {
                    callQueues.clear();
                    callQueues.addAll(callModels);
                    for (CallModel anInfusionCall : callQueues) {
                        String callTime = anInfusionCall.CallTime;
                        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        try {
                            Date covertCallTime = simpleDateFormat.parse(anInfusionCall.CallDate + callTime);
                            Date nowTime = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
                            long l = nowTime.getTime() - covertCallTime.getTime();

                            long min = ((l / (60 * 1000)));
                            long second = (l / 1000 - (min * 60));
                            anInfusionCall.TimeMinute = (int) min;
                            anInfusionCall.TimeSecond = (int) second;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    callQueueAdapter.notifyDataSetChanged();
                    for (int i = 0; i < callQueueAdapter.getCount(); i++) {
                        timeThread = new TimeThread(callQueues.get(i));
                        timeThread.start();
                    }
                } else {
                    callQueueAdapter = new CallQueueAdapter(context, new ArrayList<CallModel>());
                    mListView.setAdapter(callQueueAdapter);
                    UIHepler.showToast(CallQueueActivity.this, "当前没有呼叫信息");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(CallQueueActivity.this, "获取失败");
                mUnCompleteRefreshLayout.setRefreshing(false);
            }
        });
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = UPDATE_TIME;
                handler.sendMessage(msg);
            }
        }, 0, 1000);

    }

    @Override
    protected void receiverPatientId(String patientId) {
        UIHepler.showToast(this, "此界面不能扫腕带!");
    }

    @Override
    protected void receiverBottleId(final String patientId, final String bottleId) {

        showLoadingDialog("正在查询此人呼叫信息...");
        String url = ChuanCiApi.url_getBottleByBottleId(bottleId);
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
             final   BottleModel bottleModel = JacksonHelper.getObject(s,new TypeReference<BottleModel>() {});
                if (bottleModel != null) {
                    if (!checkExist(bottleModel)) {
                        InfusionHelper.showWarningDialog(context, "此人不在呼叫列表中!请返回后再执行!!!");
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Constant.RECEIVER_SCAN_RESULT);
                        intent.putExtra("result", "fgv1|sy|" + patientId + "|" + bottleId + "|" + "88");
                        CallQueueActivity.this.sendBroadcast(intent);
                        new AlertDialog.Builder(context).setTitle("呼叫详情")
                                .setMessage(bottleModel.PeopleInfo.Name + "\n" + "门诊号：" + bottleModel.PeopleInfo.PatId
                                        + "\n" + "科室：" + bottleModel.PeopleInfo.DepartmentId + "\n" + "流水号" + bottleModel.PeopleInfo.QueueNo).
                                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        dialogIsClose(dialog, true);
                                    }
                                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                cancel(bottleModel);
                            }
                        }).show();

                    }
                } else {
                    InfusionHelper.showWarningDialog(CallQueueActivity.this, "未找到该瓶贴!");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                UIHepler.showDilalog(CallQueueActivity.this,"查询失败");
            }
        });
    }

    private void cancel(final BottleModel bottleModel) {
        String url = ChuanCiApi.Url_CancelCall( bottleModel.InfusionId,
                LocalSetting.CurrentAccount.Id, null);
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                UIHepler.showToast(CallQueueActivity.this,"取消呼叫成功!");
                if(callQueues.size()>0){
                    for(int j =0; j<callQueues.size(); j++){
                        if(TextUtils.equals(bottleModel.PeopleInfo.SeatNo, callQueues.get(j).CallContent)){
                            callQueues.remove(j);
                        }
                    }
                }
                callQueueAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(CallQueueActivity.this,"取消呼叫失败!");
            }
        });
    }


    /**
     * 判断该人有没有呼叫过
     */
    private boolean checkExist(BottleModel info) {

        for (CallModel call : callQueues) {
            if (call.CallContent.equals(info.PeopleInfo.SeatNo)) return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    /**
     * 开启线程控制呼叫时间
     */
    class TimeThread extends Thread {
        private CallModel callModel;

        public TimeThread(CallModel callModel) {
            this.callModel = callModel;
        }

        @Override
        public void run() {
            while (true) {
                if (callQueues.size() < 0) return;
                //        CallModel call = callQueues.get(index);
                if (callModel == null) return;
                if (callModel.TimeMinute < 0) {
                    callModel.TimeMinute = 0;
                    callModel.TimeSecond = 0;
                }
                if (callModel.TimeSecond < 59) {
                    callModel.TimeSecond += 1;
                } else {
                    callModel.TimeSecond = 0;
                    callModel.TimeMinute += 1;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
