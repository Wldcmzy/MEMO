package com.example.memorandum;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DeleteService extends Service {
    private MemoSQLiteOpenHelper sqliteHelper;
    private SQLiteDatabase database;

    public DeleteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sqliteHelper = new MemoSQLiteOpenHelper(this);
        database = sqliteHelper.getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        deleteByPos(intent.getStringExtra("pos"));
        Log.i("34234234", "delete>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    private void deleteByPos(String pos) {
        database.delete(sqliteHelper.tableName, sqliteHelper.id + "=" + pos, null);
    }
}