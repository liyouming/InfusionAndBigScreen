package com.fugao.infusion.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fugao.infusion.constant.TableConstant;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.UnUpdateModel;
import com.fugao.infusion.utils.DateUtils;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.StringUtils;

import java.util.List;

/**
 * 护理任务Dao类(CRUD)
 *
 * @author findchen 2013-7-16下午4:29:10
 */
@Deprecated
public class UnPutInfusionDetailDAO {
    public String TAG = "UnPutInfusionDetailDAO";
    public DataBaseInfo dataBaseInfo;
    public SQLiteDatabase sqlDB;

    public UnPutInfusionDetailDAO(DataBaseInfo dataBaseInfoCurrent) {
        dataBaseInfo = dataBaseInfoCurrent;
        sqlDB = dataBaseInfo.getSqlDB();
    }

    public boolean isEmpty() {
        return dataBaseInfo.tableIsEmpty(TableConstant.TABLE_INFUSION_UNPUT);
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        dataBaseInfo.closeDB();
    }

    public void deleteBottleByQueueNo(String BottleId){
        sqlDB.delete(TableConstant.TABLE_INFUSION_UNPUT,"BottleId = ?",new String[]{BottleId});
    }
    public void saveToInfusionDetail(BottleModel bottleModel) {
        Log.d(TAG, "保存瓶贴信息到本地数据库");
        sqlDB.beginTransaction(); // 手动设置开始事务
        ContentValues contentValues;
        contentValues = new ContentValues();
            contentValues.put("BottleId", StringUtils.getString(bottleModel.BottleId));
            contentValues.put("InfusionId", StringUtils.getString(bottleModel.InfusionId));
            contentValues.put("InfusionNo", bottleModel.InfusionNo);
            sqlDB.insert(TableConstant.TABLE_INFUSION_UNPUT, null, contentValues);
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
        Log.d(TAG, "保存医嘱任务执行数据到本地数据库 完成");
    }

    /**
     * 保存需要执行的医嘱的到本地
     *
     * @param
     */
    public void saveToInfusionDetail(List<BottleModel> bottleModels) {
        Log.d(TAG, "保存瓶贴信息到本地数据库");
        sqlDB.beginTransaction(); // 手动设置开始事务
        ContentValues contentValues;
        // 数据插入操作循环
        for (BottleModel bottleModel : bottleModels) {
            contentValues = getContentValues(bottleModel);
            sqlDB.insert(TableConstant.TABLE_INFUSION_UNPUT, null, contentValues);
        }
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
        Log.d(TAG, "保存医嘱任务执行数据到本地数据库 完成");
    }


    private UnUpdateModel getBottleModelByCursor(Cursor cursor){
        UnUpdateModel bottleModel = new UnUpdateModel();
        while (cursor.moveToNext()) {
            String BottleId = cursor.getString(cursor.getColumnIndex("BottleId"));
            String PutDate = cursor.getString(cursor.getColumnIndex("PutDate"));
            String QueueNo = cursor.getString(cursor.getColumnIndex("QueueNo"));
            bottleModel.BottleId = BottleId;
            bottleModel.PutDate = PutDate;
            bottleModel.QueueNo = QueueNo;
        }
        cursor.close();
        return bottleModel;
    }

    /**
     * 删除表中所有的数据
     *
     * @return
     */
    public int deleteAllInfo() {
        return sqlDB.delete(TableConstant.TABLE_INFUSION_UNPUT, null, null);
    }
    /**
     * 构建contentvalues
     */
    private ContentValues getContentValues(BottleModel bottle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("BottleId", bottle.BottleId);
        contentValues.put("PutDate", DateUtils.getCurrentYearDate());
        contentValues.put("QueueNo", bottle.PeopleInfo.QueueNo);
//        contentValues.put("IsUpload", Constant.UN_UPLOAD);
        return contentValues;
    }

}
