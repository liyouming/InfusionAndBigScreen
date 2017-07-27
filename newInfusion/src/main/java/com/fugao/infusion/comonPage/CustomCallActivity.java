package com.fugao.infusion.comonPage;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.paiyao.PaiYaoActivity;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.view.ComboBox;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.ui.activity.CustomCallActivity
 * @Description: TODO 自定义呼叫内容
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/23 11:20
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class CustomCallActivity extends BaseTempleActivity {
  private static final String TAG = "Fast-CustomCallActivity";

  @InjectView(R.id.cboCall)
  ComboBox cboCall;
  @InjectView(R.id.btnCall) Button btnCall;
  @InjectView(R.id.etCall) EditText etCall;

  @InjectView(R.id.ll_title_back) LinearLayout ll_Title_Back;
  private GroupBottleModel groupBottleModel;
  private String[] callList;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_custom_call);
    }


    @Override public void initView() {
        //initActionBar();
        groupBottleModel = LocalSetting.CurrentGroupBottle;
        callList = handler(ResourceUtils.getResouce4Arrays(context, R.array.call_content));
        cboCall.getEditText().setEnabled(false);
        cboCall.setData(callList);
    }
    @Override
    public void initData() {

    }
    /**
   * 格式化呼叫呼叫内容 XXX--->32号刘前前
   *
   * @param resouce4Arrays 原始呼叫内容
   */
  private String[] handler(String[] resouce4Arrays) {

      String name = groupBottleModel.Name;
      String replaceInfo =
        (StringUtils.StringIsEmpty(groupBottleModel.Lsh) ? "" : groupBottleModel.Lsh + " ")+
                name.replace(name.charAt(1),'*');
      //        (StringUtils.StringIsEmpty(groupBottleModel.SeatNo) ? "" : groupBottleModel.SeatNo + " ")
//            + groupBottleModel.Name;
      //String replaceInfo1=(StringUtils.StringIsEmpty(groupBottleModel.Lsh)?"":groupBottleModel.Lsh+replaceInfo);
    for (int index = 0; index < resouce4Arrays.length; index++) {
      if (resouce4Arrays[index].contains("XXX")) {
        resouce4Arrays[index] = resouce4Arrays[index].replaceAll("XXX", replaceInfo);
      }
    }
    return resouce4Arrays;
  }

  @Override public void initListener() {
    cboCall.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
      @Override public void onItemClick(int position) {
        etCall.setText(callList[position]);
        etCall.setFocusable(true);
      }
    });
    btnCall.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (StringUtils.StringIsEmpty(etCall.getText().toString())) {
          UIHelper.showToast("请输入呼叫内容!");
        } else {
          excuteCall();
        }
      }
    });
    ll_Title_Back.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              finish();
          }
    });


  }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void initIntent() {

    }

    /**
   * 执行呼叫操作
   */
  private void excuteCall() {

      String etCallStr = StringUtils.getStringContainSpecialFlag(etCall.getText().toString());
      /**
       * 排药界面单独增加长按呼叫功能
       */
    if(RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex){
        String url=InfoApi.url_paiYaoCustomCall(LocalSetting.DepartmentID, groupBottleModel.PatId, etCallStr,"1");
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                if ("1".equals(s)) {
                    UIHelper.showToast(context, "呼叫成功!");
                    finish();
                }else {
                    UIHepler.showToast(context,"呼叫失败!" );
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(context,"呼叫失败!" );
            }
        });

    }else {/**
        * 非排药界面单独增加长按呼叫功能
        * */
        String url=InfoApi.url_getCustomCall(LocalSetting.DepartmentID, groupBottleModel.PatId, etCallStr);
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                if ("1".equals(s)) {
                    UIHelper.showToast(context, "呼叫成功!");
                    finish();
                }else {
                    UIHepler.showToast(context,"呼叫失败!" );
                }
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                //InfoUtils.showWifiConnectdDialogFragament(CustomCallActivity.this,error);
                UIHepler.showToast(context,"呼叫失败!" );
            }
        });
    }
  }

  /*private void initActionBar() {
    ActionBar actionBar = getActionBar();
    actionBar.setTitle("呼叫");
    actionBar.setDisplayUseLogoEnabled(false);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);
  }*/

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent;
    switch (item.getItemId()) {
      case android.R.id.home:
        intent = new Intent(this, PaiYaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }
    return false;
  }

    @Override
    protected void onDestroy() {
        LocalSetting.CurrentGroupBottle = null;
        super.onDestroy();
    }
}
