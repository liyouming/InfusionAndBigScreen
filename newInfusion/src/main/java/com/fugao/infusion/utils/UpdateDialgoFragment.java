package com.fugao.infusion.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fugao.infusion.R;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 更新控制中心
 *
 * @author findchen TODO 2013-7-30上午10:25:41
 */
public class UpdateDialgoFragment extends DialogFragment {
    public static final int UPDATE_PROGRESS = 1;

    int controlIndex;

    private View currentView;

    private Activity fatherActivity;

    /**
     * 要重新访问数据的链接
     */
    private ArrayList<String> reconnectString = new ArrayList<String>();

    /**
     * 准备要访问数据的链接
     */
    public ArrayList<String> isPrepareConnectString = new ArrayList<String>();

    /**
     * 进度条
     */
    private ProgressBar updateInfoProgressBar;

    /**
     * 控制按钮
     */
    private Button update_dialog_re_connect, update_dialog_close;

    private Handler updateProgressHandler;

    private int updateProgress = 0;

    private FinalHttp fh;

    private String apkurl;

    private String sdcardPath;

    private TextView textProgressRate;

    public String textProgressRateString;

    public static UpdateDialgoFragment newInstance(String apkurl,
                                                   String sdcardPath) {
        UpdateDialgoFragment updateDialgoFragment = new UpdateDialgoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("apk_url", apkurl);
        bundle.putString("sdcardPath", sdcardPath);
        updateDialgoFragment.setArguments(bundle);
        return updateDialgoFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        fatherActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        currentView = inflater.inflate(R.layout.update_dialog_fragment, null,
                false);
        apkurl = getArguments().getString("apk_url");
        sdcardPath = getArguments().getString("sdcardPath");
        initView();
        initListener();
        isPrepareConnectString = new ArrayList<String>();
        isPrepareConnectString.add(apkurl);
        update();
        getDialog().setTitle("版本更新");
        return currentView;
    }

    public void initView() {
        updateInfoProgressBar = (ProgressBar) currentView
                .findViewById(R.id.updateInfoProgressBar);
        updateInfoProgressBar.setMax(100);
        update_dialog_re_connect = (Button) currentView
                .findViewById(R.id.update_dialog_re_connect);
        update_dialog_close = (Button) currentView
                .findViewById(R.id.update_dialog_close);
        textProgressRate = (TextView) currentView
                .findViewById(R.id.textProgressRate);
    }

    public void initListener() {
        update_dialog_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                closeDialog();
            }
        });

        update_dialog_re_connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                reconnect();
            }
        });

        /**
         * 更新适配器
         */
        updateProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_PROGRESS:
                        updateInfoProgressBar.setProgress(updateProgress);
                        textProgressRate.setText(textProgressRateString);
                        break;

                    default:
                        break;
                }
                if (isPrepareConnectString.size() <= 0) {
                    closeDialog();
                }
            }
        };
    }

    public void closeDialog() {
        getDialog().dismiss();
    }

    public void update() {
        fh = new FinalHttp();
        HttpHandler<File> handler1 = fh.download(apkurl, sdcardPath, false,
                new AjaxCallBack<File>() {
                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        Log.i("down_apk", apkurl);
                    }

                    @Override
                    public void onLoading(long count, long current) {
                        // TODO Auto-generated method stub
                        super.onLoading(count, current);

                        Log.i("down", count + "--" + current);
                        count /= 1024;
                        current /= 1024;
                        if (count > 0) {

                            Float rate = (float) (current * 1f / count);
                            updateProgress = (int) (rate * 100);
                            textProgressRateString = updateProgress + "%"
                                    + current + "KB/" + count + "KB";
                            Log.i("down", textProgressRateString + "--"
                                    + updateProgress + "--" + rate);
                            updateProgressHandler
                                    .sendEmptyMessage(UPDATE_PROGRESS);

                        }
                    }

                    @Override
                    public void onSuccess(File t) {
                        closeDialog();
                        installApk(fatherActivity, sdcardPath);
                        isPrepareConnectString.remove(apkurl);

                    }

                    ;

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        // TODO Auto-generated method stub
                        super.onFailure(t, errorNo, strMsg);
                        reconnectString.add(apkurl);
                    }

                }
        );

    }

    /**
     * 重试 获取服务器端的数据
     */
    public void reconnect() {
        try {
            for (int i = 0; i < reconnectString.size(); i++) {
                String methodName = reconnectString.get(i);
                isPrepareConnectString.add(methodName);
                reconnectString.remove(methodName);
                Method m = this.getClass().getMethod(methodName, Object.class);
                m.invoke(m,  Object.class);
    }

} catch (NoSuchMethodException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void installApk(Context context, String apkFilePath) {
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(i);
    }

}
