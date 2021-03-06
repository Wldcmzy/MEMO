package com.example.memorandum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.memorandum.MemoSQLiteOpenHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private FloatingActionButton buttonAction, buttonSave;
    private MemoAdapter adapter;
    private Intent intent;
    private MemoSQLiteOpenHelper sqliteHelper;
    private SQLiteDatabase database;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryAll();
    }

    private void initView(){
        listView = findViewById(R.id.list_view);
        buttonAction = findViewById(R.id.sljq);
        buttonAction.setOnClickListener(this);
        buttonSave = findViewById(R.id.xjj);
        buttonSave.setOnClickListener(this);
        sqliteHelper = new MemoSQLiteOpenHelper(this);
        database = sqliteHelper.getReadableDatabase();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sljq:
                intent = new Intent(MainActivity.this, ActionActivity.class);
                intent.putExtra("action", 1);
                startActivity(intent);
                break;
            case R.id.xjj:
                intent = new Intent(MainActivity.this, CloudActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //??????????????????????????????????????????
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //????????????????????????????????????
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //??????2000ms??????????????????????????????Toast????????????
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
                //???????????????????????????????????????????????????????????????????????????
                mExitTime = System.currentTimeMillis();
            } else {
                //??????2000ms??????????????????????????????????????????-??????System.exit()??????????????????
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void queryAll(){
        Cursor cursor = database.query(sqliteHelper.tableName, null,null,null,null,null, sqliteHelper.lastModifyTime);
        adapter = new MemoAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, OneMemoActivity.class);
                intent.putExtra("i", position);
                Cursor cursor = database.query(sqliteHelper.tableName, null,null,null,null,null, sqliteHelper.id);
                cursor.moveToPosition(position);

                int tmp;
                tmp = cursor.getColumnIndex(sqliteHelper.id);
                intent.putExtra(sqliteHelper.id, cursor.getString(tmp));
                tmp = cursor.getColumnIndex(sqliteHelper.datas);
                intent.putExtra(sqliteHelper.datas, cursor.getString(tmp));
                tmp = cursor.getColumnIndex(sqliteHelper.lastModifyTime);
                intent.putExtra(sqliteHelper.lastModifyTime, cursor.getString(tmp));
                tmp = cursor.getColumnIndex(sqliteHelper.title);
                intent.putExtra(sqliteHelper.title, cursor.getString((tmp)));
                startActivity(intent);
            }
        });
    }
}