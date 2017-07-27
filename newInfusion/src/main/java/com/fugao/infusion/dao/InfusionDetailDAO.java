package com.fugao.infusion.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.TableConstant;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugDetailModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.utils.DateUtils;
import com.fugao.infusion.utils.String2InfusionModel;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.StringUtils;

import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * 输液任务Dao类(CRUD)
 *
 * @author findchen 2013-7-16下午4:29:10
 */
public class InfusionDetailDAO {
    public String TAG = "InfusionDetailDAO";
    public DataBaseInfo dataBaseInfo;
    public SQLiteDatabase sqlDB;
    public static String planExecuteDate = "";

    public InfusionDetailDAO(DataBaseInfo dataBaseInfoCurrent) {
        dataBaseInfo = dataBaseInfoCurrent;
        sqlDB = dataBaseInfo.getSqlDB();
        planExecuteDate = DateUtils.getCurrentDate("yyyyMMDD");
    }

    public boolean isEmpty() {
        return dataBaseInfo.tableIsEmpty(TableConstant.TABLE_INFUSION);
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        dataBaseInfo.closeDB();
    }


    public void save(List<BottleModel> bottleModels) {  //得到
        /**获取所有的主键**/
        String sql1 = "select BottleId from " + TableConstant.TABLE_INFUSION;
        ArrayList<String> keys = new ArrayList<String>();
        Cursor cursor = sqlDB.rawQuery(sql1, null);
        while (cursor.moveToNext()) {
            keys.add(cursor.getString(cursor.getColumnIndex("BottleId")));
        }
        cursor.close();
        ArrayList<BottleModel> bottleModels1 = new ArrayList<BottleModel>();
        /**保存进数据库的集合**/
        for (String key : keys) {
            for (BottleModel bottleModel : bottleModels) {
                if (key.equals(bottleModel.BottleId)) {
                    bottleModels1.add(bottleModel);
                }
            }
        }
        bottleModels.removeAll(bottleModels1);
        saveToInfusionDetail(bottleModels);
    }

    public void saveExecuteSingBottleModel(BottleModel bottleModel) {
        ContentValues contentValues = getUpdateContentValues(bottleModel);
        sqlDB.beginTransaction();
        sqlDB.insert(TableConstant.TABLE_INFUSION, null, contentValues);
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
    }

