package com.fugao.newbigscreen.activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fugao.newbigscreen.R;
import com.fugao.newbigscreen.model.InfusionCallQueue;
import com.fugao.newbigscreen.utils.TextSpeaker;

import java.util.Timer;
import java.util.TimerTask;

/** 呼叫提示类
 * Created by li on 2016/6/24.
 */
public class CallDialogActivity extends BaseTempleActivity{
    private TextView call_textview;
    private Timer timer=new Timer();
    private static Handler mainHandler;
    private InfusionCallQueue infusionCallQueue;
    private TextSpeaker textSpeaker;
    String msg1;
    @Override
    public void setContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calldialog_layout);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.7
        // p.height = 432;
        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.5
        // p.y = -10;
        getWindow().setAttributes(p);
    }

    @Override
    public void initView() {
        call_textview= (TextView) findViewById(R.id.call_textview);
    }
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            mainHandler.sendMessage(message);
        }
    };
    @Override
    public void initData() {


        call_textview.setText(msg1);

        timer.schedule(task,4000,10000);
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        CallDialogActivity.this.finish();
                        break;
                }
            }
        };
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {
        Intent intent=getIntent();
        infusionCallQueue=intent.getParcelableExtra("infusioncallqueue");
//        textSpeaker=new TextSpeaker(CallDialogActivity.this);
        if(infusionCallQueue.Type.equals("1")){
            msg1="请 "+infusionCallQueue.Name+" 到 "+infusionCallQueue.CallRoom+" 穿刺";
        }else if(infusionCallQueue.Type.equals("2")){
            msg1=infusionCallQueue.CallRoom;
        }
//        textSpeaker.speak(msg1);
    }
}
