package com.example.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.taller3.R;
import com.example.taller3.adapter.BtnClickListener;
import com.example.taller3.adapter.People;
import com.example.taller3.model.Usuario;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    //FireBase Authentication
    private FirebaseAuth mAuth;

    //FireBase Database
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static final String PATH_USERS="users/";

    //Layout
    private ListView listica;
    private People adapterList;

    //AUX
    List<String> nombres= new ArrayList<>();
    List<String> apellidos= new ArrayList<>();
    List<String> ids= new ArrayList<>();
    List<String> emails= new ArrayList<>();
    List<Double> latitudes= new ArrayList<>();
    List<Double> longitudes= new ArrayList<>();
    List<String> imagenes= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_USERS);
        listica = findViewById(R.id.lista);
        readUsers();
    }

    private void readUsers(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombres.clear();
                apellidos.clear();
                ids.clear();
                emails.clear();
                latitudes.clear();
                longitudes.clear();
                imagenes.clear();
                for(DataSnapshot single: dataSnapshot.getChildren())
                {
                    Usuario myuser = single.getValue(Usuario.class);
                    if(myuser.isActivo()) {
                        nombres.add(myuser.getNombre());
                        apellidos.add(myuser.getApellido());
                        ids.add(myuser.getIdentificacion());
                        emails.add(myuser.getEmail());
                        latitudes.add(myuser.getLatitud());
                        longitudes.add(myuser.getLongitud());
                        imagenes.add(myuser.getImagen());
                    }
                }
                adapterList = new People(ListActivity.this, nombres, apellidos, ids, emails, latitudes, longitudes, imagenes, new BtnClickListener() {
                    @Override
                    public void onBtnClick(int position) {
                        maps(position);
                    }
                });
                listica.setAdapter(adapterList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void maps(int position){
        Intent intent = new Intent(this, Mapa.class);
        Bundle bundle = new Bundle();
        bundle.putInt("codigo", 2);
        bundle.putString("email", emails.get(position));
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }
}