package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{

    private String TAG = "MyList2Activity";
    Handler handler;
    List<HashMap<String, String>> listItems;
    private SimpleAdapter listItemAdapter;
    private int msgwhat = 7;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();
       // MyAdapter myAdapter = new MyAdapter(this,R.layout.list_item,listItems);//自定义Adapter
        //this.setListAdapter(myAdapter);
        this.setListAdapter(listItemAdapter);

        Thread t = new Thread(this);//创建新线程
        t.start();//开启线程

        handler = new Handler(){//用于获取其他线程中的消息
            @Override
            public void handleMessage(@NonNull Message msg) {//获得数据队列
                if(msg.what == msgwhat){//判断数据是哪个线程返回的
                    listItems = (List<HashMap<String, String>>) msg.obj;
                    //生成适配器的Item和动态数组对应的元素
                    listItemAdapter= new SimpleAdapter(MyList2Activity.this, listItems,//数据源
                            R.layout.list_item,//布局实现
                            new String[] {"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail}
                            );
                    setListAdapter(listItemAdapter);
                    Log.i("handler","reset list...");
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    private void  initListView(){//初始化静态数据
        listItems = new ArrayList<HashMap<String, String>>();
        for(int i = 0;i <10 ; i++){
            HashMap<String,String>map = new HashMap<String,String>();//初始化Map
            map.put("ItemTitle","Rate："+i);
            map.put("ItemDetail","detail："+i);
            listItems.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems,//数据源
                R.layout.list_item,//布局实现
                new String[] {"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail});

    }

    @Override
    public void run() {
        Log.i("thread","run......");
        List<HashMap<String,String>> rateList = new ArrayList<HashMap<String,String>>();

        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();//从网页中获得doc对象
            Log.i("run","title:"+ doc.title());//获得body的title
            Elements tables = doc.getElementsByTag("table");//在Document dot中获取所有table内的内容
            Element table = tables.get(1);
            //Log.i(TAG,"table="+table);
            //在Element table获取td中的数据
            Elements tds = table.getElementsByTag("td");
            for(int i = 0;i<tds.size();i+=8){
                String text = tds.get(i).text();
                String value = tds.get(i + 5).text();
                Log.i("run", text+"===>"+ value);

                HashMap<String,String>  map = new HashMap<String,String>();
                map.put("ItemTitle",text);
                map.put("ItemDetail",value);

                rateList.add(map);
            }
        }catch (IOException e) {
            Log.e("run",e.toString());
            e.printStackTrace();
        }


        Message msg = handler.obtainMessage(msgwhat);//标识what用于massage
        //msg.what = 5;
        msg.obj = rateList;//编辑msg内容
        handler.sendMessage(msg);//将msg发送至消息队列
        Log.i("thread","sendMessage......");
    }

    //事件处理方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"onItemClick:position="+position);
        //获取列表对象的map数据
        HashMap<String,String> map = (HashMap<String,String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG,"onItemClick:titleStr="+titleStr);
        Log.i(TAG,"onItemClick:detailStr="+detailStr);

        //通过列表对象的控件获得数据
        //获取控件
        TextView title= view.findViewById(R.id.itemTitle);
        TextView detail= view.findViewById(R.id.itemDetail);
        //获取控件中的数据
        String titleStr2 = String.valueOf(title.getText());
        String detailStr2 = String.valueOf(detail.getText());
        Log.i(TAG,"onItemClick:titleStr2="+titleStr2);
        Log.i(TAG,"onItemClick:detailStr2="+detailStr2);

        //打开新的页面
        Intent rateCalc = new Intent(this,RateCatculateActivity.class);
        //带入数据
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("delail",Float.parseFloat(detailStr));//detailStr由String转为float
        startActivity(rateCalc);


    }

    @Override
    //长按删除操作
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG,"onItemLongClick:长按列表项="+view);
        //弹出确认对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示").setMessage("请确认是否删除当前数据")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {//匿名类对象监听对话框是/否
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"onItemLongClick:onClick:对话框处理：是");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        })
            .setNegativeButton("否",null);
        dialog.create().show();
        return true;
    }
}
