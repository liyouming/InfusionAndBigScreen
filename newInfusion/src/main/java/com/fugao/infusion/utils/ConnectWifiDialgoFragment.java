package com.fugao.infusion.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.jasonchen.base.utils.WifiUtils;

/**
 * chenliang
 * 重新连接网络，关闭网络，打开网络
 *
 */
public class ConnectWifiDialgoFragment extends DialogFragment {
    private ProgressBar connect_wifi_progress_bar;
    private TextView connect_wifi_string;
    private View currentView;
    private Activity fatherActivity;
    private TextView update_dialog_close;
    public String ssid;
    public String password;
    public int wifiType;
    public WifiAdmin wifiAdmin;
    public Handler wifiHandler;
    /**
     * wifi 关闭
     */
    public static final int WifiClose=1;
    /**
     * wifi打开
     */
    public static final int WifiOpen=2;
    /**
     * wifi正在连接
     */
    public static final int WifiConneting=3;
    /**
     * wifi 已经连接
     */
    public static final int WifiConnected=4;

    Handler handler=new Handler();

   private boolean  isStartConnect=false;
    public static ConnectWifiDialgoFragment newInstance(String ssid,
                                                   String password,int wifiType) {
        ConnectWifiDialgoFragment updateDialgoFragment = new ConnectWifiDialgoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ssid", ssid);
        bundle.putInt("wifiType", wifiType);
        bundle.putString("password", password);
        updateDialgoFragment.setArguments(bundle);
        return updateDialgoFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fatherActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.connect_wifi_dialog_fragment, null,
                false);
        ssid = getArguments().getString("ssid");
        wifiType = getArguments().getInt("wifiType");
        password = getArguments().getString("password");
        wifiHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case WifiClose:
                        break;
                    case WifiOpen:
                        break;
                    case WifiConneting:
                        break;
                    case WifiConnected:
                        break;
                    default:
                        break;
                }
            }
        };
        initView();
        initListener();
        getDialog().setTitle("连接网络！");
        handler.postDelayed(runnable, 500);
        return currentView;
    }

    public void initView() {
        connect_wifi_progress_bar = (ProgressBar) currentView
                .findViewById(R.id.connect_wifi_progress_bar);

        connect_wifi_string = (TextView) currentView
                .findViewById(R.id.connect_wifi_string);
        update_dialog_close = (Button) currentView
                .findViewById(R.id.update_dialog_close);
        wifiAdmin=new WifiAdmin(fatherActivity);
        wifiAdmin.closeWifi();

    }

    public void initListener() {
        update_dialog_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                closeDialog();
            }
        });

    }

    public void closeDialog() {
        if(getDialog()!=null){
            getDialog().dismiss();
        }

    }

    public  void conectWifeBySSIDBAndDefault(final Activity activity, final String ssid,
                                                   final String password, final int type
                                                  ) {
        // 跳转到设置界面
        WifiAdmin wifiAdmin = new WifiAdmin(activity);
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password,
                type));
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            if(wifiAdmin.checkState()== WifiManager.WIFI_STATE_DISABLED){
                handler.postDelayed(this, 3000);
                wifiAdmin.openWifi();
                connect_wifi_string.setText("正在打开wifi...");
            }else if(wifiAdmin.checkState()==WifiManager.WIFI_STATE_ENABLED){
                if(WifiUtils.isWifiConnected(fatherActivity)){
                    connect_wifi_string.setText("连接完成");
                    handler.removeCallbacks(this);
                    closeDialog();

                }else{
                    if(!isStartConnect){
                        isStartConnect=   wifiAdmin.connectNetwork(wifiAdmin.CreateWifiInfo(ssid, password,
                                wifiType));

                    }
                    connect_wifi_string.setText("正在连接 【"+ssid+"】的无线网，请稍后");
                }
                handler.postDelayed(this, 6000);

            }else{
                handler.postDelayed(this, 3000);
            }


        }
    };
}
