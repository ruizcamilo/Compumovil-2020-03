package com.example.introintrocompumovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class Ejercicio1 extends AppCompatActivity {

    Button frame, web, list;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio1);
        frame = findViewById(R.id.button3);
        web = findViewById(R.id.button2);
        list=findViewById(R.id.button4);
        frame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(),FrameActivity.class));
            }
        });
        web.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(),WebActivity.class));
            }
        });
        list.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(),ListActivity.class));
            }
        });
    }
}