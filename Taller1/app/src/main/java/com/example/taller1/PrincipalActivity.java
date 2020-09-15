package com.example.taller1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrincipalActivity extends AppCompatActivity {

    Button fibo, facto, pais;
    Spinner spin;
    EditText posicion;
    TextView cFibo, hFibo, cFacto, hFacto;

    int countFibo=0, countFacto=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        fibo = findViewById(R.id.fibonacci);
        posicion = findViewById(R.id.posiciones);
        fibo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(posicion.getText().toString().matches("")) {
                    Toast.makeText(view.getContext(), "Ingrese un valor en posición", Toast.LENGTH_SHORT).show();
                }else{
                    countFibo++;
                    updateFiboLog();
                    Intent intentA = new Intent(getBaseContext(), FibonacciActivity.class);
                    intentA.putExtra("posicion", posicion.getText().toString());
                    startActivity(intentA);
                }
            }
        });
        facto = findViewById(R.id.factorial);
        spin = findViewById(R.id.spinner);
        facto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                countFacto++;
                updateFactoLog();
                Intent intentB = new Intent(getBaseContext(),FactorialActivity.class);
                intentB.putExtra ("spin", spin.getSelectedItem().toString());
                startActivity(intentB);
            }
        });
        pais = findViewById(R.id.paises);
        pais.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(),PaisesActivity.class));
            }
        });
        cFibo=findViewById(R.id.fiboCount);
        cFibo.setText("Fibonacci log: "+countFibo);
        cFacto=findViewById(R.id.factoCount);
        cFacto.setText("Factorial log: "+countFacto);
        hFibo=findViewById(R.id.fiboHour);
        hFibo.setText("Ultimo Fibonacci: No registrado");
        hFacto=findViewById(R.id.factoHour);
        hFacto.setText("Ultimo Facto: No registrado");

    }
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
    }

    public void updateFiboLog()
    {
        SimpleDateFormat formatter= new SimpleDateFormat("'El' dd-MM-yy 'a las' HH:mm");
        Date date = new Date(System.currentTimeMillis());
        cFibo=findViewById(R.id.fiboCount);
        cFibo.setText("Fibonacci log: "+countFibo);
        hFibo=findViewById(R.id.fiboHour);
        hFibo.setText("Ultimo Fibonacci: "+ formatter.format(date));
    }
    public void updateFactoLog()
    {
        SimpleDateFormat formatter= new SimpleDateFormat("'El' dd-MM-yy 'a las' HH:mm");
        Date date = new Date(System.currentTimeMillis());
        cFacto=findViewById(R.id.factoCount);
        cFacto.setText("Factorial log: "+countFacto);
        hFacto=findViewById(R.id.factoHour);
        hFacto.setText("Ultimo Facto: "+formatter.format(date));
    }
}