package com.example.memorandum;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MemoAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;

    public MemoAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            // 将布局实例化
            convertView = LayoutInflater.from(context).inflate(R.layout.list_member, null);

            holder = new Holder();
            holder.text = convertView.findViewById(R.id.show_text);
            holder.textTime = convertView.findViewById(R.id.show_time);
            //将holder数据缓存起来
            convertView.setTag(holder);
        }else {
            //提取holder缓存数据
            holder = (Holder)convertView.getTag();
        }
        cursor.moveToPosition(position);

        //getColumnIndex允许返回的-1, 是getString的非法参数。
        int tmp;
        tmp = cursor.getColumnIndex("datas");
        String datas = cursor.getString(tmp);
        tmp = cursor.getColumnIndex("lastModifyTime");
        String Mtime = cursor.getString(tmp);
//        tmp = cursor.getColumnIndex("createTime");
//        String Ctime = cursor.getString(tmp);
//        String[] dataArray = datas.split("\n");
        tmp = cursor.getColumnIndex("title");
        String title = cursor.getString(tmp);
        holder.text.setText(title);
//        Log.d(">>>>>>>>", dataArray[0] + "<<<<<<<<<<<");
//        holder.textTime.setText("修改时间:" + Mtime + "创建时间" + Ctime);
        holder.textTime.setText("最近修改时间:  " + Mtime);
        return convertView;
    }
}

class Holder {
    TextView text;
    TextView textTime;
}