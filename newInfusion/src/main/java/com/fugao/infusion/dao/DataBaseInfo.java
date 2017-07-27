package com.fugao.infusion.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.fugao.infusion.constant.TableConstant;

/**
 * 基本数据库方案 1.单例实现数据库打开方式
 *
 * @author findchen TODO 2013-6-17上午10:34:45
 */
public class DataBaseInfo {

    private String DB_NAME = "fugaoinfusion.db";

    private int DB_VERSION = 9;

    private SQLiteDatabase sqlDB;
    public static DataBaseInfo dataBaseInfoCurrent;

    /**
     * 初始化数据库
     *
     * @param context
     */
    private DataBaseInfo(Context context) {
        if (sqlDB == null || (!sqlDB.isOpen())) {
            sqlDB = new DataBaseHelper(context).getWritableDatabase();
        }

    }

    public static DataBaseInfo getInstance(Context context) {
        if (dataBaseInfoCurrent == null) {
            dataBaseInfoCurrent = new DataBaseInfo(context);
        }
        return dataBaseInfoCurrent;
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (sqlDB != null) {
            sqlDB.close();
            sqlDB = null;
        }
        if (dataBaseInfoCurrent != null) {
            dataBaseInfoCurrent = null;
        }
    }

    public SQLiteDatabase getSqlDB() {
        return sqlDB;
    }

    public void setSqlDB(SQLiteDatabase sqlDB) {
        this.sqlDB = sqlDB;
    }

