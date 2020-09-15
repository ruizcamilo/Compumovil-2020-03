package com.example.introintrocompumovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    String [] arreglo;
    ArrayList<Pais> paises;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        JSONObject json = null;
        try {
            json = new JSONObject(loadJSONFromAsset());
            JSONArray paisesJsonArray = json.getJSONArray("paises");
            paises = new ArrayList<Pais>();
            for(int i=0;i<paisesJsonArray.length();i++) {
                JSONObject jsonObject = paisesJsonArray.getJSONObject(i);
                String capital = jsonObject.getString("capital");
                String nombre = jsonObject.getString("nombre_pais");
                String nombreint = jsonObject.getString("nombre_pais_int");
                String sig = jsonObject.getString("sigla");
                Pais p = new Pais(capital, nombre, nombreint, sig);
                paises.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<Pais> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, paises);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l)
            {
                Pais pais = (Pais) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getBaseContext(), DetailActivity.class);
                intent.putExtra("capital", pais.getCapital());
                intent.putExtra("nombre", pais.getNombre());
                intent.putExtra("nombre_int", pais.getNombre_int());
                intent.putExtra("sigla", pais.getSiglas());
                startActivity(intent);
            }
        });
    }

    private void initArray(){
        arreglo = new String[30];
        for (int i = 0; i < arreglo.length; i++)
            if (i % 2 == 0)
                arreglo[i] = "hola";
            else
                arreglo[i] = "mundo";
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("paises.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}