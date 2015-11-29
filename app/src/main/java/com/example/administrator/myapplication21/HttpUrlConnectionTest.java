package com.example.administrator.myapplication21;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.LogRecord;

/**
 * Created by fengminchao on 15-11-21.
 */
public   class HttpUrlConnectionTest implements Runnable {
    public Handler handler;
    public Handler revHandler;
    String urls = null;
    int i = 0;
    public HttpUrlConnectionTest(Handler handler){
        this.handler = handler;
    }
        @Override
        public void run(){
            //接受ui线程的消息
            Looper.prepare();
            revHandler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    if (msg.what == 0x345){
                        try{
                        urls = msg.obj.toString();
                        i ++;}
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    if (i > 0) revHandler.getLooper().quit();
                }
            };
            Looper.loop();

        StringBuilder sb = new StringBuilder();
            System.out.println(urls);
            StringBuilder stringBuilder = new StringBuilder(urls);
            //进行网络连接
        try{URL url = new URL(stringBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) (url).openConnection();
            conn.setConnectTimeout(6*1000);
            conn.setReadTimeout(6*1000);
            InputStreamReader st = new InputStreamReader(conn.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(st);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            st.close();
            br.close();
            //向ui线程发送消息
            Message msg = Message.obtain();
            msg.what = 0x123;
            Bundle bundle = new Bundle();
            bundle.putString("json", sb.toString());
            msg.setData(bundle);
            MainActivity.getHandler().sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
