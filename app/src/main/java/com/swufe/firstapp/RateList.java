package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateList extends ListActivity implements Runnable {//调用Runnable,开启子线程
    Handler handler;
    public final String TAG = "RateList";
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);
        List<String> list1 = new ArrayList<String>();
        for(int i = 1;i < 100;i++){
            list1.add("item"+i);
        }
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list1);//将数据与adapter对应
        setListAdapter(adapter);//将Adapter与布局文件对应
        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){//主线程
            public void handleMessage(@NotNull Message msg){
                if(msg.what==2){//获取子线程信息
                    List<String> list = (List<String>) msg.obj;//拆包，获取msg中的数据
                    ListAdapter adapter = new ArrayAdapter<String>(RateList.this,android.R.layout.simple_list_item_1,list);//将数据与adapter对应
                    setListAdapter(adapter);//将Adapter与布局文件对应
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        List<String> list = new ArrayList<String>();

        //获取网络数据，放入list带回到主线程
        //获得网络数据
        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();//从网页中获得doc对象
            Log.i(TAG,"title:"+ doc.title());//获得body的title
            Elements tables = doc.getElementsByTag("table");//在Document dot中获取所有table内的内容
            Element table = tables.get(1);//获取第二个table
            //Log.i(TAG,"table="+table);
            //在Element table获取td中的数据
            Elements tds = table.getElementsByTag("td");//获取所有td
            for(int i = 0;i<tds.size();i+=8){
                String text = tds.get(i).text();
                String value = tds.get(i + 5).text();
                Log.i(TAG, text+"===>"+ value);
                list.add(text+"===>"+ value);
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(2);//标识what用于massage
        //msg.what = 5;
        msg.obj = list;//编辑msg内容
        handler.sendMessage(msg);//将msg发送至消息队列
    }
}
