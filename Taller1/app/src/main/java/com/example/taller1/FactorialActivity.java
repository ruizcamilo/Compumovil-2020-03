package com.example.taller1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class FactorialActivity extends AppCompatActivity {
    String print;
    TextView respuesta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factorial);
        respuesta = findViewById(R.id.textView2);
        Intent b = getIntent();
        int fact = Integer.parseInt(getIntent().getStringExtra("spin"));
        print=new String();
        print="Multiplicación = ";
        for(int i = 1 ; i < fact + 1 ; i++)
        {
            if(i==fact) {
                print+=i;
            }else{
                print+=i+"*";
            }
        }
        int res=Factorial(fact);
        print+="\n";
        print+="Resultado = "+res;
        respuesta.setText(print);
    }

    public int Factorial(int num){
        if(num==0)
        {
            return 1;
        }
        else{
            return num * Factorial(num-1);
        }
    }
}