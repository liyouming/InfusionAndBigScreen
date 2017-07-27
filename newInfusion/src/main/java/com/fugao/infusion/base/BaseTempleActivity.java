package com.fugao.infusion.base;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.fugao.infusion.R;

import butterknife.ButterKnife;

/**
 * activity 模版类
 * @author findchen_2013
 * 2013年11月5日下午2:39:26
 */
public abstract class BaseTempleActivity extends FragmentActivity {
	public ActionBar actionBar;
	public DisplayMetrics displayMetrics = new DisplayMetrics();
	public int windowWidth;
    private ProgressDialog loadingDialog;
    public Context context;
    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindowManager().getDefaultDisplay()
		.getMetrics(displayMetrics);
		windowWidth = displayMetrics.widthPixels;
		setContentView();
        ButterKnife.inject(this);
        context=this;
		actionBar = getActionBar();
		initIntent();
		initView();
		initData();
		initListener();
        createProgressDialog(BaseTempleActivity.this);

	}
    @Override
    public void onResume() {
        super.onResume();

       // MobclickAgent.onResume(this);          //统计时长
    }
    @Override
    public void onPause() {
        super.onPause();
       // MobclickAgent.onPause(this);
    }
	public abstract void setContentView();

	
	public abstract void initView();

	public abstract void initData();

	
	public abstract void initListener();
	
	public abstract void initIntent();
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    public  ProgressDialog createProgressDialog(Context context) {
        loadingDialog = new ProgressDialog(context);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(context.getResources().getString(R.string.defaultLoadingInfo));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        return loadingDialog;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    /**
     * 显示加载进度对话框
     */
    public void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(context);
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
        if(!this.isFinishing() && loadingDialog !=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }


    /**
     * 通过Class跳转界面
     */
    public void openActivity(Class<?> cls) {
        openActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     */
    public void openActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
//    /**
//     * 跳转动画,从左进如
//     */
//    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
