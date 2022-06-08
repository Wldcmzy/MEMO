package com.example.memorandum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText, editTitle;
    private Button save, cancel;
    private MemoSQLiteOpenHelper sqliteHelper;
    private SQLiteDatabase database;
    private String pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initView();
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        sqliteHelper = new MemoSQLiteOpenHelper(this);
        database = sqliteHelper.getWritableDatabase();
        pos = getIntent().getStringExtra(sqliteHelper.id);
        String datas = getIntent().getStringExtra(sqliteHelper.datas);
        editText.setText(datas);
        editText.setSelection(datas.length());
        String title = getIntent().getStringExtra(sqliteHelper.title);
        editTitle.setText(title);
        editTitle.setSelection(title.length());
    }

    private void initView() {
        editText = findViewById(R.id.edit_text);
        editTitle = findViewById(R.id.edit_title);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                database.delete(sqliteHelper.tableName, sqliteHelper.id + "=" + pos, null);
                insertDB();
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    private void insertDB() {
        ContentValues values = new ContentValues();
        values.put(sqliteHelper.datas, editText.getText().toString());
        //values.put(sqliteHelper.createTime, formatTime());
        values.put(sqliteHelper.lastModifyTime, formatTime());
        values.put(sqliteHelper.title, editTitle.getText().toString());
        database.insert(sqliteHelper.tableName, null, values);
    }

    private String formatTime() {
        SimpleDateFormat timeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormater.format(new Date());
    }
}