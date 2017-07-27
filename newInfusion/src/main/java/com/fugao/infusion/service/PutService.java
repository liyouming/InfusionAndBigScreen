package com.fugao.infusion.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.ProgressBar;

import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.dao.UploadInfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时上传输液数据中 操作过的数据的任务，
 * 1.支持  医嘱执行任务
 * 2.
 */
public class PutService extends Service {
    public static final String ACTION = "com.android.fastinfusion.utils.service.PutService";
//    private UnPutInfusionDetailDAO unInfusionDetailDAO;
    private UploadInfusionDetailDAO uploadInfusionDetailDAO;
    private InfusionDetailDAO infusionDetailDAO;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        Log.e("PutService 启动");
//        unInfusionDetailDAO = new UnPutInfusionDetailDAO(DataBaseInfo.getInstance(getApplicationContext()));
        uploadInfusionDetailDAO = new UploadInfusionDetailDAO(DataBaseInfo.getInstance(getApplicationContext()));
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(getApplicationContext()));


    }

    @Override
    public void onStart(Intent intent, int startId) {
//        PutData();
        new PutThread().start();
    }

    class PutThread extends Thread {
        @Override
        public void run() {
            Log.e("上传任务执行");
            PutData();
        }
    }

    /**
     * 上传数据
     */
    private void PutData(){

        final List<BottleModel> bottleModels = checkData();
        if(bottleModels.size()==0){
            return;
        }
        Log.e("上传任务执行");
        String postData = JacksonHelper.model2String(bottleModels);
       final String ip =  XmlDB.getInstance(PutService.this).getKeyString("ip", "");
        final String porp =  XmlDB.getInstance(PutService.this).getKeyString("port", "");
        String acountId = XmlDB.getInstance(PutService.this).getKeyString("AcountId","");
        String url = "http://"+ ip +":"+porp+"/"+ ChuanCiApi.url_updateBottlesAndQueues(acountId);
        RestClient.putSyn(PutService.this, url, postData,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {

                        uploadInfusionDetailDAO.updateIsUploadStatusToUploading((ArrayList)bottleModels);
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                        unInfusionDetailDAO.saveToInfusionDetail((ArrayList) bottleModels);
                        uploadInfusionDetailDAO.updateIsUploadStatus((ArrayList) bottleModels);
                        Log.d("本地数据上传成功");
//                                                Toast.makeText(PutService.this, "穿刺任务上传成功," + bottleModels.size(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes,
                                          Throwable throwable) {
                        uploadInfusionDetailDAO.updateIsUploadStatusToUnUpload((ArrayList)bottleModels);
                        Log.d("本地数据上传失败");

//                                                Toast.makeText(PutService.this, "穿刺任务上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private List<BottleModel> checkData(){
        List<BottleModel>  bottleModels = uploadInfusionDetailDAO.getBottleListAndUnUpload();
        return bottleModels;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("PutService");
    }


}
