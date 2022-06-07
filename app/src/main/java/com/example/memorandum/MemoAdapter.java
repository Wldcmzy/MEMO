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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_member, null);
            holder = new Holder();
            holder.text = convertView.findViewById(R.id.show_text);
            holder.textTime = convertView.findViewById(R.id.show_time);
            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }
        cursor.moveToPosition(position);

        int tmp;
        tmp = cursor.getColumnIndex("datas");
        String content = cursor.getString(tmp);
        Log.d(">>>>>>>>", content + "<<<<<<<<<<<");
        tmp = cursor.getColumnIndex("lastModifyTime");
        String time = cursor.getString(tmp);
        holder.text.setText(content);
        holder.textTime.setText(time);
        return convertView;
    }
}

class Holder {
    TextView text;
    TextView textTime;
}