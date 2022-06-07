package com.example.memorandum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OneMemoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;
    private Button delete, back, edit;
    private MemoSQLiteOpenHelper sqliteHelper;
    private SQLiteDatabase database;
    private String pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_memo);
        initView();
        delete.setOnClickListener(this);
        back.setOnClickListener(this);
        edit.setOnClickListener(this);
        sqliteHelper = new MemoSQLiteOpenHelper(this);
        database = sqliteHelper.getWritableDatabase();
        pos = getIntent().getStringExtra(sqliteHelper.id);
        String content = getIntent().getStringExtra(sqliteHelper.datas);
        textView.setText(content);
    }

    private void initView() {
        textView = findViewById(R.id.look_text);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete:
                deleteByPos();
                Intent intent1 = new Intent(OneMemoActivity.this, DeleteService.class);
                intent1.putExtra("pos", pos);
                startService(intent1);
                finish();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                Intent intent = new Intent(OneMemoActivity.this, EditActivity.class);
                intent.putExtra(sqliteHelper.id, getIntent().getStringExtra(sqliteHelper.id));
                intent.putExtra(sqliteHelper.datas, getIntent().getStringExtra(sqliteHelper.datas));
                startActivity(intent);
                break;
        }
    }

    private void deleteByPos() {
        database.delete(sqliteHelper.tableName, sqliteHelper.id + "=" + pos, null);
    }
}