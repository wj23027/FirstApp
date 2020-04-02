package com.swufe.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {
    public final String TAG = "RateActivity";
    EditText rmb;
    TextView show;
    float dollarRate = 1/6.5f;
    float euroRate = 1/11f;
    float wonRate = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public void calculate(View btn){
        //获得用户输入内容
        String str = rmb.getText().toString();
        float r = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
            show.setText("请输入");
        }

        if(btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else if(btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else if(btn.getId()==R.id.btn_won){
            show.setText(String.format("%.2f",r*wonRate));
        }

    }

    public void  openOther(View btn){
        Log.i("open","openOne");
        onConfig();

    }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onConfig();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("newD",0.1f);
            euroRate = bundle.getFloat("newE",0.1f);
            wonRate = bundle.getFloat("newW",0.1f);
            Log.i(TAG,"new dollarRate="+dollarRate);
            Log.i(TAG,"new euroRate="+euroRate);
            Log.i(TAG,"new wonRate="+wonRate);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