    public void saveToInfusionDetail(BottleModel bottleModel) {
        Log.d(TAG, "保存瓶贴信息到本地数据库");
        sqlDB.beginTransaction(); // 手动设置开始事务
        ContentValues contentValues;
        contentValues = new ContentValues();

        contentValues.put("BottleId", StringUtils.getString(bottleModel.BottleId));
        contentValues.put("InfusionId", StringUtils.getString(bottleModel.InfusionId));
        contentValues.put("InfusionNo", bottleModel.InfusionNo);
        contentValues.put("InvoicingId", bottleModel.InvoicingId);
        contentValues.put("PrescriptionId", bottleModel.PrescriptionId);
        contentValues.put("DoctorId", StringUtils.getString(bottleModel.DoctorId));
        contentValues.put("DoctorCore", StringUtils.getString(bottleModel.DoctorCore));
        contentValues.put("DoctorName", StringUtils.getString(bottleModel.DoctorName));
        contentValues.put("DiagnoseCore", StringUtils.getString(bottleModel.DiagnoseCore));
        contentValues.put("DiagnoseName", StringUtils.getString(bottleModel.DiagnoseName));
        contentValues.put("PrescribeDate", StringUtils.getString(bottleModel.PrescribeDate));
        contentValues.put("PrescribeTime", StringUtils.getString(bottleModel.PrescribeTime));
        contentValues.put("GroupId", StringUtils.getString(bottleModel.GroupId));
        contentValues.put("BottleStatus", bottleModel.BottleStatus);
        contentValues.put("Way", StringUtils.getString(bottleModel.Way));
        contentValues.put("Frequency", StringUtils.getString(bottleModel.Frequency));
        contentValues.put("TransfusionBulk", bottleModel.TransfusionBulk);
        contentValues.put("TransfusionSpeed", StringUtils.getString(bottleModel.TransfusionSpeed));
        contentValues.put("ExpectTime", bottleModel.ExpectTime);
        contentValues.put("SubscribeDate", StringUtils.getString(bottleModel.SubscribeDate));
        contentValues.put("SubscribeTime", StringUtils.getString(bottleModel.SubscribeTime));
        contentValues.put("RegistrationDate", StringUtils.getString(bottleModel.RegistrationDate));
        contentValues.put("RegistrationTime", StringUtils.getString(bottleModel.RegistrationTime));
        contentValues.put("RegistrationId", bottleModel.RegistrationId);
        contentValues.put("RegistrationCore", StringUtils.getString(bottleModel.RegistrationCore));
        contentValues.put("PillDate", StringUtils.getString(bottleModel.PillDate));
        contentValues.put("PillTime", StringUtils.getString(bottleModel.PillTime));
        contentValues.put("PillId", bottleModel.PillId);
        contentValues.put("PillCore", StringUtils.getString(bottleModel.PillCore));
        contentValues.put("LiquorDate", StringUtils.getString(bottleModel.LiquorDate));
        contentValues.put("LiquorTime", StringUtils.getString(bottleModel.LiquorTime));
        contentValues.put("LiquorId", bottleModel.LiquorId);
        contentValues.put("LiquorCore", StringUtils.getString(bottleModel.LiquorCore));
        contentValues.put("InfusionDate", StringUtils.getString(bottleModel.InfusionDate));
        contentValues.put("InfusionTime", StringUtils.getString(bottleModel.InfusionTime));
        contentValues.put("InfusionPeopleId", bottleModel.InfusionPeopleId);
        contentValues.put("InfusionCore", StringUtils.getString(bottleModel.InfusionCore));
        contentValues.put("EndDate", StringUtils.getString(bottleModel.EndDate));
        contentValues.put("EndTime", StringUtils.getString(bottleModel.EndTime));
        contentValues.put("EndId", bottleModel.EndId);
        contentValues.put("EndCore", StringUtils.getString(bottleModel.EndCore));
        contentValues.put("Remark", StringUtils.getString(bottleModel.Remark));
        contentValues.put("SeatNo", StringUtils.getString(bottleModel.SeatNo));
        contentValues.put("SpeedUnit", StringUtils.getString(bottleModel.SpeedUnit));
        contentValues.put("DrugDetails", JacksonHelper.model2String(bottleModel.DrugDetails));
        contentValues.put("PeopleInfo", JacksonHelper.model2String(bottleModel.PeopleInfo));
        contentValues.put("IsUpload", Constant.IS_FROM_SERVER);
        contentValues.put("PatId", StringUtils.getString(bottleModel.PeopleInfo.PatId));
        contentValues.put("AboutPatrols", JacksonHelper.model2String(bottleModel.AboutPatrols));
        contentValues.put("PillName", StringUtils.getString(bottleModel.PillName));
        contentValues.put("LiquorName", StringUtils.getString(bottleModel.LiquorName));
        contentValues.put("InfusionName", StringUtils.getString(bottleModel.InfusionName));
        contentValues.put("EndName", StringUtils.getString(bottleModel.EndName));
        contentValues.put("LZZ", StringUtils.getString(bottleModel.LZZ));
        contentValues.put("GCF", StringUtils.getString(bottleModel.GCF));
        contentValues.put("CheckCore", StringUtils.getString(bottleModel.CheckCore));
        contentValues.put("CheckName", StringUtils.getString(bottleModel.CheckName));
        contentValues.put("CheckDate", StringUtils.getString(bottleModel.CheckDate));
        contentValues.put("CheckTime", StringUtils.getString(bottleModel.CheckTime));
        sqlDB.insert(TableConstant.TABLE_INFUSION, null, contentValues);
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
            contentValues = new ContentValues();
            contentValues.put("BottleId", StringUtils.getString(bottleModel.BottleId));
            contentValues.put("InfusionId", StringUtils.getString(bottleModel.InfusionId));
            contentValues.put("InfusionNo", bottleModel.InfusionNo);
            contentValues.put("InvoicingId", bottleModel.InvoicingId);
            contentValues.put("PrescriptionId", bottleModel.PrescriptionId);
            contentValues.put("DoctorId", StringUtils.getString(bottleModel.DoctorId));
            contentValues.put("DoctorCore", StringUtils.getString(bottleModel.DoctorCore));
            contentValues.put("DoctorName", StringUtils.getString(bottleModel.DoctorName));
            contentValues.put("DiagnoseCore", StringUtils.getString(bottleModel.DiagnoseCore));
            contentValues.put("DiagnoseName", StringUtils.getString(bottleModel.DiagnoseName));
            contentValues.put("PrescribeDate", StringUtils.getString(bottleModel.PrescribeDate));
            contentValues.put("PrescribeTime", StringUtils.getString(bottleModel.PrescribeTime));
            contentValues.put("GroupId", StringUtils.getString(bottleModel.GroupId));
            contentValues.put("BottleStatus", bottleModel.BottleStatus);
            contentValues.put("Way", StringUtils.getString(bottleModel.Way));
            contentValues.put("Frequency", StringUtils.getString(bottleModel.Frequency));
            contentValues.put("TransfusionBulk", bottleModel.TransfusionBulk);
            contentValues.put("TransfusionSpeed", StringUtils.getString(bottleModel.TransfusionSpeed));
            contentValues.put("ExpectTime", bottleModel.ExpectTime);
            contentValues.put("SubscribeDate", StringUtils.getString(bottleModel.SubscribeDate));
            contentValues.put("SubscribeTime", StringUtils.getString(bottleModel.SubscribeTime));
            contentValues.put("RegistrationDate", StringUtils.getString(bottleModel.RegistrationDate));
            contentValues.put("RegistrationTime", StringUtils.getString(bottleModel.RegistrationTime));
            contentValues.put("RegistrationId", bottleModel.RegistrationId);
            contentValues.put("RegistrationCore", StringUtils.getString(bottleModel.RegistrationCore));
            contentValues.put("PillDate", StringUtils.getString(bottleModel.PillDate));
            contentValues.put("PillTime", StringUtils.getString(bottleModel.PillTime));
            contentValues.put("PillId", bottleModel.PillId);
            contentValues.put("PillCore", StringUtils.getString(bottleModel.PillCore));
            contentValues.put("LiquorDate", StringUtils.getString(bottleModel.LiquorDate));
            contentValues.put("LiquorTime", StringUtils.getString(bottleModel.LiquorTime));
            contentValues.put("LiquorId", bottleModel.LiquorId);
            contentValues.put("LiquorCore", StringUtils.getString(bottleModel.LiquorCore));
            contentValues.put("InfusionDate", StringUtils.getString(bottleModel.InfusionDate));
            contentValues.put("InfusionTime", StringUtils.getString(bottleModel.InfusionTime));
            contentValues.put("InfusionPeopleId", bottleModel.InfusionPeopleId);
            contentValues.put("InfusionCore", StringUtils.getString(bottleModel.InfusionCore));
            contentValues.put("EndDate", StringUtils.getString(bottleModel.EndDate));
            contentValues.put("EndTime", StringUtils.getString(bottleModel.EndTime));
            contentValues.put("EndId", bottleModel.EndId);
            contentValues.put("EndCore", StringUtils.getString(bottleModel.EndCore));
            contentValues.put("Remark", StringUtils.getString(bottleModel.Remark));
            contentValues.put("SeatNo", StringUtils.getString(bottleModel.SeatNo));
            contentValues.put("SpeedUnit", StringUtils.getString(bottleModel.SpeedUnit));
            contentValues.put("DrugDetails", JacksonHelper.model2String(bottleModel.DrugDetails));
            contentValues.put("PeopleInfo", JacksonHelper.model2String(bottleModel.PeopleInfo));
            contentValues.put("IsUpload", Constant.IS_FROM_SERVER);
            contentValues.put("PatId", StringUtils.getString(bottleModel.PeopleInfo.PatId));
            contentValues.put("AboutPatrols", JacksonHelper.model2String(bottleModel.AboutPatrols));
            contentValues.put("PillName", StringUtils.getString(bottleModel.PillName));
            contentValues.put("LiquorName", StringUtils.getString(bottleModel.LiquorName));
            contentValues.put("InfusionName", StringUtils.getString(bottleModel.InfusionName));
            contentValues.put("EndName", StringUtils.getString(bottleModel.EndName));
            contentValues.put("LZZ", StringUtils.getString(bottleModel.LZZ));
            contentValues.put("GCF", StringUtils.getString(bottleModel.GCF));
            contentValues.put("CheckCore", StringUtils.getString(bottleModel.CheckCore));
            contentValues.put("CheckName", StringUtils.getString(bottleModel.CheckName));
            contentValues.put("CheckDate", StringUtils.getString(bottleModel.CheckDate));
            contentValues.put("CheckTime", StringUtils.getString(bottleModel.CheckTime));
            sqlDB.insert(TableConstant.TABLE_INFUSION, null, contentValues);
        }
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
        Log.d(TAG, "保存医嘱任务执行数据到本地数据库 完成");
    }

    /**
     * TODO 更新本地数据库
     *
     * @param bottleModels
     * @return
     */
    public int updateLocalData(ArrayList<BottleModel> bottleModels) {
        return updateToInfusionDetail(bottleModels, Constant.UN_UPLOAD);
    }

    /**
     * 根据门诊号删除瓶贴
     */
    public void deleteBottlesByPatid(String patId) {
        sqlDB.beginTransaction();
        sqlDB.delete(TableConstant.TABLE_INFUSION, "PatId = ?", new String[]{patId});
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
    }

    /**
     * TODO 更新服务器数据
     *
     * @param bottleModels
     * @return
     */
    public int updateDateFromServer(ArrayList<BottleModel> bottleModels) {
        return updateToInfusionDetail(bottleModels, Constant.IS_FROM_SERVER);
    }

    /**
     * TODO 更新一组瓶贴信息状态为未上传
     *
     * @param bottle 瓶贴
     * @return 返回受影响行数
     */
    public int updateBottle(BottleModel bottle) {
        sqlDB.beginTransaction();
        ContentValues values = getUpdateContentValues(bottle);
        int updateStatus = sqlDB.update(TableConstant.TABLE_INFUSION, values,
                "BottleId= ?", new String[]{bottle.BottleId});
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
        return updateStatus;
    }

    /**
     * TODO 更新一组瓶贴信息状态为已上传
     *
     * @param bottle 瓶贴
     * @return 返回受影响行数
     */
    public int updatePeople(BottleModel bottle) {
        ContentValues values = getUpdateContentValuesForPeople(bottle);
        return sqlDB.update(TableConstant.TABLE_INFUSION, values,
                "BottleId= ?", new String[]{bottle.BottleId});
    }

    /**
     * 更新一组瓶贴信息
     * 不更新巡视记录
     *
     * @param bottle 瓶贴
     * @return 返回受影响行数
     */
    public int update(BottleModel bottle, boolean flag) {
        ContentValues values = getContentValues(bottle, flag);
        return sqlDB.update(TableConstant.TABLE_INFUSION, values,
                "BottleId= ?", new String[]{bottle.BottleId});
    }

    /**
     * boolean 不更新巡视记录
     * 构建contentvalues
     */
    private ContentValues getContentValues(BottleModel bottle, boolean flag) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BottleColumns.BottleId, bottle.BottleId);
        contentValues.put(BottleColumns.InfusionId, bottle.InfusionId);
        contentValues.put(BottleColumns.InfusionNo, bottle.InfusionNo);
        contentValues.put(BottleColumns.InvoicingId, bottle.InvoicingId);
        contentValues.put(BottleColumns.PrescriptionId, bottle.PrescriptionId);
        contentValues.put(BottleColumns.DoctorId, StringUtils.getString(bottle.DoctorId));
        contentValues.put(BottleColumns.DoctorCore, StringUtils.getString(bottle.DoctorCore));
        contentValues.put(BottleColumns.DoctorName, StringUtils.getString(bottle.DoctorName));
        contentValues.put(BottleColumns.DiagnoseCore, StringUtils.getString(bottle.DiagnoseCore));
        contentValues.put(BottleColumns.DiagnoseName, StringUtils.getString(bottle.DiagnoseName));
        contentValues.put(BottleColumns.PrescribeDate, StringUtils.getString(bottle.PrescribeDate));
        contentValues.put(BottleColumns.PrescribeTime, StringUtils.getString(bottle.PrescribeTime));
        contentValues.put(BottleColumns.GroupId, bottle.GroupId);
        contentValues.put(BottleColumns.BottleStatus, bottle.BottleStatus);
        contentValues.put(BottleColumns.Way, StringUtils.getString(bottle.Way));
        contentValues.put(BottleColumns.Frequency, StringUtils.getString(bottle.Frequency));
        contentValues.put(BottleColumns.TransfusionBulk, bottle.TransfusionBulk);
        contentValues.put(BottleColumns.TransfusionSpeed, bottle.TransfusionSpeed);
        contentValues.put(BottleColumns.ExpectTime, bottle.ExpectTime);
        contentValues.put(BottleColumns.SubscribeDate, StringUtils.getString(bottle.SubscribeDate));
        contentValues.put(BottleColumns.SubscribeTime, StringUtils.getString(bottle.SubscribeTime));
        contentValues.put(BottleColumns.RegistrationDate, StringUtils.getString(bottle.RegistrationDate));
        contentValues.put(BottleColumns.RegistrationTime, StringUtils.getString(bottle.RegistrationTime));
        contentValues.put(BottleColumns.RegistrationId, bottle.RegistrationId);
        contentValues.put(BottleColumns.RegistrationCore, StringUtils.getString(bottle.RegistrationCore));
        contentValues.put(BottleColumns.PillDate, StringUtils.getString(bottle.PillDate));
        contentValues.put(BottleColumns.PillTime, StringUtils.getString(bottle.PillTime));
        contentValues.put(BottleColumns.PillId, bottle.PillId);
        contentValues.put(BottleColumns.PillCore, StringUtils.getString(bottle.PillCore));
        contentValues.put(BottleColumns.LiquorDate, StringUtils.getString(bottle.LiquorDate));
        contentValues.put(BottleColumns.LiquorTime, StringUtils.getString(bottle.LiquorTime));
        contentValues.put(BottleColumns.LiquorId, bottle.LiquorId);
        contentValues.put(BottleColumns.LiquorCore, StringUtils.getString(bottle.LiquorCore));
        contentValues.put(BottleColumns.InfusionDate, StringUtils.getString(bottle.InfusionDate));
        contentValues.put(BottleColumns.InfusionTime, StringUtils.getString(bottle.InfusionTime));
        contentValues.put(BottleColumns.InfusionPeopleId, bottle.InfusionPeopleId);
        contentValues.put(BottleColumns.InfusionCore, StringUtils.getString(bottle.InfusionCore));
        contentValues.put(BottleColumns.EndDate, StringUtils.getString(bottle.EndDate));
        contentValues.put(BottleColumns.EndTime, StringUtils.getString(bottle.EndTime));
        contentValues.put(BottleColumns.EndId, bottle.EndId);
        contentValues.put(BottleColumns.EndCore, StringUtils.getString(bottle.EndCore));
        contentValues.put(BottleColumns.Remark, StringUtils.getString(bottle.Remark));
        contentValues.put(BottleColumns.SeatNo, StringUtils.getString(bottle.SeatNo));
        contentValues.put(BottleColumns.DrugDetails, StringUtils.getString(JacksonHelper.model2String(bottle.DrugDetails)));
        contentValues.put(BottleColumns.PeopleInfo, StringUtils.getString(JacksonHelper.model2String(bottle.PeopleInfo)));
        if (flag) {
            contentValues.put(BottleColumns.AboutPatrols, StringUtils.getString(JacksonHelper.model2String(bottle.AboutPatrols)));
        }
        contentValues.put(BottleColumns.PatId, bottle.PeopleInfo.PatId);
        contentValues.put(BottleColumns.PillName, StringUtils.getString(bottle.PillName));
        contentValues.put(BottleColumns.LiquorName, StringUtils.getString(bottle.LiquorName));
        contentValues.put(BottleColumns.InfusionName, StringUtils.getString(bottle.InfusionName));
        contentValues.put(BottleColumns.EndName, StringUtils.getString(bottle.EndName));
        contentValues.put(BottleColumns.LZZ, StringUtils.getString(bottle.LZZ));
        contentValues.put(BottleColumns.GCF, StringUtils.getString(bottle.GCF));
        contentValues.put(BottleColumns.CheckCore, StringUtils.getString(bottle.CheckCore));
        contentValues.put(BottleColumns.CheckName, StringUtils.getString(bottle.CheckName));
        contentValues.put(BottleColumns.CheckDate, StringUtils.getString(bottle.CheckDate));
        contentValues.put(BottleColumns.CheckTime, StringUtils.getString(bottle.CheckTime));

        return contentValues;
    }

    /**
     * 得到待输个数
     *
     * @param pid 病人门诊号
     */
    public int getUnBottlesCount(String pid) {
        Cursor cursor = sqlDB.query(TableConstant.TABLE_INFUSION, null, "PatId = ? and BottleStatus=?", new String[]{pid, BottleStatusCategory.WAITINGINFUSE.getKey() + ""}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * 根据瓶贴ID获取瓶贴
     *
     * @param bottleID 瓶贴ID
     * @return 返回瓶贴对象
     */
    public BottleModel getBottleById(String bottleID) {
        BottleModel retBottle = null;
        String sql = "select * from " + TableConstant.TABLE_INFUSION + " where BottleId='" + bottleID + "'";
        Cursor cursor = sqlDB.rawQuery(sql, null);
        retBottle = fromCursor(cursor);
        cursor.close();
        return retBottle;
    }

    /**
     * 根据瓶贴状态查询
     *
     * @param BottlsStatus
     * @return TODO 巡视需要的查询
     */
    public ArrayList<BottleModel> getBottleByBottlsStatus(String BottlsStatus) {
        BottleModel retBottle = null;
        String sql = "select * from " + TableConstant.TABLE_INFUSION + " where BottleStatus='" + BottlsStatus + "'";
        return getInfusionDeatailBySql(sql);


    }


    /**
     * 通过门诊号获得 瓶贴集合
     *
     * @param patid
     * @return
     */
    public List<BottleModel> getBottleByPatid(String patid) {
        String sql = "select * from " + TableConstant.TABLE_INFUSION + " where PatId='" + patid + "'";
        return getInfusionDeatailBySql(sql);
    }


    /**
     * 获得正在输液中的瓶贴个数
     *
     * @param patid
     * @return
     */
    public int getInfusioningCountByPatid(String patid) {
        String sql = "select * from " + TableConstant.TABLE_INFUSION + " where PatId='" + patid + "'"
                + " and " + "BottleStatus" + "=" + BottleStatusCategory.INFUSIONG.getKey();
        return getInfusionDeatailBySql(sql).size();
    }

    private BottleModel fromCursor(Cursor cursor) {
        BottleModel bottleModel = new BottleModel();
        while (cursor.moveToNext()) {
            String BottleId = cursor.getString(cursor.getColumnIndex("BottleId"));
            String InfusionId = cursor.getString(cursor.getColumnIndex("InfusionId"));
            int InfusionNo = cursor.getInt(cursor.getColumnIndex("InfusionNo"));
            int InvoicingId = cursor.getInt(cursor.getColumnIndex("InvoicingId"));
            int PrescriptionId = cursor.getInt(cursor.getColumnIndex("PrescriptionId"));
            String DoctorId = cursor.getString(cursor.getColumnIndex("DoctorId"));
            String DoctorCore = cursor.getString(cursor.getColumnIndex("DoctorCore"));
            String DoctorName = cursor.getString(cursor.getColumnIndex("DoctorName"));
            String DiagnoseCore = cursor.getString(cursor.getColumnIndex("DiagnoseCore"));
            String DiagnoseName = cursor.getString(cursor.getColumnIndex("DiagnoseName"));
            String PrescribeDate = cursor.getString(cursor.getColumnIndex("PrescribeDate"));
            String PrescribeTime = cursor.getString(cursor.getColumnIndex("PrescribeTime"));
            String GroupId = cursor.getString(cursor.getColumnIndex("GroupId"));
            int BottleStatus = cursor.getInt(cursor.getColumnIndex("BottleStatus"));
            String Way = cursor.getString(cursor.getColumnIndex("Way"));
            String Frequency = cursor.getString(cursor.getColumnIndex("Frequency"));
            int TransfusionBulk = cursor.getInt(cursor.getColumnIndex("TransfusionBulk"));
            String TransfusionSpeed = cursor.getString(cursor.getColumnIndex("TransfusionSpeed"));
            int ExpectTime = cursor.getInt(cursor.getColumnIndex("ExpectTime"));
            String SubscribeDate = cursor.getString(cursor.getColumnIndex("SubscribeDate"));
            String SubscribeTime = cursor.getString(cursor.getColumnIndex("SubscribeTime"));
            String RegistrationDate = cursor.getString(cursor.getColumnIndex("RegistrationDate"));
            String RegistrationTime = cursor.getString(cursor.getColumnIndex("RegistrationTime"));
            int RegistrationId = cursor.getInt(cursor.getColumnIndex("RegistrationId"));
            String RegistrationCore = cursor.getString(cursor.getColumnIndex("RegistrationCore"));
            String PillDate = cursor.getString(cursor.getColumnIndex("PillDate"));
            String PillTime = cursor.getString(cursor.getColumnIndex("PillTime"));
            int PillId = cursor.getInt(cursor.getColumnIndex("PillId"));
            String PillCore = cursor.getString(cursor.getColumnIndex("PillCore"));
            String LiquorDate = cursor.getString(cursor.getColumnIndex("LiquorDate"));
            String LiquorTime = cursor.getString(cursor.getColumnIndex("LiquorTime"));
            int LiquorId = cursor.getInt(cursor.getColumnIndex("LiquorId"));
            String LiquorCore = cursor.getString(cursor.getColumnIndex("LiquorCore"));
            String InfusionDate = cursor.getString(cursor.getColumnIndex("InfusionDate"));
            String InfusionTime = cursor.getString(cursor.getColumnIndex("InfusionTime"));
            int InfusionPeopleId = cursor.getInt(cursor.getColumnIndex("InfusionPeopleId"));
            String InfusionCore = cursor.getString(cursor.getColumnIndex("InfusionCore"));
            String EndDate = cursor.getString(cursor.getColumnIndex("EndDate"));
            String EndTime = cursor.getString(cursor.getColumnIndex("EndTime"));
            int EndId = cursor.getInt(cursor.getColumnIndex("EndId"));
            String EndCore = cursor.getString(cursor.getColumnIndex("EndCore"));
            String Remark = cursor.getString(cursor.getColumnIndex("Remark"));
            String SeatNo = cursor.getString(cursor.getColumnIndex("SeatNo"));
            String SpeedUnit = cursor.getString(cursor.getColumnIndex("SpeedUnit"));
            String DrugDetails = cursor.getString(cursor.getColumnIndex("DrugDetails"));
            String PeopleInfo = cursor.getString(cursor.getColumnIndex("PeopleInfo"));
            int IsUpload = cursor.getInt(cursor.getColumnIndex("IsUpload"));
            String PatId = cursor.getString(cursor.getColumnIndex("PatId"));
            String AboutPatrols = cursor.getString(cursor.getColumnIndex("AboutPatrols"));
            String PillName = cursor.getString(cursor.getColumnIndex("PillName"));
            String LiquorName = cursor.getString(cursor.getColumnIndex("LiquorName"));
            String InfusionName = cursor.getString(cursor.getColumnIndex("InfusionName"));
            String EndName = cursor.getString(cursor.getColumnIndex("EndName"));
            String LZZ = cursor.getString(cursor.getColumnIndex("LZZ"));
            String GCF = cursor.getString(cursor.getColumnIndex("GCF"));
            String CheckCore = cursor.getString(cursor.getColumnIndex("CheckCore"));
            String CheckName = cursor.getString(cursor.getColumnIndex("CheckName"));
            String CheckDate = cursor.getString(cursor.getColumnIndex("CheckDate"));
            String CheckTime = cursor.getString(cursor.getColumnIndex("CheckTime"));
            bottleModel.BottleId = BottleId;
            bottleModel.InfusionId = InfusionId;
            bottleModel.InfusionNo = InfusionNo;
            bottleModel.InvoicingId = InvoicingId;
            bottleModel.PrescriptionId = PrescriptionId;
            bottleModel.DoctorId = DoctorId;
            bottleModel.DoctorCore = DoctorCore;
            bottleModel.DoctorName = DoctorName;
            bottleModel.DiagnoseCore = DiagnoseCore;
            bottleModel.DiagnoseName = DiagnoseName;
            bottleModel.PrescribeDate = PrescribeDate;
            bottleModel.PrescribeTime = PrescribeTime;
            bottleModel.GroupId = GroupId;
            bottleModel.BottleStatus = BottleStatus;
            bottleModel.Way = Way;
            bottleModel.Frequency = Frequency;
            bottleModel.TransfusionBulk = TransfusionBulk;
            bottleModel.TransfusionSpeed = TransfusionSpeed;
            bottleModel.ExpectTime = ExpectTime;
            bottleModel.SubscribeDate = SubscribeDate;
            bottleModel.SubscribeTime = SubscribeTime;
            bottleModel.RegistrationDate = RegistrationDate;
            bottleModel.RegistrationTime = RegistrationTime;
            bottleModel.RegistrationId = RegistrationId;
            bottleModel.RegistrationCore = RegistrationCore;
            bottleModel.PillDate = PillDate;
            bottleModel.PillTime = PillTime;
            bottleModel.PillId = PillId;
            bottleModel.PillCore = PillCore;
            bottleModel.LiquorDate = LiquorDate;
            bottleModel.LiquorTime = LiquorTime;
            bottleModel.LiquorId = LiquorId;
            bottleModel.LiquorCore = LiquorCore;
            bottleModel.InfusionDate = InfusionDate;
            bottleModel.InfusionTime = InfusionTime;
            bottleModel.InfusionPeopleId = InfusionPeopleId;
            bottleModel.InfusionCore = InfusionCore;
            bottleModel.EndDate = EndDate;
            bottleModel.EndTime = EndTime;
            bottleModel.EndId = EndId;
            bottleModel.EndCore = EndCore;
            bottleModel.Remark = Remark;
            bottleModel.SeatNo = SeatNo;
            bottleModel.SpeedUnit = SpeedUnit;
            if (!StringUtils.StringIsEmpty(DrugDetails)) {
                bottleModel.DrugDetails = JacksonHelper.getObjects(DrugDetails, new TypeReference<List<DrugDetailModel>>() {});

            }
            bottleModel.PeopleInfo = String2InfusionModel.string2QueueModel(PeopleInfo);

            bottleModel.IsUpload = IsUpload;
            bottleModel.PatId = PatId;
            bottleModel.AboutPatrols = JacksonHelper.getObjects(AboutPatrols, new TypeReference<List<PatrolModel>>() {});
            bottleModel.PillName = PillName;
            bottleModel.LiquorName = LiquorName;
            bottleModel.InfusionName = InfusionName;
            bottleModel.EndName = EndName;
            bottleModel.LZZ = LZZ;
            bottleModel.GCF = GCF;
            bottleModel.CheckCore = CheckCore;
            bottleModel.CheckName = CheckName;
            bottleModel.CheckDate = CheckDate;
            bottleModel.CheckTime = CheckTime;
        }
        return bottleModel;
    }

    /**
     * 从服务器端返回的数据 来更新本地的数据,用来显示当前数据已经上传成功 确定唯一性 根据[ 频率 医嘱编号 住院号]
     *
     * @param bottleModels
     * @Title: updateToInfusionDetail
     * @Description: TODO
     * @return: void
     */
    public int updateToInfusionDetail(ArrayList<BottleModel> bottleModels,
                                      int statues) {
        Log.d(TAG, statues + "数据 来更新本地的数据,用来显示当前数据");
        int flag = -1;
        sqlDB.beginTransaction(); // 手动设置开始事务

        // 数据插入操作循环
        for (BottleModel bottleModel : bottleModels) {
            ContentValues contentValues;
            contentValues = new ContentValues();
            contentValues.put("BottleId", StringUtils.getString(bottleModel.BottleId));
            contentValues.put("InfusionId", StringUtils.getString(bottleModel.InfusionId));
            contentValues.put("InfusionNo", bottleModel.InfusionNo);
            contentValues.put("InvoicingId", bottleModel.InvoicingId);
            contentValues.put("PrescriptionId", bottleModel.PrescriptionId);
            contentValues.put("DoctorId", StringUtils.getString(bottleModel.DoctorId));
            contentValues.put("DoctorCore", StringUtils.getString(bottleModel.DoctorCore));
            contentValues.put("DoctorName", StringUtils.getString(bottleModel.DoctorName));
            contentValues.put("DiagnoseCore", StringUtils.getString(bottleModel.DiagnoseCore));
            contentValues.put("DiagnoseName", StringUtils.getString(bottleModel.DiagnoseName));
            contentValues.put("PrescribeDate", StringUtils.getString(bottleModel.PrescribeDate));
            contentValues.put("PrescribeTime", StringUtils.getString(bottleModel.PrescribeTime));
            contentValues.put("GroupId", StringUtils.getString(bottleModel.GroupId));
            contentValues.put("BottleStatus", bottleModel.BottleStatus);
            contentValues.put("Way", StringUtils.getString(bottleModel.Way));
            contentValues.put("Frequency", StringUtils.getString(bottleModel.Frequency));
            contentValues.put("TransfusionBulk", bottleModel.TransfusionBulk);
            contentValues.put("TransfusionSpeed", StringUtils.getString(bottleModel.TransfusionSpeed));
            contentValues.put("ExpectTime", bottleModel.ExpectTime);
            contentValues.put("SubscribeDate", StringUtils.getString(bottleModel.SubscribeDate));
            contentValues.put("SubscribeTime", StringUtils.getString(bottleModel.SubscribeTime));
            contentValues.put("RegistrationDate", StringUtils.getString(bottleModel.RegistrationDate));
            contentValues.put("RegistrationTime", StringUtils.getString(bottleModel.RegistrationTime));
            contentValues.put("RegistrationId", bottleModel.RegistrationId);
            contentValues.put("RegistrationCore", StringUtils.getString(bottleModel.RegistrationCore));
            contentValues.put("PillDate", StringUtils.getString(bottleModel.PillDate));
            contentValues.put("PillTime", StringUtils.getString(bottleModel.PillTime));
            contentValues.put("PillId", bottleModel.PillId);
            contentValues.put("PillCore", StringUtils.getString(bottleModel.PillCore));
            contentValues.put("LiquorDate", StringUtils.getString(bottleModel.LiquorDate));
            contentValues.put("LiquorTime", StringUtils.getString(bottleModel.LiquorTime));
            contentValues.put("LiquorId", bottleModel.LiquorId);
            contentValues.put("LiquorCore", StringUtils.getString(bottleModel.LiquorCore));
            contentValues.put("InfusionDate", StringUtils.getString(bottleModel.InfusionDate));
            contentValues.put("InfusionTime", StringUtils.getString(bottleModel.InfusionTime));
            contentValues.put("InfusionPeopleId", bottleModel.InfusionPeopleId);
            contentValues.put("InfusionCore", StringUtils.getString(bottleModel.InfusionCore));
            contentValues.put("EndDate", StringUtils.getString(bottleModel.EndDate));
            contentValues.put("EndTime", StringUtils.getString(bottleModel.EndTime));
            contentValues.put("EndId", bottleModel.EndId);
            contentValues.put("EndCore", StringUtils.getString(bottleModel.EndCore));
            contentValues.put("Remark", StringUtils.getString(bottleModel.Remark));
            contentValues.put("SeatNo", StringUtils.getString(bottleModel.SeatNo));
            contentValues.put("SpeedUnit", StringUtils.getString(bottleModel.SpeedUnit));
            contentValues.put("DrugDetails", JacksonHelper.model2String(bottleModel.DrugDetails));
            contentValues.put("PeopleInfo", JacksonHelper.model2String(bottleModel.PeopleInfo));
            contentValues.put("IsUpload", statues);
            contentValues.put("PatId", StringUtils.getString(bottleModel.PatId));
            contentValues.put("AboutPatrols", JacksonHelper.model2String(bottleModel.AboutPatrols));
            contentValues.put("PillName", StringUtils.getString(bottleModel.PillName));
            contentValues.put("LiquorName", StringUtils.getString(bottleModel.LiquorName));
            contentValues.put("InfusionName", StringUtils.getString(bottleModel.InfusionName));
            contentValues.put("EndName", StringUtils.getString(bottleModel.EndName));
            contentValues.put("LZZ", StringUtils.getString(bottleModel.LZZ));
            contentValues.put("GCF", StringUtils.getString(bottleModel.GCF));
            contentValues.put("CheckCore", StringUtils.getString(bottleModel.CheckCore));
            contentValues.put("CheckName", StringUtils.getString(bottleModel.CheckName));
            contentValues.put("CheckDate", StringUtils.getString(bottleModel.CheckDate));
            contentValues.put("CheckTime", StringUtils.getString(bottleModel.CheckTime));

            flag = sqlDB.update(TableConstant.TABLE_INFUSION, contentValues,
                    "BottleId" + "=" + bottleModel.BottleId, null);
        }
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
        Log.d(TAG, "保存护理任务数据到本地数据库 完成，来源为" + statues);
        return flag;
    }

    /**
     * 删除表中所有的数据
     *
     * @return
     */
    public int deleteAllInfo() {
        return sqlDB.delete(TableConstant.TABLE_INFUSION, null, null);
    }

    /**
     * 根据瓶贴id删除
     *
     * @param BottleId
     */
    public void deleteBottleById(String BottleId) {
        sqlDB.delete(TableConstant.TABLE_INFUSION, "BottleId = ?", new String[]{BottleId});
    }

    // TODO 查询未上传的数据
    public ArrayList<BottleModel> getBottleListAndUnUPload() {
      /*  String sql = "select *  from " + TableConstant.TABLE_INFUSION + " where " +
                "IsUpload= '"+ Constant.UN_UPLOAD + "'";*/

        String sql = "select *  from " + TableConstant.TABLE_INFUSION +
                " a  where a.BottleId not in (select BottleId from " + TableConstant.TABLE_INFUSION_UNPUT + ") and " +
                "IsUpload= '" + Constant.UN_UPLOAD + "'";
        return getInfusionDeatailBySql(sql);
    }

    // TODO 查询已上传的数据
    public ArrayList<BottleModel> getBottleListAndUPload() {
        String sql = "select *  from " + TableConstant.TABLE_INFUSION + " where " +
                "IsUpload= '" + Constant.UPLOADED + "'";
        return getInfusionDeatailBySql(sql);
    }

    public int getUnUploadCount() {
        String sql = "select *  from " + TableConstant.TABLE_INFUSION +
                " a  where a.BottleId not in (select BottleId from " + TableConstant.TABLE_INFUSION_UNPUT + ") and " +
                "IsUpload= '" + Constant.UN_UPLOAD + "'";
        //        Cursor cursor = sqlDB.query(TableConstant.TABLE_INFUSION ,null,"IsUpload=?",new String[]{Constant.UN_UPLOAD+""},null,null,null,null);
        Cursor cursor = sqlDB.rawQuery(sql, null);
        return cursor.getCount();
    }

    /**
     * TODO 获得瓶贴集合
     *
     * @return
     */
    public ArrayList<BottleModel> getInfusionList() {
        String sql = "select *  from " + TableConstant.TABLE_INFUSION;
        return getInfusionDeatailBySql(sql);
    }

    public ArrayList<BottleModel> getInfusionDeatailBySql(String sql) {
        ArrayList<BottleModel> bottleList = new ArrayList<BottleModel>();
        Cursor cursor = sqlDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String BottleId = cursor.getString(cursor.getColumnIndex("BottleId"));
            String InfusionId = cursor.getString(cursor.getColumnIndex("InfusionId"));
            int InfusionNo = cursor.getInt(cursor.getColumnIndex("InfusionNo"));
            int InvoicingId = cursor.getInt(cursor.getColumnIndex("InvoicingId"));
            int PrescriptionId = cursor.getInt(cursor.getColumnIndex("PrescriptionId"));
            String DoctorId = cursor.getString(cursor.getColumnIndex("DoctorId"));
            String DoctorCore = cursor.getString(cursor.getColumnIndex("DoctorCore"));
            String DoctorName = cursor.getString(cursor.getColumnIndex("DoctorName"));
            String DiagnoseCore = cursor.getString(cursor.getColumnIndex("DiagnoseCore"));
            String DiagnoseName = cursor.getString(cursor.getColumnIndex("DiagnoseName"));
            String PrescribeDate = cursor.getString(cursor.getColumnIndex("PrescribeDate"));
            String PrescribeTime = cursor.getString(cursor.getColumnIndex("PrescribeTime"));
            String GroupId = cursor.getString(cursor.getColumnIndex("GroupId"));
            int BottleStatus = cursor.getInt(cursor.getColumnIndex("BottleStatus"));
            String Way = cursor.getString(cursor.getColumnIndex("Way"));
            String Frequency = cursor.getString(cursor.getColumnIndex("Frequency"));
            int TransfusionBulk = cursor.getInt(cursor.getColumnIndex("TransfusionBulk"));
            String TransfusionSpeed = cursor.getString(cursor.getColumnIndex("TransfusionSpeed"));
            int ExpectTime = cursor.getInt(cursor.getColumnIndex("ExpectTime"));
            String SubscribeDate = cursor.getString(cursor.getColumnIndex("SubscribeDate"));
            String SubscribeTime = cursor.getString(cursor.getColumnIndex("SubscribeTime"));
            String RegistrationDate = cursor.getString(cursor.getColumnIndex("RegistrationDate"));
            String RegistrationTime = cursor.getString(cursor.getColumnIndex("RegistrationTime"));
            int RegistrationId = cursor.getInt(cursor.getColumnIndex("RegistrationId"));
            String RegistrationCore = cursor.getString(cursor.getColumnIndex("RegistrationCore"));
            String PillDate = cursor.getString(cursor.getColumnIndex("PillDate"));
            String PillTime = cursor.getString(cursor.getColumnIndex("PillTime"));
            int PillId = cursor.getInt(cursor.getColumnIndex("PillId"));
            String PillCore = cursor.getString(cursor.getColumnIndex("PillCore"));
            String LiquorDate = cursor.getString(cursor.getColumnIndex("LiquorDate"));
            String LiquorTime = cursor.getString(cursor.getColumnIndex("LiquorTime"));
            int LiquorId = cursor.getInt(cursor.getColumnIndex("LiquorId"));
            String LiquorCore = cursor.getString(cursor.getColumnIndex("LiquorCore"));
            String InfusionDate = cursor.getString(cursor.getColumnIndex("InfusionDate"));
            String InfusionTime = cursor.getString(cursor.getColumnIndex("InfusionTime"));
            int InfusionPeopleId = cursor.getInt(cursor.getColumnIndex("InfusionPeopleId"));
            String InfusionCore = cursor.getString(cursor.getColumnIndex("InfusionCore"));
            String EndDate = cursor.getString(cursor.getColumnIndex("EndDate"));
            String EndTime = cursor.getString(cursor.getColumnIndex("EndTime"));
            int EndId = cursor.getInt(cursor.getColumnIndex("EndId"));
            String EndCore = cursor.getString(cursor.getColumnIndex("EndCore"));
            String Remark = cursor.getString(cursor.getColumnIndex("Remark"));
            String SeatNo = cursor.getString(cursor.getColumnIndex("SeatNo"));
            String SpeedUnit = cursor.getString(cursor.getColumnIndex("SpeedUnit"));
            String DrugDetails = cursor.getString(cursor.getColumnIndex("DrugDetails"));
            String PeopleInfo = cursor.getString(cursor.getColumnIndex("PeopleInfo"));
            int IsUpload = cursor.getInt(cursor.getColumnIndex("IsUpload"));
            String PatId = cursor.getString(cursor.getColumnIndex("PatId"));
            String AboutPatrols = cursor.getString(cursor.getColumnIndex("AboutPatrols"));
            String PillName = cursor.getString(cursor.getColumnIndex("PillName"));
            String LiquorName = cursor.getString(cursor.getColumnIndex("LiquorName"));
            String InfusionName = cursor.getString(cursor.getColumnIndex("InfusionName"));
            String EndName = cursor.getString(cursor.getColumnIndex("EndName"));
            String LZZ = cursor.getString(cursor.getColumnIndex("LZZ"));
            String GCF = cursor.getString(cursor.getColumnIndex("GCF"));
            String CheckCore = cursor.getString(cursor.getColumnIndex("CheckCore"));
            String CheckName = cursor.getString(cursor.getColumnIndex("CheckName"));
            String CheckDate = cursor.getString(cursor.getColumnIndex("CheckDate"));
            String CheckTime = cursor.getString(cursor.getColumnIndex("CheckTime"));
            BottleModel bottleModel = new BottleModel();
            bottleModel.BottleId = BottleId;
            bottleModel.InfusionId = InfusionId;
            bottleModel.InfusionNo = InfusionNo;
            bottleModel.InvoicingId = InvoicingId;
            bottleModel.PrescriptionId = PrescriptionId;
            bottleModel.DoctorId = DoctorId;
            bottleModel.DoctorCore = DoctorCore;
            bottleModel.DoctorName = DoctorName;
            bottleModel.DiagnoseCore = DiagnoseCore;
            bottleModel.DiagnoseName = DiagnoseName;
            bottleModel.PrescribeDate = PrescribeDate;
            bottleModel.PrescribeTime = PrescribeTime;
            bottleModel.GroupId = GroupId;
            bottleModel.BottleStatus = BottleStatus;
            bottleModel.Way = Way;
            bottleModel.Frequency = Frequency;
            bottleModel.TransfusionBulk = TransfusionBulk;
            bottleModel.TransfusionSpeed = TransfusionSpeed;
            bottleModel.ExpectTime = ExpectTime;
            bottleModel.SubscribeDate = SubscribeDate;
            bottleModel.SubscribeTime = SubscribeTime;
            bottleModel.RegistrationDate = RegistrationDate;
            bottleModel.RegistrationTime = RegistrationTime;
            bottleModel.RegistrationId = RegistrationId;
            bottleModel.RegistrationCore = RegistrationCore;
            bottleModel.PillDate = PillDate;
            bottleModel.PillTime = PillTime;
            bottleModel.PillId = PillId;
            bottleModel.PillCore = PillCore;
            bottleModel.LiquorDate = LiquorDate;
            bottleModel.LiquorTime = LiquorTime;
            bottleModel.LiquorId = LiquorId;
            bottleModel.LiquorCore = LiquorCore;
            bottleModel.InfusionDate = InfusionDate;
            bottleModel.InfusionTime = InfusionTime;
            bottleModel.InfusionPeopleId = InfusionPeopleId;
            bottleModel.InfusionCore = InfusionCore;
            bottleModel.EndDate = EndDate;
            bottleModel.EndTime = EndTime;
            bottleModel.EndId = EndId;
            bottleModel.EndCore = EndCore;
            bottleModel.Remark = Remark;
            bottleModel.SeatNo = SeatNo;
            bottleModel.SpeedUnit = SpeedUnit;
            if (!StringUtils.StringIsEmpty(DrugDetails)) {
                bottleModel.DrugDetails = JacksonHelper.getObjects(DrugDetails, new TypeReference<List<DrugDetailModel>>() {});

            }
            bottleModel.PeopleInfo = String2InfusionModel.string2QueueModel(PeopleInfo);

            bottleModel.IsUpload = IsUpload;
            bottleModel.PatId = PatId;
            bottleModel.AboutPatrols = JacksonHelper.getObjects(AboutPatrols, new TypeReference<List<PatrolModel>>() {});

            bottleModel.PillName = PillName;
            bottleModel.LiquorName = LiquorName;
            bottleModel.InfusionName = InfusionName;
            bottleModel.EndName = EndName;
            bottleModel.LZZ = LZZ;
            bottleModel.GCF = GCF;
            bottleModel.CheckCore = CheckCore;
            bottleModel.CheckName = CheckName;
            bottleModel.CheckDate = CheckDate;
            bottleModel.CheckTime = CheckTime;
            bottleList.add(bottleModel);

        }
        cursor.close();
        return bottleList;
    }

    /**
     * 构建contentvalues未上传
     */
    private ContentValues getUpdateContentValues(BottleModel bottle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BottleColumns.BottleId, bottle.BottleId);
        contentValues.put(BottleColumns.InfusionId, bottle.InfusionId);
        contentValues.put(BottleColumns.InfusionNo, bottle.InfusionNo);
        contentValues.put(BottleColumns.InvoicingId, bottle.InvoicingId);
        contentValues.put(BottleColumns.PrescriptionId, bottle.PrescriptionId);
        contentValues.put(BottleColumns.DoctorId, StringUtils.getString(bottle.DoctorId));
        contentValues.put(BottleColumns.DoctorCore, StringUtils.getString(bottle.DoctorCore));
        contentValues.put(BottleColumns.DoctorName, StringUtils.getString(bottle.DoctorName));
        contentValues.put(BottleColumns.DiagnoseCore, StringUtils.getString(bottle.DiagnoseCore));
        contentValues.put(BottleColumns.DiagnoseName, StringUtils.getString(bottle.DiagnoseName));
        contentValues.put(BottleColumns.PrescribeDate, StringUtils.getString(bottle.PrescribeDate));
        contentValues.put(BottleColumns.PrescribeTime, StringUtils.getString(bottle.PrescribeTime));
        contentValues.put(BottleColumns.GroupId, bottle.GroupId);
        contentValues.put(BottleColumns.BottleStatus, bottle.BottleStatus);
        contentValues.put(BottleColumns.Way, StringUtils.getString(bottle.Way));
        contentValues.put(BottleColumns.Frequency, StringUtils.getString(bottle.Frequency));
        contentValues.put(BottleColumns.TransfusionBulk, bottle.TransfusionBulk);
        contentValues.put(BottleColumns.TransfusionSpeed, bottle.TransfusionSpeed);
        contentValues.put(BottleColumns.ExpectTime, bottle.ExpectTime);
        contentValues.put(BottleColumns.SubscribeDate, StringUtils.getString(bottle.SubscribeDate));
        contentValues.put(BottleColumns.SubscribeTime, StringUtils.getString(bottle.SubscribeTime));
        contentValues.put(BottleColumns.RegistrationDate, StringUtils.getString(bottle.RegistrationDate));
        contentValues.put(BottleColumns.RegistrationTime, StringUtils.getString(bottle.RegistrationTime));
        contentValues.put(BottleColumns.RegistrationId, bottle.RegistrationId);
        contentValues.put(BottleColumns.RegistrationCore, StringUtils.getString(bottle.RegistrationCore));
        contentValues.put(BottleColumns.PillDate, StringUtils.getString(bottle.PillDate));
        contentValues.put(BottleColumns.PillTime, StringUtils.getString(bottle.PillTime));
        contentValues.put(BottleColumns.PillId, bottle.PillId);
        contentValues.put(BottleColumns.PillCore, StringUtils.getString(bottle.PillCore));
        contentValues.put(BottleColumns.LiquorDate, StringUtils.getString(bottle.LiquorDate));
        contentValues.put(BottleColumns.LiquorTime, StringUtils.getString(bottle.LiquorTime));
        contentValues.put(BottleColumns.LiquorId, bottle.LiquorId);
        contentValues.put(BottleColumns.LiquorCore, StringUtils.getString(bottle.LiquorCore));
        contentValues.put(BottleColumns.InfusionDate, StringUtils.getString(bottle.InfusionDate));
        contentValues.put(BottleColumns.InfusionTime, StringUtils.getString(bottle.InfusionTime));
        contentValues.put(BottleColumns.InfusionPeopleId, bottle.InfusionPeopleId);
        contentValues.put(BottleColumns.InfusionCore, StringUtils.getString(bottle.InfusionCore));
        contentValues.put(BottleColumns.EndDate, StringUtils.getString(bottle.EndDate));
        contentValues.put(BottleColumns.EndTime, StringUtils.getString(bottle.EndTime));
        contentValues.put(BottleColumns.EndId, bottle.EndId);
        contentValues.put(BottleColumns.EndCore, StringUtils.getString(bottle.EndCore));
        contentValues.put(BottleColumns.Remark, StringUtils.getString(bottle.Remark));
        contentValues.put(BottleColumns.SeatNo, StringUtils.getString(bottle.SeatNo));
        contentValues.put(BottleColumns.DrugDetails, StringUtils.getString(JacksonHelper.model2String(bottle.DrugDetails)));
        contentValues.put(BottleColumns.PeopleInfo, StringUtils.getString(JacksonHelper.model2String(bottle.PeopleInfo)));
        contentValues.put("IsUpload", Constant.UN_UPLOAD);
        contentValues.put(BottleColumns.PatId, bottle.PeopleInfo.PatId);
        contentValues.put(BottleColumns.AboutPatrols, StringUtils.getString(JacksonHelper.model2String(bottle.AboutPatrols)));
        contentValues.put(BottleColumns.PillName, StringUtils.getString(bottle.PillName));
        contentValues.put(BottleColumns.LiquorName, StringUtils.getString(bottle.LiquorName));
        contentValues.put(BottleColumns.InfusionName, StringUtils.getString(bottle.InfusionName));
        contentValues.put(BottleColumns.EndName, StringUtils.getString(bottle.EndName));
        contentValues.put(BottleColumns.LZZ, StringUtils.getString(bottle.LZZ));
        contentValues.put(BottleColumns.GCF, StringUtils.getString(bottle.GCF));
        contentValues.put(BottleColumns.CheckCore,StringUtils.getString(bottle.CheckCore));
        contentValues.put(BottleColumns.CheckName,StringUtils.getString(bottle.CheckName));
        contentValues.put(BottleColumns.CheckDate,StringUtils.getString(bottle.CheckDate));
        contentValues.put(BottleColumns.CheckTime,StringUtils.getString(bottle.CheckTime));
        return contentValues;
    }

    /**
     * 构建contentvalues 已上传
     */
    private ContentValues getUpdateContentValuesForPeople(BottleModel bottle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BottleColumns.BottleId, bottle.BottleId);
        contentValues.put(BottleColumns.InfusionId, bottle.InfusionId);
        contentValues.put(BottleColumns.InfusionNo, bottle.InfusionNo);
        contentValues.put(BottleColumns.InvoicingId, bottle.InvoicingId);
        contentValues.put(BottleColumns.PrescriptionId, bottle.PrescriptionId);
        contentValues.put(BottleColumns.DoctorId, StringUtils.getString(bottle.DoctorId));
        contentValues.put(BottleColumns.DoctorCore, StringUtils.getString(bottle.DoctorCore));
        contentValues.put(BottleColumns.DoctorName, StringUtils.getString(bottle.DoctorName));
        contentValues.put(BottleColumns.DiagnoseCore, StringUtils.getString(bottle.DiagnoseCore));
        contentValues.put(BottleColumns.DiagnoseName, StringUtils.getString(bottle.DiagnoseName));
        contentValues.put(BottleColumns.PrescribeDate, StringUtils.getString(bottle.PrescribeDate));
        contentValues.put(BottleColumns.PrescribeTime, StringUtils.getString(bottle.PrescribeTime));
        contentValues.put(BottleColumns.GroupId, bottle.GroupId);
        contentValues.put(BottleColumns.BottleStatus, bottle.BottleStatus);
        contentValues.put(BottleColumns.Way, StringUtils.getString(bottle.Way));
        contentValues.put(BottleColumns.Frequency, StringUtils.getString(bottle.Frequency));
        contentValues.put(BottleColumns.TransfusionBulk, bottle.TransfusionBulk);
        contentValues.put(BottleColumns.TransfusionSpeed, bottle.TransfusionSpeed);
        contentValues.put(BottleColumns.ExpectTime, bottle.ExpectTime);
        contentValues.put(BottleColumns.SubscribeDate, StringUtils.getString(bottle.SubscribeDate));
        contentValues.put(BottleColumns.SubscribeTime, StringUtils.getString(bottle.SubscribeTime));
        contentValues.put(BottleColumns.RegistrationDate, StringUtils.getString(bottle.RegistrationDate));
        contentValues.put(BottleColumns.RegistrationTime, StringUtils.getString(bottle.RegistrationTime));
        contentValues.put(BottleColumns.RegistrationId, bottle.RegistrationId);
        contentValues.put(BottleColumns.RegistrationCore, StringUtils.getString(bottle.RegistrationCore));
        contentValues.put(BottleColumns.PillDate, StringUtils.getString(bottle.PillDate));
        contentValues.put(BottleColumns.PillTime, StringUtils.getString(bottle.PillTime));
        contentValues.put(BottleColumns.PillId, bottle.PillId);
        contentValues.put(BottleColumns.PillCore, StringUtils.getString(bottle.PillCore));
        contentValues.put(BottleColumns.LiquorDate, StringUtils.getString(bottle.LiquorDate));
        contentValues.put(BottleColumns.LiquorTime, StringUtils.getString(bottle.LiquorTime));
        contentValues.put(BottleColumns.LiquorId, bottle.LiquorId);
        contentValues.put(BottleColumns.LiquorCore, StringUtils.getString(bottle.LiquorCore));
        contentValues.put(BottleColumns.InfusionDate, StringUtils.getString(bottle.InfusionDate));
        contentValues.put(BottleColumns.InfusionTime, StringUtils.getString(bottle.InfusionTime));
        contentValues.put(BottleColumns.InfusionPeopleId, bottle.InfusionPeopleId);
        contentValues.put(BottleColumns.InfusionCore, StringUtils.getString(bottle.InfusionCore));
        contentValues.put(BottleColumns.EndDate, StringUtils.getString(bottle.EndDate));
        contentValues.put(BottleColumns.EndTime, StringUtils.getString(bottle.EndTime));
        contentValues.put(BottleColumns.EndId, bottle.EndId);
        contentValues.put(BottleColumns.EndCore, StringUtils.getString(bottle.EndCore));
        contentValues.put(BottleColumns.Remark, StringUtils.getString(bottle.Remark));
        contentValues.put(BottleColumns.SeatNo, StringUtils.getString(bottle.SeatNo));
        contentValues.put(BottleColumns.DrugDetails, StringUtils.getString(JacksonHelper.model2String(bottle.DrugDetails)));
        contentValues.put(BottleColumns.PeopleInfo, StringUtils.getString(JacksonHelper.model2String(bottle.PeopleInfo)));
        contentValues.put("IsUpload", Constant.UPLOADED);
        contentValues.put(BottleColumns.PatId, bottle.PeopleInfo.PatId);
        contentValues.put(BottleColumns.AboutPatrols, StringUtils.getString(JacksonHelper.model2String(bottle.AboutPatrols)));
        contentValues.put(BottleColumns.PillName, StringUtils.getString(bottle.PillName));
        contentValues.put(BottleColumns.LiquorName, StringUtils.getString(bottle.LiquorName));
        contentValues.put(BottleColumns.InfusionName, StringUtils.getString(bottle.InfusionName));
        contentValues.put(BottleColumns.EndName, StringUtils.getString(bottle.EndName));
        contentValues.put(BottleColumns.LZZ, StringUtils.getString(bottle.LZZ));
        contentValues.put(BottleColumns.GCF, StringUtils.getString(bottle.GCF));
        contentValues.put(BottleColumns.CheckCore,StringUtils.getString(bottle.CheckCore));
        contentValues.put(BottleColumns.CheckName,StringUtils.getString(bottle.CheckName));
        contentValues.put(BottleColumns.CheckDate,StringUtils.getString(bottle.CheckDate));
        contentValues.put(BottleColumns.CheckTime,StringUtils.getString(bottle.CheckTime));
        return contentValues;
    }


    /**
     * 构建contentvalues
     */
    private ContentValues getIsUpdateContentValues(BottleModel bottle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BottleColumns.BottleId, bottle.BottleId);
        contentValues.put(BottleColumns.InfusionId, bottle.InfusionId);
        contentValues.put(BottleColumns.InfusionNo, bottle.InfusionNo);
        contentValues.put(BottleColumns.InvoicingId, bottle.InvoicingId);
        contentValues.put(BottleColumns.PrescriptionId, bottle.PrescriptionId);
        contentValues.put(BottleColumns.DoctorId, StringUtils.getString(bottle.DoctorId));
        contentValues.put(BottleColumns.DoctorCore, StringUtils.getString(bottle.DoctorCore));
        contentValues.put(BottleColumns.DoctorName, StringUtils.getString(bottle.DoctorName));
        contentValues.put(BottleColumns.DiagnoseCore, StringUtils.getString(bottle.DiagnoseCore));
        contentValues.put(BottleColumns.DiagnoseName, StringUtils.getString(bottle.DiagnoseName));
        contentValues.put(BottleColumns.PrescribeDate, StringUtils.getString(bottle.PrescribeDate));
        contentValues.put(BottleColumns.PrescribeTime, StringUtils.getString(bottle.PrescribeTime));
        contentValues.put(BottleColumns.GroupId, bottle.GroupId);
        contentValues.put(BottleColumns.BottleStatus, bottle.BottleStatus);
        contentValues.put(BottleColumns.Way, StringUtils.getString(bottle.Way));
        contentValues.put(BottleColumns.Frequency, StringUtils.getString(bottle.Frequency));
        contentValues.put(BottleColumns.TransfusionBulk, bottle.TransfusionBulk);
        contentValues.put(BottleColumns.TransfusionSpeed, bottle.TransfusionSpeed);
        contentValues.put(BottleColumns.ExpectTime, bottle.ExpectTime);
        contentValues.put(BottleColumns.SubscribeDate, StringUtils.getString(bottle.SubscribeDate));
        contentValues.put(BottleColumns.SubscribeTime, StringUtils.getString(bottle.SubscribeTime));
        contentValues.put(BottleColumns.RegistrationDate, StringUtils.getString(bottle.RegistrationDate));
        contentValues.put(BottleColumns.RegistrationTime, StringUtils.getString(bottle.RegistrationTime));
        contentValues.put(BottleColumns.RegistrationId, bottle.RegistrationId);
        contentValues.put(BottleColumns.RegistrationCore, StringUtils.getString(bottle.RegistrationCore));
        contentValues.put(BottleColumns.PillDate, StringUtils.getString(bottle.PillDate));
        contentValues.put(BottleColumns.PillTime,StringUtils.getString(bottle.PillTime));
        contentValues.put(BottleColumns.PillId, bottle.PillId);
        contentValues.put(BottleColumns.PillCore, StringUtils.getString(bottle.PillCore));
        contentValues.put(BottleColumns.LiquorDate, StringUtils.getString(bottle.LiquorDate));
        contentValues.put(BottleColumns.LiquorTime, StringUtils.getString(bottle.LiquorTime));
        contentValues.put(BottleColumns.LiquorId, bottle.LiquorId);
        contentValues.put(BottleColumns.LiquorCore, StringUtils.getString(bottle.LiquorCore));
        contentValues.put(BottleColumns.InfusionDate, StringUtils.getString(bottle.InfusionDate));
        contentValues.put(BottleColumns.InfusionTime, StringUtils.getString(bottle.InfusionTime));
        contentValues.put(BottleColumns.InfusionPeopleId, bottle.InfusionPeopleId);
        contentValues.put(BottleColumns.InfusionCore, StringUtils.getString(bottle.InfusionCore));
        contentValues.put(BottleColumns.InfusionName, StringUtils.getString(bottle.InfusionName));
        contentValues.put(BottleColumns.EndDate, StringUtils.getString(bottle.EndDate));
        contentValues.put(BottleColumns.EndTime, StringUtils.getString(bottle.EndTime));
        contentValues.put(BottleColumns.EndId, bottle.EndId);
        contentValues.put(BottleColumns.EndCore, StringUtils.getString(bottle.EndCore));
        contentValues.put(BottleColumns.Remark, StringUtils.getString(bottle.Remark));
        contentValues.put(BottleColumns.SeatNo, StringUtils.getString(bottle.SeatNo));
        contentValues.put(BottleColumns.DrugDetails, StringUtils.getString(JacksonHelper.model2String(bottle.DrugDetails)));
        contentValues.put(BottleColumns.PeopleInfo, StringUtils.getString(JacksonHelper.model2String(bottle.PeopleInfo)));
        contentValues.put("IsUpload",Constant.UPLOADED);
        contentValues.put(BottleColumns.PatId, bottle.PeopleInfo.PatId);
        contentValues.put(BottleColumns.AboutPatrols, StringUtils.getString(JacksonHelper.model2String(bottle.AboutPatrols)));
        contentValues.put(BottleColumns.LZZ, StringUtils.getString(bottle.LZZ));
        contentValues.put(BottleColumns.GCF, StringUtils.getString(bottle.GCF));
        contentValues.put(BottleColumns.CheckCore,StringUtils.getString(bottle.CheckCore));
        contentValues.put(BottleColumns.CheckName,StringUtils.getString(bottle.CheckName));
        contentValues.put(BottleColumns.CheckDate,StringUtils.getString(bottle.CheckDate));
        contentValues.put(BottleColumns.CheckTime,StringUtils.getString(bottle.CheckTime));
        return contentValues;
    }

    public void updateBottleIsUpload(ArrayList<BottleModel> bottleModels){
        sqlDB.beginTransaction(); // 手动设置开始事务
        for(BottleModel bottleModel : bottleModels){
            ContentValues contentValues = getIsUpdateContentValues(bottleModel);
            sqlDB.update(TableConstant.TABLE_INFUSION, contentValues,
                    "BottleId" +"=" + bottleModel.BottleId, null);
        }
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();

    }
}
