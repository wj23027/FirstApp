package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    EditText tempC ;
    TextView tempF ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        tempC = findViewById(R.id.tempC);
        tempF = findViewById(R.id.tempF);
    }

    @SuppressLint("SetTextI18n")
    public void CToF(View v) {
        String inputTemp = tempC.getText().toString();
        double outputTemp = Double.parseDouble(inputTemp) * 1.8 + 32;
        tempF.setText(""+outputTemp+" ℉");
    }
}
