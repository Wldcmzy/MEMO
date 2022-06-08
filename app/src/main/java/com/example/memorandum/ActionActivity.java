package com.example.memorandum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActionActivity extends AppCompatActivity implements View.OnClickListener {
    private int action;
    private Button btnAdd, btnBack;
    private EditText editText, editTitle;
    private MemoSQLiteOpenHelper sqliteHelper;
    private SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        action = getIntent().getIntExtra("action", 1);
        sqliteHelper = new MemoSQLiteOpenHelper(this);
        database = sqliteHelper.getWritableDatabase();
        initView();
        doAction();
    }

    private void initView() {
        editText = findViewById(R.id.edit_text);
        editTitle = findViewById(R.id.edit_title);
        btnAdd = findViewById(R.id.btn_add);
        btnBack = findViewById(R.id.btn_back);
        btnAdd.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void doAction() {
        switch (action){
            case 1:
                break;
            default:
                return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                insertDB();
                finish();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void insertDB() {
        ContentValues values = new ContentValues();
        values.put(sqliteHelper.datas, editText.getText().toString());
//        values.put(sqliteHelper.createTime, formatTime());
        values.put(sqliteHelper.lastModifyTime, formatTime());
        values.put(sqliteHelper.title, editTitle.getText().toString());
        database.insert(sqliteHelper.tableName, null, values);
    }

    private String formatTime() {
        SimpleDateFormat timeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormater.format(new Date());
    }

}