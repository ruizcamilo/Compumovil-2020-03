package com.example.introintrocompumovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
    EditText n;
    Button clickMe;
    Button clickMeFX;
    TextView titulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Inflate
        clickMe = findViewById(R.id.clickMe);
        titulo = findViewById(R.id.titulo);
        clickMeFX = findViewById(R.id.clickMeFX);
        n = findViewById(R.id.nombre);

        clickMe.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), MainActivity.class));
                titulo.setText("Hola Mundo");
                Toast.makeText(getBaseContext(), "Hola Mundo", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void pika(View view)
    {
        Toast.makeText(this, "Hola Pika", Toast.LENGTH_SHORT).show();
        Intent in = new Intent(this, MainActivity.class);
        in.putExtra("mensaje", n.getText().toString());
        startActivity(in);
    }
}