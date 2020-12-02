package com.example.taller3.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taller3.R;
import com.example.taller3.activities.MainActivity;
import com.example.taller3.activities.Mapa;
import com.example.taller3.adapter.People;
import com.example.taller3.model.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationJobIntentService extends JobIntentService {
    private static final int JOB_ID = 12;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference availables, myRef;
    public static String CHANNEL_ID = "Notificaciones";
    public static final String PATH_AVAILABLE="disponibles/";
    private Map<String, Boolean> disponibles;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotificationJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        availables = firebaseDatabase.getReference(PATH_AVAILABLE);
        disponibles = new ConcurrentHashMap<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent){
        availables.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren())
                {
                    disponibles.put(d.getKey(), true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        availables.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null)
                {
                    Map<String, Boolean> llega = new ConcurrentHashMap<>();
                    for(DataSnapshot d: dataSnapshot.getChildren())
                    {
                        llega.put(d.getKey(), true);
                    }
                    if (disponibles.size() < llega.size())
                    {
                        for (Map.Entry<String, Boolean> entry: llega.entrySet())
                        {
                            if(!disponibles.containsKey(entry.getKey()))
                            {
                                disponibles.put(entry.getKey(), true);
                                BuildNotification(entry.getKey());
                            }
                        }
                    }
                    else{
                        for (Map.Entry<String, Boolean> entry: disponibles.entrySet())
                        {
                            if(!llega.containsKey(entry.getKey()))
                            {
                                disponibles.remove(entry.getKey());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void BuildNotification(String id)
    {
        myRef=firebaseDatabase.getReference(Mapa.PATH_USERS+id);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario myuser = dataSnapshot.getValue(Usuario.class);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationJobIntentService.this, CHANNEL_ID);
                mBuilder.setSmallIcon(R.drawable.bell);
                mBuilder.setContentTitle("Nuevo Usuario disponible");
                mBuilder.setContentText(myuser.getNombre() +" "+myuser.getApellido()+" se acabada conectar. Haz click acá para ver su ubicación.");
                mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                //Acción asociada a la notificación
                Intent intent = new Intent(NotificationJobIntentService.this, Mapa.class);
                Bundle bundle = new Bundle();
                bundle.putInt("codigo", 2);
                System.out.println("-------------------"+dataSnapshot.getKey());
                bundle.putString("id", dataSnapshot.getKey());
                intent.putExtra("bundle", bundle);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationJobIntentService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setAutoCancel(true); //Remueve la notificación cuando se toca

                int notificationId = 001;
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationJobIntentService.this);
                notificationManager.notify(notificationId, mBuilder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}