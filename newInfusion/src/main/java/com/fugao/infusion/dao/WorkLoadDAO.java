package com.fugao.infusion.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fugao.infusion.constant.TableConstant;
import com.fugao.infusion.model.WorkloadModel;
import com.fugao.infusion.utils.DateUtils;
import com.jasonchen.base.utils.StringUtils;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.dao.WorkLoadDAO
 * @Description: TODO 工作量的DAO
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/12/29 15:43
 * @version: V1.0
 */

public class WorkLoadDAO {
    private static final String TAG = "Fugao-WorkLoadDAO";

    public DataBaseInfo dataBaseInfo;
    public SQLiteDatabase sqlDB;
    public static String planExecuteDate = "";

    public WorkLoadDAO(DataBaseInfo dataBaseInfoCurrent) {
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

    /***
     * 插入工作量
     */
    public void insertWorlLoad(WorkloadModel workloadModel){
        ContentValues contentValues = getContentValues(workloadModel);
        sqlDB.beginTransaction();
        sqlDB.insert(TableConstant.TABLE_WORKlOAD, null, contentValues);
        sqlDB.setTransactionSuccessful();
        sqlDB.endTransaction();
    }

    /**
     * 查询指定工作量
     */
    public WorkloadModel query(String catalog) {
        WorkloadModel workloadModel = null;
        String sql = "select *from "+ TableConstant.TABLE_WORKlOAD + " where Catalog='"+ catalog+"'";
        Cursor cursor = sqlDB.rawQuery(sql, null);
        workloadModel = fromCursor(cursor);
        cursor.close();
        return workloadModel;
    }

    /**
     * 删除所有工作量
     */
    public int deleteAllInfo() {
        return sqlDB.delete(TableConstant.TABLE_WORKlOAD, null, null);
    }

    /**
     * 构建contentvalues
     */
    private ContentValues getContentValues(WorkloadModel workloadModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorkloadColumns.Date, StringUtils.getString(workloadModel.Date));
        contentValues.put(WorkloadColumns.PeopleId, StringUtils.getString(workloadModel.PeopleId));
        contentValues.put(WorkloadColumns.DjCount, workloadModel.DjCount);
        contentValues.put(WorkloadColumns.DoseCount, workloadModel.DoseCount);
        contentValues.put(WorkloadColumns.PaiyaoCount, workloadModel.PaiYaoCount);
        contentValues.put(WorkloadColumns.InfusionCount, workloadModel.InfusionCount);
        contentValues.put(WorkloadColumns.PatrolCount, workloadModel.PatrolCount);
        contentValues.put(WorkloadColumns.PunctureCount, workloadModel.PunctureCount);
        contentValues.put(WorkloadColumns.CallOver, workloadModel.CallOver);
        contentValues.put(WorkloadColumns.DoneCount, workloadModel.DoneCount);
        contentValues.put(WorkloadColumns.InvalidOver, workloadModel.InvalidOver);
        contentValues.put(WorkloadColumns.SiginInTime, StringUtils.getString(workloadModel.SiginInTime));
        contentValues.put(WorkloadColumns.SiginOutTime, StringUtils.getString(workloadModel.SiginOutTime));
        contentValues.put(WorkloadColumns.Catalog, StringUtils.getString(workloadModel.Catalog));
        return contentValues;
    }

    /**
     * 查询model
     * @param cursor
     * @return
     */
    public static WorkloadModel fromCursor(Cursor cursor) {
        WorkloadModel workloadModel = new WorkloadModel();
        while (cursor.moveToNext()) {
        workloadModel.Date = cursor.getString(cursor.getColumnIndex(WorkloadColumns.Date));
        workloadModel.PeopleId = cursor.getString(cursor.getColumnIndex(WorkloadColumns.PeopleId));
        workloadModel.DjCount = cursor.getInt(cursor.getColumnIndex(WorkloadColumns.DjCount));
        workloadModel.DoseCount = cursor.getInt(cursor.getColumnIndex(WorkloadColumns.DoseCount));
        workloadModel.PaiYaoCount = cursor.getInt(cursor.getColumnIndex(WorkloadColumns.PaiyaoCount));
        workloadModel.InfusionCount =
                cursor.getInt(cursor.getColumnIndex(WorkloadColumns.InfusionCount));
        workloadModel.PatrolCount = cursor.getInt(cursor.getColumnIndex(WorkloadColumns.PatrolCount));
        workloadModel.PunctureCount =
                cursor.getInt(cursor.getColumnIndex(WorkloadColumns.PunctureCount));
        workloadModel.CallOver = cursor.getInt(cursor.getColumnIndex(WorkloadColumns.CallOver));
        workloadModel.DoneCount = cursor.getInt(cursor.getColumnIndex(WorkloadColumns.DoneCount));
        workloadModel.InvalidOver = cursor.getInt(cursor.getColumnIndex(WorkloadColumns.InvalidOver));
        workloadModel.SiginInTime =
                cursor.getString(cursor.getColumnIndex(WorkloadColumns.SiginInTime));
        workloadModel.SiginOutTime =
                cursor.getString(cursor.getColumnIndex(WorkloadColumns.SiginOutTime));
        workloadModel.Catalog = cursor.getString(cursor.getColumnIndex(WorkloadColumns.Catalog));
        }
        return workloadModel;
    }

}
