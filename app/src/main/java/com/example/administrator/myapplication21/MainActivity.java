package com.example.administrator.myapplication21;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {
    private Button search;
    private EditText edt;
    HttpUrlConnectionTest httpUrlConnectionTest;
    String path;

    List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
    SimpleAdapter simpleAdapter;
    static Handler handler;
    public static Handler getHandler(){
        return handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4, 200, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(5));
        edt = (EditText) findViewById(R.id.edt);
        search = (Button) findViewById(R.id.search);
        httpUrlConnectionTest = new HttpUrlConnectionTest(handler);
        //接受其他线程的消息
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    try {
                        String json = msg.getData().getString("json");
                        JSONObject jsonObj = new JSONObject(json);
                        JSONArray jsonArr = jsonObj.getJSONArray("users");
                        for (int i = 0; i < jsonArr.length(); i++) {
                            Map<String, Object> list = new HashMap<String, Object>();
                            list.put("name", "姓名:" + jsonArr.getJSONObject(i).getString("name"));
                            list.put("id", "ID:" + jsonArr.getJSONObject(i).getString("id"));
//                    list.put("loc_name", "现居地：" + jsonArr.getJSONObject(i).getString("loc_name"));
                            list.put("created", "创建时间：" + jsonArr.getJSONObject(i).getString("created"));
                            list.put("desc", "简介：" + jsonArr.getJSONObject(i).getString("desc"));
                            lists.add(list);
                        }
                        simpleAdapter = new SimpleAdapter(MainActivity.this, lists, R.layout.list_item,
                                new String[]{"name", "id", "created", "desc"}, new int[]{R.id.name, R.id.id, R.id.created, R.id.desc});
                        ListView lv = (ListView) findViewById(R.id.lv);
                        lv.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pool.execute(httpUrlConnectionTest);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                try{stringBuilder.append("https://api.douban.com/v2/user?q=").append(URLEncoder.encode(edt.getText().toString(),"utf-8"));
                Message msg = new Message();
                    msg.what = 0x345;
                    msg.obj = stringBuilder;
                    path = stringBuilder.toString();
                    httpUrlConnectionTest.revHandler.sendMessage(msg);
                }
                catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });
    }
}