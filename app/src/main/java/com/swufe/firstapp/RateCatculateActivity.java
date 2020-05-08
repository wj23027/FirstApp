package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RateCatculateActivity extends AppCompatActivity {
    float rate = 0f;
    String TAG = "rateCatculrate";
    EditText editText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_cat);

        //获得上个页面传入的数据
        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("delail",0f);

        //将获得的数据放入textView进行计算
        ((TextView)findViewById(R.id.title2)).setText(title);

        editText = findViewById(R.id.input);
        //对editText添加文本改变监听器
        editText.addTextChangedListener(new TextWatcher() {//匿名对象监听
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override//监听数据变化后
            public void afterTextChanged(Editable s) {
                TextView show = RateCatculateActivity.this.findViewById(R.id.show);
                if(s.length()>0){
                    float val = Float.parseFloat(s.toString());//获取输入框中的数据
                    float result = (100f/rate*val);
                    show.setText(val+" RMB===> "+String.format("%.2f",result));//显示计算结果
                }else{
                    show.setText("");
                }

            }
        });

    }
}
