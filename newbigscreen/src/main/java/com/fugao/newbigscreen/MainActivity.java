package com.fugao.newbigscreen;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugao.newbigscreen.activitys.BaseTempleActivity;
import com.fugao.newbigscreen.activitys.CallDialogActivity;
import com.fugao.newbigscreen.adapter.GridviewAdapter;
import com.fugao.newbigscreen.constant.BigscreenConstant;
import com.fugao.newbigscreen.constant.InfoApi;
import com.fugao.newbigscreen.model.BottleModel;
import com.fugao.newbigscreen.model.InfusionCallAndWaitModel;
import com.fugao.newbigscreen.model.InfusionCallQueue;
import com.fugao.newbigscreen.model.QueueModel;
import com.fugao.newbigscreen.utils.PollingService;
import com.fugao.newbigscreen.utils.StandardGridView;
import com.fugao.newbigscreen.utils.String2InfusionModel;
import com.fugao.newbigscreen.utils.TextSpeaker;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.PollingUtils;
import com.jasonchen.base.utils.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseTempleActivity {
    private TextView main_nowTime;
    private StandardGridView standardGridView;
    private GridviewAdapter gridviewAdapter;
    private RelativeLayout mainlayout;
    public static Handler mainHandler;
    private String currentDate;
    private String currentWeek;
    private String stringtext;
    private String callMessage;
    private String departmentId="100001";
    private String limit="20";
    private String type="1";
    private List<String> namestrings;
    private List<String> namestrings1;
    private List<QueueModel> waitList;
    private String[] names_string;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private Timer timer3=new Timer();
    private TextSpeaker textSpeaker;
    private InfusionCallAndWaitModel infusionCallAndWaitModel;
    private String url="http://192.168.252.1:8011/Update/android/icons/bigscreen_background.png";
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView() {
        main_nowTime= (TextView) findViewById(R.id.main_nowTime);
        standardGridView= (StandardGridView) findViewById(R.id.standard_gridview);
        textSpeaker=new TextSpeaker(MainActivity.this);
        mainlayout= (RelativeLayout) findViewById(R.id.main_layout);
//        mainlayout.setBackgroundDrawable(new BitmapDrawable(getResources(),returnBitmap(url)));
        initconfig();
    }

    @Override
    public void initData() {
//        names_string=getResources().getStringArray(R.array.names_string);
//        namestrings1= Arrays.asList(names_string);
//        namestrings=new ArrayList<>();
//        for(int i=0;i<namestrings1.size();i++){
//            namestrings.add(BigscreenConstant.getHideName(namestrings1.get(i)));
//        }
        waitList=new ArrayList<>();
        gridviewAdapter=new GridviewAdapter(MainActivity.this,waitList);
        standardGridView.setAdapter(gridviewAdapter);
        timer1.schedule(task1, 1000, 1000);
        timer2.schedule(task2, 2000, 10000);
        timer3.schedule(task3, 3000, 10000);
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        showDate();
                        break;
                    case 2:
                        getCall();
                        break;
                    case 3:
                        getdata();
                        break;
                    case 4:
                        callMessage= (String) msg.obj;
                        Log.d("111","111");
                        break;
                }
            }
        };
    }
    TimerTask task1=new TimerTask() {
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
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }

    public void getdata(){
        String url= InfoApi.getGetCallandwait(departmentId,limit,type);
        RestClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String s=new String(bytes,"UTF-8");
                    infusionCallAndWaitModel= String2InfusionModel.string2InfusionCallAndWaitModel(s);
                    if(infusionCallAndWaitModel.WaitList!=null){
                        waitList=infusionCallAndWaitModel.WaitList;
                        gridviewAdapter.notifyadapter(waitList);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
    /**
     * 得到呼叫信息
     */
    private void getCall() {
        String url = InfoApi.getinfusionCallQueueUrl("2");
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
//                s="{\"Type\":\"1\",\"Name\":\"欧萍\",\"CallNo\":\"584\",\"DateAndTime\":\"20160624152513\",\"CallRoom\":\"注射室1\"}";
//                s = "{\"Type\":\"2\",\"Name\":\"明萱\",\"CallNo\":\"614\",\"DateAndTime\":\"20160625105339\",\"CallRoom\":\"请614 明*到门急诊输液药房9号窗口\"}";
                InfusionCallQueue infusionCallQueue = String2InfusionModel.string2InfusionCallQueue(s);
                if (infusionCallQueue.Name != null) {
                    String msg = infusionCallQueue.CallRoom;
                    textSpeaker.speak(msg);
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, CallDialogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("infusioncallqueue", infusionCallQueue);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {

            }
        });
    }
    /**
     * 显示日期
     */
    private void showDate(){
        currentDate= DateUtils.getCurrentDate("yyyyMMdd");
        currentWeek=getCurrentWeek();
        stringtext=currentDate+"  "+currentWeek;
        main_nowTime.setText(stringtext);
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
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.bigground)          // image在加载过程中，显示的图片
                .showImageForEmptyUri(R.drawable.bigground)  // empty URI时显示的图片
                .showImageOnFail(R.drawable.bigground)       // 不是图片文件 显示图片
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheInMemory(false)           // default 不缓存至内存
                .cacheOnDisc(false)             // default 不缓存至手机SDCard
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// default
                .bitmapConfig(Bitmap.Config.ARGB_8888)              // default
                .displayer(new SimpleBitmapDisplayer()) // default 可以设置动画，比如圆角或者渐变
                .handler(new Handler())                             // default
                .build();
        imageLoader.loadImage(url,options,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                mainlayout.setBackgroundDrawable(new BitmapDrawable(loadedImage));
            }
        });

    }
    @Override
    protected void onDestroy() {
//        PollingUtils.stopPollingService(this, PollingService.class,
//                PollingService.ACTION);
        super.onDestroy();
    }
}
