package com.fugao.newbigscreen.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.newbigscreen.R;
import com.fugao.newbigscreen.constant.BigscreenConstant;
import com.fugao.newbigscreen.constant.InfoApi;
import com.fugao.newbigscreen.model.CallModel;
import com.fugao.newbigscreen.model.InfusionCallAndWaitModel;
import com.fugao.newbigscreen.model.InfusionCallQueue;
import com.fugao.newbigscreen.utils.AppManager;
import com.fugao.newbigscreen.utils.String2InfusionModel;
import com.fugao.newbigscreen.utils.TextSpeaker;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/** 注射呼叫
 * Created by li on 2016/6/17.
 */
public class InjectionWaitingActivity extends BaseTempleActivity{
    private TextView nowTime;
    private TextView injection_room1;
    private TextView injection_room2;
    private TextView injection_room3;
    private TextView injection_room4;
    private TextView injection_room5;
    private TextView injection_room6;
    private TextView overNo_room1;
    private TextView overNo_room2;
    private TextView overNo_room3;
    private TextView overNo_room4;
    private TextView overNo_room5;
    private TextView overNo_room6;
//    private TextView overNo_news;
    private TextView call_textview;
    private LinearLayout inject_background;
    private String currentDate;
    private String currentTime;
    private String currentWeek;
    private String stringtext;
    public static Handler mainHandler;
    private InfusionCallAndWaitModel infusionCallAndWaitModel;
    private List<CallModel> overCall;
    private List<CallModel> fourRoom;
    private String limit="20";
    private String departmentId="";
    private String type="0";
    private String message;
    private TextSpeaker textSpeaker;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private Timer timer3=new Timer();
    private String url="http://172.18.12.206:8011/Update/android/icons/bigscreen_background.png";
    @Override
    public void setContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newinjection_layout);
    }

    @Override
    public void initView() {
        nowTime= (TextView) findViewById(R.id.nowTime);
        injection_room1= (TextView) findViewById(R.id.injection_room1);
        injection_room2= (TextView) findViewById(R.id.injection_room2);
        injection_room3= (TextView) findViewById(R.id.injection_room3);
        injection_room4= (TextView) findViewById(R.id.injection_room4);
        injection_room5= (TextView) findViewById(R.id.injection_room5);
        injection_room6= (TextView) findViewById(R.id.injection_room6);
        overNo_room1= (TextView) findViewById(R.id.overNo_room1);
        overNo_room2= (TextView) findViewById(R.id.overNo_room2);
        overNo_room3= (TextView) findViewById(R.id.overNo_room3);
        overNo_room4= (TextView) findViewById(R.id.overNo_room4);
        overNo_room5= (TextView) findViewById(R.id.overNo_room5);
        overNo_room6= (TextView) findViewById(R.id.overNo_room6);
//        overNo_news= (TextView) findViewById(R.id.overNo_news);
        call_textview= (TextView) findViewById(R.id.call_textview);
        inject_background= (LinearLayout) findViewById(R.id.inject_background);
        textSpeaker=new TextSpeaker(InjectionWaitingActivity.this);
        TextPaint tp = call_textview.getPaint();
        tp.setFakeBoldText(true);
//        initconfig();
    }

    @Override
    public void initData() {
//        showDate();
        timer3.schedule(task3,1000,60000);
        timer2.schedule(task2, 2000, 7000);
        timer1.schedule(task1, 3000, 12000);

        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        getData();
                        break;
                    case 2:
                        getCall();
                        break;
                    case 3:
//                        showDate();
                        getSyctime();
                        break;
                }
            }
        };
