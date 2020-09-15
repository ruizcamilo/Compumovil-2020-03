package com.example.taller1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class BuscarFibonacci extends AppCompatActivity {

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_fibonacci);
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("https://es.wikipedia.org/wiki/Leonardo_de_Pisa");
    }
}