package com.fugao.infusion.base;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 碎片基类  support V4的碎片
 * @author findchen
 * TODO
 * 2013-6-18下午1:20:00
 */
public abstract class BaseFragmentV4 extends Fragment {
	/**
	 * 当前activity
	 */
	public Activity fatherActivity;
	/**
	 * 当前视图
	 */
	public View currentView;
    public  DisplayMetrics displayMetrics = new DisplayMetrics();
    public int phoneWidth;
    private ProgressDialog loadingDialog;

    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.fatherActivity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

        fatherActivity.getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        phoneWidth = displayMetrics.widthPixels;
        phoneWidth = displayMetrics.widthPixels;
		currentView = setContentView(inflater, container,
				savedInstanceState);
        ButterKnife.inject(this,currentView);
		initView(currentView);
		initData();
		initListener();
        loadingDialog=new ProgressDialog(fatherActivity);
		return currentView;
	}

	public abstract View setContentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);
	public abstract void initView(View currentView);
	public abstract void initData();
	public abstract void initListener();
    /**
     * 显示加载进度对话框
     */
    public void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(fatherActivity);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setIndeterminate(false);
            loadingDialog.setCancelable(true);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        loadingDialog.setMessage(message);
        loadingDialog.show();
    }

    /**
     * 关闭对话框
     */
    public void dismissLoadingDialog() {
        if(!fatherActivity.isFinishing() && loadingDialog !=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

}