//        PollingUtils.startPollingService(this, 40, PollingService.class,
//                PollingService.ACTION);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }
    private void initconfig(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(1920, 1080)          // default = device screen dimensions
                .threadPoolSize(3)                          // default
                .threadPriority(Thread.NORM_PRIORITY - 1)   // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)              // default
                .discCacheSize(50 * 1920 * 1080)        // 缓冲大小
                .discCacheFileCount(100)                // 缓冲文件数目
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.bigground)          // image在加载过程中，显示的图片
//                .showImageForEmptyUri(R.drawable.bigground)  // empty URI时显示的图片
//                .showImageOnFail(R.drawable.bigground)       // 不是图片文件 显示图片
//                .resetViewBeforeLoading(false)  // default
//                .delayBeforeLoading(1000)
//                .cacheInMemory(false)           // default 不缓存至内存
//                .cacheOnDisc(false)             // default 不缓存至手机SDCard
//                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// default
//                .bitmapConfig(Bitmap.Config.ARGB_8888)              // default
//                .displayer(new SimpleBitmapDisplayer()) // default 可以设置动画，比如圆角或者渐变
//                .handler(new Handler())                             // default
//                .build();
        imageLoader.loadImage(url,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                inject_background.setBackgroundDrawable(new BitmapDrawable(loadedImage));
            }
        });

    }
    /**
     * 得到同步时间
     */
    private void getSyctime(){
        String url= InfoApi.getSyctime();
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                currentDate=s.substring(1,11);
                currentTime=s.substring(s.length()-9,s.length()-4);
                currentWeek=getCurrentWeek();
                stringtext=currentDate+"  "+currentWeek+"      "+currentTime;
                nowTime.setText(stringtext);
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {

            }
        });
    }
    /**
     * 得到呼叫信息
     */
    private void getCall(){
        String url= InfoApi.getinfusionCallQueueUrl("all");
//        String msg="请380号到3号注射室穿刺";
//        call_textview.setText(msg);
//        textSpeaker.speak(msg);
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
//                s="{\"Type\":\"1\",\"Name\":\"欧萍\",\"CallNo\":\"584\",\"DateAndTime\":\"20160624152513\",\"CallRoom\":\"注射室1\"}";
//                s="{\"Type\":\"2\",\"Name\":\"明萱\",\"CallNo\":\"614\",\"DateAndTime\":\"20160625105339\",\"CallRoom\":\"请614 明*到门急诊输液药房9号窗口\"}";
                InfusionCallQueue infusionCallQueue=String2InfusionModel.string2InfusionCallQueue(s);
                if(infusionCallQueue.Name!=null){
                    if(StringUtils.getString(infusionCallQueue.Type).equals("1")){
                        String msg="请 "+StringUtils.getString(infusionCallQueue.CallNo)+"号  "+"\n"+StringUtils.getString(infusionCallQueue.Name)+
                                "\n"+"到"+StringUtils.getString(infusionCallQueue.CallRoom)+" 穿刺";
                        call_textview.setText(msg);
                        textSpeaker.speak(msg);
                    }else if(StringUtils.getString(infusionCallQueue.Type).equals("2")){
                        String msg=StringUtils.getString(infusionCallQueue.CallRoom);
                        call_textview.setText(msg);
                        textSpeaker.speak(msg);
                    }
//                    Intent intent=new Intent();
//                    intent.setClass(InjectionWaitingActivity.this,CallDialogActivity.class);
//                    Bundle bundle=new Bundle();
//                    bundle.putParcelable("infusioncallqueue",infusionCallQueue);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {

            }
        });
    }
    /**
     * 加载数据
     */
    private void getData(){
        String url= InfoApi.getGetCallandwait(departmentId,limit,type);
        RestClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String s=new String(bytes,"UTF-8");
//                    s="{\"WaitList\":[],\"OverCall\":[{\"LSH_Call\":4837,\"CallType\":5,\"CallContent\":\"582\",\"CallDate\":\"20160620\",\"CallTime\":\"170746\",\"Remark\":\"注射室1\",\"Status\":1,\"OperateId\":null,\"RelevanceId\":\"0071609014\",\"RelevanceName\":\"李雅杰\",\"InfusionId\":\"201506020582\",\"EndDate\":null,\"EndTime\":null,\"CreateTime\":\"2016-06-20T17:07:46\",\"AreaId\":0,\"InfusionStatus\":0},{\"LSH_Call\":4836,\"CallType\":5,\"CallContent\":\"580\",\"CallDate\":\"20160620\",\"CallTime\":\"170729\",\"Remark\":\"注射室1\",\"Status\":1,\"OperateId\":null,\"RelevanceId\":\"0071105110\",\"RelevanceName\":\"叶逸尘\",\"InfusionId\":\"201506020580\",\"EndDate\":null,\"EndTime\":null,\"CreateTime\":\"2016-06-20T17:07:29\",\"AreaId\":0,\"InfusionStatus\":0}],\"FourRoom\":[]}";
                    infusionCallAndWaitModel= String2InfusionModel.string2InfusionCallAndWaitModel(s);
                    setview();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                String s="";
//                Log.d("error1","222");
            }
        });
    }
    /**
     * 适配
     */
    private void setview(){
        String zhu1="";
        String zhu2="";
        String zhu3="";
        String zhu4="";
        String zhu5="";
        String zhu6="";
        String guo="";
        String guo1="";
        String guo2="";
        String guo3="";
        String guo4="";
        String guo5="";
        String guo6="";
        overCall=infusionCallAndWaitModel.OverCall;
        fourRoom=infusionCallAndWaitModel.FourRoom;
        if(fourRoom!=null&&fourRoom.size()>0){
            for(int i=0;i<fourRoom.size();i++){
                if(fourRoom.get(i).Remark.equals("1号穿刺台")){
//                    zhu1=zhu1+fourRoom.get(i).CallContent+"  ";
                    zhu1=zhu1+fourRoom.get(i).RelevanceName+"("+fourRoom.get(i).CallContent+")   ";
//                    zhu1=zhu1+ BigscreenConstant.getHideName(fourRoom.get(i).RelevanceName)+"（"+fourRoom.get(i).CallContent+")"+ "  ";
                }else if(fourRoom.get(i).Remark.equals("2号穿刺台")){
//                    zhu2=zhu2+fourRoom.get(i).CallContent+"  ";
                    zhu2=zhu2+fourRoom.get(i).RelevanceName+"("+fourRoom.get(i).CallContent+")   ";
//                    zhu2=zhu2+BigscreenConstant.getHideName(fourRoom.get(i).RelevanceName)+"->"+fourRoom.get(i).CallContent+"  ";
                }else if(fourRoom.get(i).Remark.equals("3号穿刺台")){
//                    zhu3=zhu3+fourRoom.get(i).CallContent+"  ";
                    zhu3=zhu3+fourRoom.get(i).RelevanceName+"("+fourRoom.get(i).CallContent+")   ";
//                    zhu3=zhu3+BigscreenConstant.getHideName(fourRoom.get(i).RelevanceName)+"->"+fourRoom.get(i).CallContent+"  ";
                }else if(fourRoom.get(i).Remark.equals("4号穿刺台")){
//                    zhu4=zhu4+fourRoom.get(i).CallContent+"  ";
                    zhu4=zhu4+fourRoom.get(i).RelevanceName+"("+fourRoom.get(i).CallContent+")   ";
//                    zhu4=zhu4+BigscreenConstant.getHideName(fourRoom.get(i).RelevanceName)+"->"+fourRoom.get(i).CallContent+"  ";
                }else if(fourRoom.get(i).Remark.equals("5号穿刺台")){
//                    zhu5=zhu5+fourRoom.get(i).CallContent+"  ";
                    zhu5=zhu5+fourRoom.get(i).RelevanceName+"("+fourRoom.get(i).CallContent+")   ";
//                    zhu5=zhu5+BigscreenConstant.getHideName(fourRoom.get(i).RelevanceName)+"->"+fourRoom.get(i).CallContent+"  ";
                }else if(fourRoom.get(i).Remark.equals("6号穿刺台")){
//                    zhu6=zhu6+fourRoom.get(i).CallContent+"  ";
                    zhu6=zhu6+fourRoom.get(i).RelevanceName+"("+fourRoom.get(i).CallContent+")   ";
//                    zhu6=zhu6+BigscreenConstant.getHideName(fourRoom.get(i).RelevanceName)+"->"+fourRoom.get(i).CallContent+"  ";
                }
            }
        }
        if(overCall!=null&&overCall.size()>0){
            for(int i=0;i<overCall.size();i++){
                guo=guo+BigscreenConstant.getHideName(overCall.get(i).RelevanceName)+"->"+overCall.get(i).Remark+"  ";
                if(overCall.get(i).Remark.equals("1号穿刺台")){
//                    guo1=guo1+overCall.get(i).CallContent+"  ";
                    guo1=guo1+overCall.get(i).RelevanceName+"("+overCall.get(i).CallContent+")   ";
//                    guo1=guo1+BigscreenConstant.getHideName(overCall.get(i).RelevanceName)+"->"+overCall.get(i).CallContent+"  ";
                }else if(overCall.get(i).Remark.equals("2号穿刺台")){
//                    guo2=guo2+overCall.get(i).CallContent+"  ";
                    guo2=guo2+overCall.get(i).RelevanceName+"("+overCall.get(i).CallContent+")   ";
//                    guo2=guo2+BigscreenConstant.getHideName(overCall.get(i).RelevanceName)+"->"+overCall.get(i).CallContent+"  ";
                }else if(overCall.get(i).Remark.equals("3号穿刺台")){
//                    guo3=guo3+overCall.get(i).CallContent+"  ";
                    guo3=guo3+overCall.get(i).RelevanceName+"("+overCall.get(i).CallContent+")   ";
//                    guo3=guo3+BigscreenConstant.getHideName(overCall.get(i).RelevanceName)+"->"+overCall.get(i).CallContent+"  ";
                }else if(overCall.get(i).Remark.equals("4号穿刺台")){
//                    guo4=guo4+overCall.get(i).CallContent+"  ";
                    guo4=guo4+overCall.get(i).RelevanceName+"("+overCall.get(i).CallContent+")   ";
//                    guo4=guo4+BigscreenConstant.getHideName(overCall.get(i).RelevanceName)+"->"+overCall.get(i).CallContent+"  ";
                }else if(overCall.get(i).Remark.equals("5号穿刺台")){
//                    guo5=guo5+overCall.get(i).CallContent+"  ";
                    guo5=guo5+overCall.get(i).RelevanceName+"("+overCall.get(i).CallContent+")   ";
                }else if(overCall.get(i).Remark.equals("6号穿刺台")){
//                    guo6=guo6+overCall.get(i).CallContent+"  ";
                    guo6=guo6+overCall.get(i).RelevanceName+"("+overCall.get(i).CallContent+")   ";
                }
            }
        }
        injection_room1.setText(zhu1);
        injection_room2.setText(zhu2);
        injection_room3.setText(zhu3);
        injection_room4.setText(zhu4);
        injection_room5.setText(zhu5);
        injection_room6.setText(zhu6);
        overNo_room1.setText(guo1);
        overNo_room2.setText(guo2);
        overNo_room3.setText(guo3);
        overNo_room4.setText(guo4);
        overNo_room5.setText(guo5);
        overNo_room6.setText(guo6);
//        overNo_news.setText(guo);
    }
    /**
     * 显示日期
     */
    private void showDate(){
        currentDate= DateUtils.getCurrentDate("yyyyMMdd");
        currentTime=DateUtils.getCurrentDate("HH:mm:ss");
        currentWeek=getCurrentWeek();
        stringtext=currentDate+"  "+currentWeek+"      "+currentTime;
        nowTime.setText(stringtext);
    }
    /**
     * 得到当前星期几
     * @return
     */
    private String getCurrentWeek(){
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.exite(InjectionWaitingActivity.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    TimerTask task1 = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            mainHandler.sendMessage(message);
        }
    };
    TimerTask task2=new TimerTask() {
        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 2;
            mainHandler.sendMessage(message);
        }
    };
    TimerTask task3=new TimerTask() {
        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 3;
            mainHandler.sendMessage(message);
        }
    };
    @Override
    protected void onDestroy() {
//        PollingUtils.stopPollingService(this, PollingService.class,
//                PollingService.ACTION);
        super.onDestroy();
    }
}
