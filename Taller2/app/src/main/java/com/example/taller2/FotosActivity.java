package com.example.taller2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FotosActivity extends AppCompatActivity {
    private static final int IMAGE_PICKER_REQUEST = 1, REQUEST_IMAGE_CAPTURE=2, OUR_MULTIPLE_REQUESTS=3;
    Button imagenBut, camaraBut;
    ImageView imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos);
        requestMyPermissions();
    }

    private void requestMyPermissions() {
        imagenBut=findViewById(R.id.imagen);
        camaraBut=findViewById(R.id.camara);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if(permissionStorage != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }else{
            imagenBut.setEnabled(true);
        }
        if(permissionCamera != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }else{
            camaraBut.setEnabled(true);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),OUR_MULTIPLE_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        imagenBut = findViewById(R.id.imagen);
        camaraBut = findViewById(R.id.camara);
        switch (requestCode) {
            case OUR_MULTIPLE_REQUESTS: {
                if (grantResults.length > 0) {
                    for(int i=0; i < permissions.length; i++){
                    //for (String per : permissions) {
                        System.out.println(permissions[i]+" "+grantResults[i]);
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            if(permissions[i].equals("android.permission.READ_EXTERNAL_STORAGE"))
                            {
                                imagenBut.setEnabled(true);
                            }
                            if(permissions[i].equals("android.permission.CAMERA")) {
                                System.out.println("entro!");
                                camaraBut.setEnabled(true);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public void selectImage(View v){
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
    }

    public void takePhoto(View v)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagen = findViewById(R.id.selectedPhoto);
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imagen.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imagen.setImageBitmap(imageBitmap);
                }
        }
    }
}