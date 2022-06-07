package com.example.memorandum;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoSQLiteOpenHelper extends SQLiteOpenHelper {
    public final static int databaseVersion = 1;
    public final static String databaseName = "memotest2.db";  //数据库名字
    public final static String tableName = "memo2";
    public final static String datas = "datas"; //内容
    public final static String id = "id"; //id
    public final static String createTime = "createTime"; //时间
    public final static String lastModifyTime = "lastModifyTime";


    public MemoSQLiteOpenHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + tableName + " ( "
                        + id + " INTEGER PRIMARY KEY AUTOINCREMENT"
                        + ", " + datas + " text"
                        + ", " + createTime + " text"
                        + ", " + lastModifyTime + " text"
                        + " ) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}