package com.swufe.firstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {

    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);
    }

    public void calculate(View btn){
        //获得用户输入内容
        String str = rmb.getText().toString();
        float r = 0;
        float val = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
            show.setText("请输入");
        }

        if(btn.getId()==R.id.btn_dollar){
            val = r / 6.5f;
        }else if(btn.getId()==R.id.btn_euro){
            val = r / 11;
        }else if(btn.getId()==R.id.btn_won){
            val = r * 500;
        }
        show.setText(String.format("%.2f",val)+"");
    }

    public void  openOther(View btn){
        Log.i("open","openOne");
        Intent hello = new Intent(this,ScoreActivity.class);
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("tel:15607001699"));
        Intent web = new Intent(Intent.ACTION_DIAL, Uri.parse("https://m.weibo.cn/"));
        startActivity(web);
    }
}