    /**
     * 查询表是否为空
     *
     * @param tablename tableAdviceCategory
     * @return
     */
    public boolean tableIsEmpty(String tablename) {
        // 返回true为空，
        boolean flag = false;
        Cursor cursor = sqlDB.query(tablename, null, null, null, null, null,
                null);
        int x = cursor.getCount();
        cursor.close();
        if (x == 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 数据库帮助类 表的创建 和数据库的更新，
     *
     * @author chenliang
     * @date 2012-12-24
     */
    public class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DataBaseHelper(Context context) {
            this(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            /***
             * 瓶贴表
             */
            String taskSql = "CREATE TABLE " + TableConstant.TABLE_INFUSION
                    + " (BottleId varchar(30)  PRIMARY KEY NOT NULL," + "InfusionId TEXT,"
                    + "InfusionNo int," + "InvoicingId int,"
                    + "PrescriptionId int," + "DoctorId TEXT,"
                    + "DoctorCore TEXT," + "DoctorName TEXT,"
                    + "DiagnoseCore TEXT," + "DiagnoseName TEXT,"
                    + "PrescribeDate TEXT," + "PrescribeTime TEXT,"
                    + "GroupId TEXT," + "BottleStatus int,"
                    + "Way TEXT," + "Frequency TEXT,"
                    + "TransfusionBulk int," + "TransfusionSpeed TEXT,"
                    + "ExpectTime int," + "SubscribeDate TEXT,"
                    + "SubscribeTime TEXT," + "RegistrationDate TEXT,"
                    + "RegistrationTime TEXT," + "RegistrationId int,"
                    + "RegistrationCore TEXT," + "PillDate TEXT,"
                    + "PillTime TEXT," + "PillId int,"
                    + "PillCore TEXT," + "LiquorDate TEXT,"
                    + "LiquorTime TEXT," + "LiquorId int,"
                    + "LiquorCore TEXT," + "InfusionDate TEXT,"
                    + "InfusionTime TEXT," + "InfusionPeopleId int,"
                    + "InfusionCore TEXT," + "EndDate TEXT,"
                    + "EndTime TEXT," + "EndId int,"
                    + "EndCore TEXT," + "Remark TEXT,"
                    + "SeatNo TEXT," + "SpeedUnit TEXT,"
                    + "DrugDetails TEXT," + "PeopleInfo TEXT,"
                    + "IsUpload int," + "PatId TEXT,"
                    + "AboutPatrols TEXT,"
                    + "PillName TEXT," +"LiquorName TEXT,"
                    + "InfusionName TEXT," +"EndName TEXT,"
                    + "LZZ TEXT," + "GCF TEXT,"+"CheckTime TEXT,"
                    +"CheckDate TEXT,"+"CheckName TEXT,"+"CheckCore TEXT)"
                    ;
            db.execSQL(taskSql);

            /**
             * 工作量
             */
            String workLoadSql = "CREATE TABLE " + TableConstant.TABLE_WORKlOAD
                    + " (Date varchar(30),"
                    + "PeopleId varchar(50)," +"DjCount int,"
                    + "DoseCount int ," +"PaiyaoCount int,"
                    + "InfusionCount int ," +"PatrolCount int,"
                    + "PunctureCount int ," +"CallOver int,"
                    + "DoneCount int ," +"InvalidOver int,"
                    + "SiginInTime varchar(50)," +"SiginOutTime varchar(50),"
                    + "Catalog varchar(30))"
                    ;
            db.execSQL(workLoadSql);
          /*  //未上传数据表
            String unTaskSql = "CREATE TABLE " + TableConstant.TABLE_INFUSION_UNPUT
                    + " (QueueNo varchar(30) PRIMARY KEY NOT NULL," + "PutDate varchar(30),"
                    + "BottleId varchar(30))";
            db.execSQL(unTaskSql);*/
            String unTaskSql = "CREATE TABLE " + TableConstant.TABLE_INFUSION_UPLOAD
                    + " (BottleId varchar(30)  PRIMARY KEY NOT NULL," + "InfusionId TEXT,"
                    + "InfusionNo int," + "InvoicingId int,"
                    + "PrescriptionId int," + "DoctorId TEXT,"
                    + "DoctorCore TEXT," + "DoctorName TEXT,"
                    + "DiagnoseCore TEXT," + "DiagnoseName TEXT,"
                    + "PrescribeDate TEXT," + "PrescribeTime TEXT,"
                    + "GroupId TEXT," + "BottleStatus int,"
                    + "Way TEXT," + "Frequency TEXT,"
                    + "TransfusionBulk int," + "TransfusionSpeed TEXT,"
                    + "ExpectTime int," + "SubscribeDate TEXT,"
                    + "SubscribeTime TEXT," + "RegistrationDate TEXT,"
                    + "RegistrationTime TEXT," + "RegistrationId int,"
                    + "RegistrationCore TEXT," + "PillDate TEXT,"
                    + "PillTime TEXT," + "PillId int,"
                    + "PillCore TEXT," + "LiquorDate TEXT,"
                    + "LiquorTime TEXT," + "LiquorId int,"
                    + "LiquorCore TEXT," + "InfusionDate TEXT,"
                    + "InfusionTime TEXT," + "InfusionPeopleId int,"
                    + "InfusionCore TEXT," + "EndDate TEXT,"
                    + "EndTime TEXT," + "EndId int,"
                    + "EndCore TEXT," + "Remark TEXT,"
                    + "SeatNo TEXT," + "SpeedUnit TEXT,"
                    + "DrugDetails TEXT," + "PeopleInfo TEXT,"
                    + "IsUpload int," + "PatId TEXT,"
                    + "AboutPatrols TEXT,"
                    + "PillName TEXT," +"LiquorName TEXT,"
                    + "InfusionName TEXT," +"EndName TEXT,"
                    + "CheckTime TEXT,"+"CheckDate TEXT,"
                    +  "CheckName TEXT,"+"CheckCore TEXT,"
                    + "LZZ TEXT," + "GCF TEXT)"
                    ;
            db.execSQL(unTaskSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            if (newVersion > oldVersion) {
                // TODO 更新数据库版本的时候 要保存以前的数据的操作
                db.execSQL("drop table if exists " + TableConstant.TABLE_INFUSION);
                db.execSQL("drop table if exists " + TableConstant.TABLE_WORKlOAD);
                db.execSQL("drop table if exists " + TableConstant.TABLE_INFUSION_UPLOAD);

            }
            onCreate(db);
        }

    }

}
