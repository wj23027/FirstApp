package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {
    public final String TAG = "RateActivity";
    EditText rmb;
    TextView show;
    Handler handler;
    float dollarRate;
    float euroRate;
    float wonRate;
    String updateDate="";

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        //获得用户输入控件
        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);

        //获得SP里保存的数据
        //SharedPreferences sp2 = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate = sp.getFloat("dollar_rate",0.0f);
        euroRate = sp.getFloat("euro_rate",0.0f);
        wonRate = sp.getFloat("won_rate",0.0f);
        updateDate = sp.getString("update_date","");

        //获取当期系统时间
        Date today = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG,"dollar:"+dollarRate);
        Log.i(TAG,"euro:"+euroRate);
        Log.i(TAG,"won:"+wonRate);
        Log.i(TAG,"updateDate:"+updateDate);
        Log.i(TAG,"todayString:"+todayStr);

        //判断是否更新
        if(!todayStr.equals(updateDate)){
            //开启子线程
            Thread t = new Thread(this);
            t.start();
            Log.i(TAG,"需要更新");
        }else{
            Log.i(TAG,"不需要更新");
        }



        handler = new Handler(){//用于获取其他线程中的消息
            @Override
            public void handleMessage(@NonNull Message msg) {//获得数据队列
                if(msg.what == 5){//判断数据是哪个线程返回的
                    Bundle bundle = (Bundle) msg.obj;
                    dollarRate = bundle.getFloat("dollar-rate");
                    euroRate = bundle.getFloat("euro-rate");
                    wonRate = bundle.getFloat("won-rate");
                    Log.i(TAG,"网页中的dollar:"+dollarRate);
                    Log.i(TAG,"网页中的euro:"+euroRate);
                    Log.i(TAG,"网页中的won:"+wonRate);

                    //保存更新的日期
                    SharedPreferences sp = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("update_date",todayStr);
                    Log.i(TAG,"已保存sp update_date:"+todayStr);
                    editor.apply();

                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

    }


    @Override//创建菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @SuppressLint("DefaultLocale")
    public void calculate(View btn){
        //获得用户输入内容
        String str = rmb.getText().toString();
        float r = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
            show.setText("请输入");
            return;
        }

        //计算并输出
        if(btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else if(btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else if(btn.getId()==R.id.btn_won){
            show.setText(String.format("%.2f",r*wonRate));
        }

    }

    //获得事件（参数类型为View)
    public void  openOther(View btn){
        Log.i("open","openOne");
        onConfig();
    }

    //打开新页面并传入参数
    private void onConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar",dollarRate);
        config.putExtra("euro",euroRate);
        config.putExtra("won",wonRate);
        Log.i(TAG,"dollar:"+dollarRate);
        Log.i(TAG,"euro:"+euroRate);
        Log.i(TAG,"won:"+wonRate);
        //startActivity(config);
        startActivityForResult(config,1);
    }

    @Override//通过菜单下拉框打开新页面
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_set){
            onConfig();
        }else if(item.getItemId() == R.id.rate_list){
            Intent list = new Intent(this, MyList2Activity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override//打开设置页面并返回设置页面中的值）
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==2){
            //获得bundle中的值
            assert data != null;
            Bundle bundle = data.getExtras();
            assert bundle != null;
            dollarRate = bundle.getFloat("newD",0.1f);
            euroRate = bundle.getFloat("newE",0.1f);
            wonRate = bundle.getFloat("newW",0.1f);
            Log.i(TAG,"new dollarRate="+dollarRate);
            Log.i(TAG,"new euroRate="+euroRate);
            Log.i(TAG,"new wonRate="+wonRate);

            //将新设置的汇率写到sp里
            SharedPreferences sp = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.apply();
            Log.i(TAG,"onActivityResult:数据已保存到sp"+dollarRate);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override//多线程
    public void run() {
        Bundle bundle = getFromBOC(); ;//用于保存获取的汇率


//        Log.i(TAG,"runrun");
//        for (int i = 1;i<6;i++) {
//            Log.i(TAG, "run:i=" + i);
//            //当期停止两秒钟
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

            //获取Msg对象，用于返回主线程
            Message msg = handler.obtainMessage(5);//标识what用于massage
            //msg.what = 5;
            msg.obj = bundle;//编辑msg内容
            handler.sendMessage(msg);//将msg发送至消息队列


        //bundle中通过massage保存数据
        //获取Msg对象，用于返回主线程
//        while(true){
//            try {
//                Thread.sleep(86400000);//使用sleep(long)方法定时刷新
//                Message msg = handler.obtainMessage(5);//标识what用于massage
//                //msg.what = 5;
//                msg.obj = bundle;//编辑msg内容
//                handler.sendMessage(msg);//将msg发送至消息队列
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }




//        URL url = null;
//        try {
//            Log.i(TAG,"run:html=");
//            url = new URL("http://www.usd-cny.com/bankofchina.htm");
//            //利用HttpURLConnection打开远程链接
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream inputStream = http.getInputStream();//获得页面的输入流
//            String html = inputStringToString(inputStream);//解析数据流中的文本（从inputStream转为String
//            Log.i(TAG,"run:html="+html);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }


/*
获得网络数据到Bundle
 */
    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        //获得网络数据
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();//从网页中获得doc对象
            Log.i(TAG,"title:"+ doc.title());//获得body的title
            Elements tables = doc.getElementsByTag("table");//在Document dot中获取所有table内的内容
            Element table = tables.get(1);
            //Log.i(TAG,"table="+table);
            //在Element table获取td中的数据
            Elements tds = table.getElementsByTag("td");
            for(int i = 0;i<tds.size();i+=8){
                String text = tds.get(i).text();
                String value = tds.get(i + 5).text();
                //Log.i(TAG, text+"===>"+ value);
                if("美元".equals(text)){
                    Log.i(TAG, text+"===>"+ 100f/Float.parseFloat(value));
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(value));
                }else if("欧元".equals(text)){
                    Log.i(TAG, text+"===>"+ 100f/Float.parseFloat(value));
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(value));
                }if("韩元".equals(text)){
                    Log.i(TAG, text+"===>"+ 100f/Float.parseFloat(value));
                    bundle.putFloat("won-rate",100f/Float.parseFloat(value));
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    private Bundle getFromUsdCny() {
        Bundle bundle = new Bundle();
        //获得网络数据
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();//从网页中获得doc对象
            Log.i(TAG,"title:"+ doc.title());//获得body的title
            Elements tables = doc.getElementsByTag("table");//在Document dot中获取所有table内的内容
            Element table = tables.get(0);
            //Log.i(TAG,"table="+table);
            //在Element table获取td中的数据
            Elements tds = table.getElementsByTag("td");
            for(int i = 0;i<tds.size();i+=6){
                String text = tds.get(i).text();
                String value = tds.get(i + 5).text();
                //Log.i(TAG, text+"===>"+ value);
                if("美元".equals(text)){
                    Log.i(TAG, text+"===>"+ 100f/Float.parseFloat(value));
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(value));
                }else if("欧元".equals(text)){
                    Log.i(TAG, text+"===>"+ 100f/Float.parseFloat(value));
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(value));
                }if("韩元".equals(text)){
                    Log.i(TAG, text+"===>"+ 100f/Float.parseFloat(value));
                    bundle.putFloat("won-rate",100f/Float.parseFloat(value));
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    //将输入流转化为字符串(可到网上查找方法）
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String inputStringToString(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for(; ; ){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz < 0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
