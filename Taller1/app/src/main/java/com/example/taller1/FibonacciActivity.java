package com.example.taller1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FibonacciActivity extends AppCompatActivity {

    ImageView imgFibo;
    ListView respuesta;
    List<Double> fibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fibonacci);
        imgFibo = findViewById(R.id.imageView);
        respuesta = findViewById(R.id.espacio);
        Intent a = getIntent();
        double pos = Double.parseDouble(a.getStringExtra("posicion"));
        fibo=new ArrayList<Double>();
        try{
            double res = Fibonacci(pos);
            ArrayAdapter<Double> adapter = new ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1, fibo);
            respuesta.setAdapter(adapter);
        }catch(OutOfMemoryError e)
        {
            Toast.makeText(this, "No se puede calcular un número tan grande", Toast.LENGTH_SHORT).show();
        }
    }



    public double Fibonacci(double n)
    {
        fibo.add(0.0);
        if(n == 0) {
            return n;
        }
        fibo.add(1.0);
        if(n == 1) {
            return n;
        }
        double fib = 1;
        double prevFib = 1;
        fibo.add(fib);
        for(double i=2; i<n; i++) {
            double temp = fib;
            fib+= prevFib;
            prevFib = temp;
            fibo.add(fib);
        }
        return fib;
    }

    public void searchFibonacci(View v)
    {
        Intent intent = new Intent(v.getContext(), BuscarFibonacci.class);
        startActivity(intent);
    }
}