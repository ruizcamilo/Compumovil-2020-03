package com.example.taller2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton photo, location, contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Contactos(View v){
        Intent intent = new Intent(v.getContext(),ContactosActivity.class);
        startActivity(intent);
    }
    public void Fotos(View v){
        Intent intent = new Intent(v.getContext(),FotosActivity.class);
        startActivity(intent);
    }
    public void Mapa(View v){
        Intent intent = new Intent(v.getContext(),RealMapsActivity.class);
        startActivity(intent);
    }
}