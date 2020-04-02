package com.swufe.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    public final String TAG = "ConfigActivity";
    EditText dollartext;
    EditText eurotext;
    EditText wontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        //取intent对象
        Intent intent = getIntent();
        float dollar = intent.getFloatExtra("dollar",0.0f);
        float euro = intent.getFloatExtra("euro",0.0f);
        float won = intent.getFloatExtra("won",0.0f);
        Log.i(TAG,"onGreate:dollar2="+dollar);
        Log.i(TAG,"onGreate:euro2="+euro);
        Log.i(TAG,"onGreate:won="+won);

        dollartext = findViewById(R.id.dollar_cfg);
        eurotext = findViewById(R.id.euro_cfg);
        wontext = findViewById(R.id.won_cfg);

        dollartext.setText(String.valueOf(dollar));
        eurotext.setText(String.valueOf(euro));
        wontext.setText(String.valueOf(won));
    }

    public void save(View btn){
        Log.i(TAG,"save");
        //获取新的输入数据
        float newDollar = Float.parseFloat(dollartext.getText().toString());
        float newEuro = Float.parseFloat(eurotext.getText().toString());
        float newWon = Float.parseFloat(wontext.getText().toString());
        Log.i(TAG,"save:获取到新的值");
        Log.i(TAG,"onGreate:newDollar="+newDollar);
        Log.i(TAG,"onGreate:newEuro="+newEuro);
        Log.i(TAG,"onGreate:newon="+newWon);

        //保存到Bundle或放入Extra
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putFloat("newD",newDollar);
        bdl.putFloat("newE",newEuro);
        bdl.putFloat("newW",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);
        //返回到调用页面
        finish();

    }
}
