package com.example.memorandum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CloudActivity extends AppCompatActivity implements View.OnClickListener{
    private Button saveAddr, loadAddr, upload, download;
    private EditText editIp, editPort, storageName, storagePassword;
    final private String addressFile = "MemoAddress34.txt";
    private MemoSQLiteOpenHelper sqliteHelper;
    private SQLiteDatabase database;
    private int buff_size = 4096;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        initView();
    }

    private void initView(){
        saveAddr = findViewById(R.id.ipsave);
        saveAddr.setOnClickListener(this);
        loadAddr = findViewById(R.id.ipread);
        loadAddr.setOnClickListener(this);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(this);
        download = findViewById(R.id.download);
        download.setOnClickListener(this);

        editIp = findViewById(R.id.edit_ip);
        editPort = findViewById(R.id.edit_port);
        storageName = findViewById(R.id.storage_name);
        storagePassword = findViewById(R.id.storage_pswd);

        sqliteHelper = new MemoSQLiteOpenHelper(this);
        database = sqliteHelper.getReadableDatabase();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ipread:
                selectIfAddressLoad();
                break;
            case R.id.ipsave:
                selectIfAddressDump();
                break;
            case R.id.upload:
                selectIfUpload(editIp.getText().toString(), editPort.getText().toString());
//                upload(editIp.getText().toString(), editPort.getText().toString());
//                uploadUDP(editIp.getText().toString(), editPort.getText().toString());
                break;
            case R.id.download:
                selectIfDownload(editIp.getText().toString(), editPort.getText().toString());
                break;
            default:
                break;
        }
    }


    private void selectIfAddressLoad(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CloudActivity.this);
        builder.setMessage("你确定要将地址设为默认值吗?")
                .setTitle("二次询问")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addressLoad();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("不,刚才我手滑了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void addressLoad() {
        File file = new File(addressFile);

        try {
            FileInputStream fis = openFileInput(addressFile);

            byte[] buff = new byte[1024];

            int len;
            String data = "";
            while ((len = fis.read(buff)) >= 0) {
                data += new String(buff, 0, len);
            }
            fis.close();

            String[] dataArray = data.split("\n");

            if(dataArray.length >= 1){
                editIp.setText(dataArray[0]);
            }else{
                editIp.setText("");
            }
            if(dataArray.length >= 2){
                editPort.setText(dataArray[1]);
            }else{
                editPort.setText("");
            }


        } catch (FileNotFoundException e) {
            Log.i("no file", e.getMessage() + "<<<<<<<<<<<<<<<<<");
            Toast.makeText(this, "无储存好的默认地址", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.i("error_ipdump", e.getMessage() + "<<<<<<<<<<<<<<<<<" + e.toString());
        }
    }

    private void selectIfAddressDump(){
        String addr = editIp.getText().toString() + ":" + editPort.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(CloudActivity.this);
        builder.setMessage("你确定要将" + addr + "设为默认地址吗?")
                .setTitle("二次询问")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addressDump();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("不,刚才我手滑了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void addressDump(){
        String data = editIp.getText().toString() + "\n" + editPort.getText().toString();
        try{

            FileOutputStream fos = openFileOutput(addressFile, Context.MODE_PRIVATE);

            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(this, "保存完成", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.i("error_ipdump", e.getMessage() + "<<<<<<<<<<<<<<<<<");
        }

    }


    private void selectIfUpload(String ip, String port){
        String addr = editIp.getText().toString() + ":" + editPort.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(CloudActivity.this);
        builder.setMessage("你确定要将所有备忘录信息发送至" + addr + "吗?")
                .setTitle("二次询问")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                upload(ip, port);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("不,刚才我手滑了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }


    private void upload(String ip, String port){
        new Thread() {
            //线程运行的代码
            public void run() {
                boolean know_error = false;
                Looper.prepare();
                try {

                    String db_name = storageName.getText().toString();
                    String db_key = storagePassword.getText().toString();
                    if(db_name.length() <= 2 || db_key.length() <= 2){
                        know_error = true;
                        Toast.makeText(CloudActivity.this, "库名和密码长度需要大于2", Toast.LENGTH_SHORT).show();
                        throw new Exception("too short of name or key");
                    }

                    InetAddress serverip= InetAddress.getByName(ip);;//定义保存服务器地址的对象
                    Socket client=new Socket(serverip, Integer.parseInt(port));//定义创建客户端对象的Socket

                    OutputStream socketOut=client.getOutputStream(); //定义发送信息的输出流对象
                    InputStream socketIn=client.getInputStream(); //定义接收数据的输入流

                    byte receive[] = new byte[buff_size]; //定义保存客户端发送来的数据的字节数组

                    Cursor cursor = database.query(sqliteHelper.tableName, null,null,null,null,null, sqliteHelper.lastModifyTime);
                    int rowSize = cursor.getCount();
                    String sep = "_@#sE!p_";

                    String firstData = db_name + sep + db_key + sep + "upload";
                    socketOut.write(firstData.getBytes("utf-8"));
                    int len=socketIn.read(receive);
                    String rev=new String(receive,0,len);

                    if (! rev.equals("ok")){
                        know_error = true;
                        Toast.makeText(CloudActivity.this, "拒绝访问", Toast.LENGTH_SHORT).show();
                        throw new Exception("access deny");
                    }

                    for(int row=0; row<rowSize ; row++) {

                        String data = Integer.toString(row);
                        cursor.moveToPosition(row);

                        int tmp;
                        tmp = cursor.getColumnIndex(sqliteHelper.title);
                        data += sep + cursor.getString(tmp);
                        tmp = cursor.getColumnIndex(sqliteHelper.lastModifyTime);
                        data += sep + cursor.getString(tmp);
                        tmp = cursor.getColumnIndex(sqliteHelper.datas);
                        data += sep + cursor.getString(tmp);

                        socketOut.write(data.getBytes("utf-8"));

                        len=socketIn.read(receive);
                        //将读取来保存在字节数组中的数据转换成字符串
                        rev=new String(receive,0,len);
                        if (! rev.equals("ok")){
                            know_error = true;
                            Toast.makeText(CloudActivity.this, "传输中遇到错误", Toast.LENGTH_SHORT).show();
                            throw new Exception("error at half road");
                        }


                    }
                    socketOut.close();
                    socketIn.close();
                    client.close();

                    Toast.makeText(CloudActivity.this, "同步完成", Toast.LENGTH_SHORT).show();
                }catch(ConnectException e){
                    Toast.makeText(CloudActivity.this, "未能成功连接服务器", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Log.i("tcp-error", e.getMessage() + e.getStackTrace() + e.getClass());
                    if(! know_error){
                        Toast.makeText(CloudActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
                    }
                }
                Looper.loop();
            }
        }.start();//启动线程
    }

    private void selectIfDownload(String ip, String port){
        AlertDialog.Builder builder = new AlertDialog.Builder(CloudActivity.this);
        builder.setMessage("你确定要将本地数据同步未最新云端数据吗?你刚编辑的进度可能会丢失。")
                .setTitle("二次询问")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                download(ip, port);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("不,刚才我手滑了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void download(String ip, String port){
        new Thread() {
            //线程运行的代码
            public void run() {
                boolean know_error = false;
                Looper.prepare();
                try {

                    String db_name = storageName.getText().toString();
                    String db_key = storagePassword.getText().toString();
                    if(db_name.length() <= 2 || db_key.length() <= 2){
                        know_error = true;
                        Toast.makeText(CloudActivity.this, "库名和密码长度需要大于2", Toast.LENGTH_SHORT).show();
                        throw new Exception("too short of name or key");
                    }

                    InetAddress serverip= InetAddress.getByName(ip);;//定义保存服务器地址的对象
                    Socket client=new Socket(serverip, Integer.parseInt(port));//定义创建客户端对象的Socket

                    OutputStream socketOut=client.getOutputStream(); //定义发送信息的输出流对象
                    InputStream socketIn=client.getInputStream(); //定义接收数据的输入流

                    byte receive[] = new byte[buff_size]; //定义保存客户端发送来的数据的字节数组

                    String sep = "_@#sE!p_";

                    String firstData = db_name + sep + db_key + sep + "download";
                    socketOut.write(firstData.getBytes("utf-8"));
                    int len=socketIn.read(receive);
                    String rev=new String(receive,0,len);

                    if (! rev.equals("ok")){
                        know_error = true;
                        if(rev.equals("no_table")){
                            Toast.makeText(CloudActivity.this, "无此仓库", Toast.LENGTH_SHORT).show();
                            throw new Exception("no storage");
                        }else {
                            Toast.makeText(CloudActivity.this, "拒绝访问", Toast.LENGTH_SHORT).show();
                            throw new Exception("access deny");
                        }
                    }

                    database.execSQL("delete from " + sqliteHelper.tableName + ";");

                    while(true) {
                        socketOut.write("next".getBytes("utf-8"));

                        len=socketIn.read(receive);
                        //将读取来保存在字节数组中的数据转换成字符串
                        rev=new String(receive,0,len);
                        if (rev.equals("deny")){
                            know_error = true;
                            Toast.makeText(CloudActivity.this, "传输中遇到错误", Toast.LENGTH_SHORT).show();
                            throw new Exception("error at half road");
                        }

                        if (rev.equals("finished")) {
                            Toast.makeText(CloudActivity.this, "同步完成", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        String[] dataArray = rev.split(sep);
                        insertDB(dataArray[0], dataArray[1], dataArray[2]);

                    }
                    socketOut.close();
                    socketIn.close();
                    client.close();
                }catch(ConnectException e){
                    Toast.makeText(CloudActivity.this, "未能成功连接服务器", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Log.i("tcp-error", e.getMessage() + e.getStackTrace() + e.getClass());
                    if(! know_error){
                        Toast.makeText(CloudActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
                    }
                }
                Looper.loop();
            }
        }.start();//启动线程
    }

    private void insertDB(String datas, String lastModifyTime, String title) {
        ContentValues values = new ContentValues();
        values.put(sqliteHelper.datas, datas);
        values.put(sqliteHelper.lastModifyTime, lastModifyTime);
        values.put(sqliteHelper.title, title);
        database.insert(sqliteHelper.tableName, null, values);
    }

//    private void uploadUDP(String ip, String port){
//        new Thread() {
//            //线程运行的代码
//            public void run() {
//                try {
//                    Looper.prepare();
//                    InetAddress address = InetAddress.getByName(ip);
//                    Cursor cursor = database.query(sqliteHelper.tableName, null,null,null,null,null, sqliteHelper.lastModifyTime);
//                    DatagramSocket socket = new DatagramSocket();
//
//                    String sep = "_@#sE*p_";
//
//                    int cnt = cursor.getCount();
//                    for(int i=0; i<cnt ; i++){
//                        String data = Integer.toString(i);
//                        if (data.length() < 4) {
//                            for(int j=0; j< 4 - data.length(); j++){
//                                data = "0" + data;
//                            }
//                        }
//                        cursor.moveToPosition(i);
//
//                        int tmp;
//                        tmp = cursor.getColumnIndex(sqliteHelper.title);
//                        data += sep + cursor.getString(tmp);
//                        tmp = cursor.getColumnIndex(sqliteHelper.lastModifyTime);
//                        data += sep + cursor.getString(tmp);
//                        tmp = cursor.getColumnIndex(sqliteHelper.datas);
//                        data += sep + cursor.getString(tmp);
//
//                        byte[] data1 = data.getBytes();
//                        DatagramPacket packet = new DatagramPacket(data1, data1.length, address, Integer.parseInt(port));
//                        socket.send(packet);
//
//                        byte[] data2 = new byte[1 << 5];
//                        DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
//                        socket.receive(packet2);
//                        String reply = new String(data2, 0, packet2.getLength());
//                        Toast.makeText(CloudActivity.this, reply, Toast.LENGTH_SHORT).show();
//                    }
//
//                    Looper.loop();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();//启动线程
//    }
}

