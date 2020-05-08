package com.swufe.firstapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



public class TestActivity extends AppCompatActivity implements Runnable {

    private String TAG = "TestActivity";
    Handler handler;
    EditText editText;
    ListView listView;
    private SimpleAdapter listItemAdapter;
    String input = "";
    List<HashMap<String, String>> list;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        editText = findViewById(R.id.input);
        listView = findViewById(R.id.textListView);

        //获取SP中的数据
        SharedPreferences sp = getSharedPreferences("mydata", Activity.MODE_PRIVATE);
        String data = sp.getString("data", "");
        String update_calendarStr = sp.getString("update_calendar","");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<HashMap<String,String>>>() {
        }.getType();
        list = gson.fromJson(data, listType);
        Log.i(TAG,"onCreate:SharedPreferences:已获取sp保存的list数据");


        //初始化ListView
        List<HashMap<String,String>> initList = new ArrayList<HashMap<String,String>>();
        ListView listView = findViewById(R.id.textListView);
        for(int i = 0;i <10 ; i++){
            HashMap<String,String>map = new HashMap<String,String>();//初始化Map
            map.put("title","title："+i);
            map.put("href","href："+i);
            initList.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, initList,//数据源
                R.layout.list_item,//布局实现
                new String[] {"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail});
        Log.i(TAG,"onCreate:initList:已初始化listView");



        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当期系统时间
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        //计算下次更新时间
        Date update_day = new Date();
        try {
            update_day = sdf.parse(update_calendarStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar update_cal = Calendar.getInstance();
        assert update_day != null;
        update_cal.setTime(update_day);
        update_cal.add(Calendar.DATE,7);
        Date reset_date = update_cal.getTime();
        //判断是否开启子线程
        Log.i(TAG,"onCreate:当期日期："+sdf.format(today.getTime()));
        Log.i(TAG,"onCreate:下次更新日期："+sdf.format(reset_date.getTime()));
//        if(!reset_date.after(today)){
            Thread t = new Thread(this);
            t.start();
            Log.i(TAG,"onCreate:满足更新时间，打开子线程");
//        }else{
//            Log.i(TAG,"onCreate:未达更新时间，不打开子线程");
//        }

        handler = new Handler(){//用于获取其他线程中的消息
            @Override
            public void handleMessage(@NonNull Message msg) {//获得数据队列
                if(msg.what == 1){//判断数据是哪个线程返回的
                    list = (List<HashMap<String, String>>)msg.obj;
                    Log.i(TAG,"onCreate:handler:获得数据队列中的list数据");
                }
                super.handleMessage(msg);

                listItemAdapter= new SimpleAdapter(TestActivity.this, list,//数据源
                        R.layout.activity_test_list,//布局实现
                        new String[] {"title","href"},
                        new int[]{R.id.testTitle,R.id.testURL}
                );
                listView.setAdapter(listItemAdapter);
                Log.i(TAG,"onCreate:handler:利用list数据重置listView");


                SharedPreferences sp = getSharedPreferences("mydata", Activity.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = gson.toJson(list);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("data",json);
                //保存更新的日期
                editor.putString("update_calendar",sdf.format(calendar.getTime()));
                editor.apply();
                Log.i(TAG,"onCreate:handler:SharedPreferences:已保存list数据及更新日期:"+sdf.format(calendar.getTime()));


            }
        };


        editText.addTextChangedListener(new TextWatcher() {//匿名对象监听
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override//监听数据变化后
            public void afterTextChanged(Editable s) {
                List<HashMap<String, String>> listViews = new ArrayList<HashMap<String, String>>();
                input = s.toString();
                Log.i(TAG,"onCreate:editText:afterTextChanged:查询关键词:"+input);
                boolean b = false;
                for(HashMap<String, String> map:list){
                    String title = map.get("title");
                    assert title != null;
                    if (title.contains(input)){
                        HashMap<String,String>  map2 = new HashMap<String,String>();
                        map2.put("href",map.get("href"));
                        map2.put("title",title);
                        listViews.add(map2);
                        Log.i(TAG,"onCreate:editText:afterTextChanged:标题:"+title);
                        b = true;
                    }
                }
                if (!b){
                    Log.i(TAG,"onCreate:editText:afterTextChanged:没有查询到相关公告");
                    Toast.makeText(TestActivity.this,"没有查询到相关公告",Toast.LENGTH_LONG).show();
                }

                //展示查询结果
                listItemAdapter= new SimpleAdapter(TestActivity.this, listViews,//数据源
                        R.layout.activity_test_list,//布局实现
                        new String[] {"title","href"},
                        new int[]{R.id.testTitle,R.id.testURL}
                );
                listView.setAdapter(listItemAdapter);
                Log.i(TAG,"onCreate:editText:afterTextChanged:已在listView展示查询结果");
            }

        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title= view.findViewById(R.id.testTitle);
                TextView href= view.findViewById(R.id.testURL);
                String titleStr = String.valueOf(title.getText());
                String hrefStr = String.valueOf(href.getText());
                Log.i(TAG,"onCreate:listView:onItemClick:点击标题："+titleStr);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hrefStr));
                startActivity(intent);
                Log.i(TAG,"onCreate:listView:onItemClick:正在打卡链接："+hrefStr);
            }
        });
    }

    @Override
    public void run() {
        Log.i(TAG,"run:子线程运行...");

        //获取学院title及href数据至itemList
        List<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();//从网页中获得doc对象
            Log.i(TAG,"run:查询页面标题:"+ doc.title());//获得body的title
            Elements uls = doc.getElementsByTag("ul");//在Document dot中获取所有table内的内容
            Element ul = uls.get(17);
            //Log.i(TAG,"run:ul:"+ ul);
            Elements as = ul.getElementsByTag("a");
            for(int i = 0;i<as.size();i++){
                Elements spans = as.get(i).getElementsByTag("span");
                String title = spans.get(0).text();
                Log.i(TAG, "run:title:"+ title);
                String href = "https://it.swufe.edu.cn/"+as.get(i).attr("href");
                Log.i(TAG, "run:href:"+ href);

                HashMap<String,String>  map = new HashMap<String,String>();
                map.put("href",href);
                map.put("title",title);
                itemList.add(map);
            }
            Log.i(TAG, "run:已获取title及href数据至itemList");
        }catch (IOException e) {
            Log.e(TAG,"run:"+e.toString());
            e.printStackTrace();
        }

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(1);//标识what用于massage
        //msg.what = 5;
        msg.obj = itemList;//编辑msg内容
        handler.sendMessage(msg);//将msg发送至消息队列
        Log.i(TAG,"run:子线程的itemList数据发送至消息队列Message");
    }
}
