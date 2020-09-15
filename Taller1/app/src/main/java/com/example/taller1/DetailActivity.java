package com.example.taller1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    TextView capital, nombre, nombre_int, sigla;
    ImageView bandera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        try {
            mostrarInfo(getIntent());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void mostrarInfo(Intent in) throws IOException, InterruptedException {
        bandera=findViewById(R.id.imageView3);
        nombre=findViewById(R.id.nombre);
        capital=findViewById(R.id.capital);
        nombre_int=findViewById(R.id.nomInt);
        sigla=findViewById(R.id.sigla);

        final String urlbandera = in.getStringExtra("URLbandera");
        final String nombrepais = in.getStringExtra("nombre");
        final String nombreint = in.getStringExtra("nombre_int");
        final String sig = in.getStringExtra("sigla");
        final String cap = in.getStringExtra("capital");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Bitmap bmp = BitmapFactory.decodeStream((InputStream)new URL(urlbandera).getContent());
                    bandera.setImageBitmap(bmp);
                    nombre.setText(nombrepais);
                    capital.setText(cap);
                    nombre_int.setText(nombreint);
                    sigla.setText(sig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();


    }
}