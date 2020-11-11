package com.example.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.taller3.R;
import com.example.taller3.model.LocationT;
import com.example.taller3.services.NotificationJobIntentService;
import com.example.taller3.tasks.RutaTask;
import com.example.taller3.model.Usuario;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    //FireBase Authentication
    private FirebaseAuth mAuth;

    //FireBase Database
    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2;
    private ValueEventListener val;
    public static final String PATH_USERS="users/";
    public static final String PATH_UBIS="locations/";
    public static final String PATH_DISP="disponibles/";

    //Map
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION = 1;
    private static final double RADIUS_OF_EARTH_KM = 6371;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private Geocoder mGeocoder;
    private FusedLocationProviderClient mLocationProvider;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Marker myPositionMarker;
    private LatLng myPosition;
    private Marker usersMarker;
    private LatLng usersPosition;
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude= -78.903968;
    public static final double upperRightLatitude= 11.983639;
    public static final double upperRightLongitude= -71.869905;

    //Notificaciones
    public static String CHANNEL_ID = "Notificaciones";

    //Intent
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_USERS);
        myRef2 = database.getReference(PATH_DISP);
        //Map
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mGeocoder = new Geocoder(getBaseContext());
        //Notificaciones
        createNotificationChannel();
        Intent intent = new Intent(this, NotificationJobIntentService.class);
        NotificationJobIntentService.enqueueWork(this, intent);
        //Permiso
        solicitarPermiso(this, Manifest.permission.ACCESS_FINE_LOCATION, "", LOCATION_PERMISSION);
        //Mi localización
        getMyLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
        if(code == 2) {
            getUsersLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            stopLocationUpdates();
        }
        if(val != null)
        {
            myRef.removeEventListener(val);
        }
    }

    private void startLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mLocationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }
    private void stopLocationUpdates(){
        mLocationProvider.removeLocationUpdates(mLocationCallback);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); //tasa de refresco en milisegundos
        locationRequest.setFastestInterval(5000); //máxima tasa de refresco
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        int choice = bundle.getInt("codigo");
        code = choice;
        switch (choice){
            case 1:
                marcadores();
                break;
            case 2:
                //Localización de la persona que estoy buscando
                getUsersLocation();
                break;
            default:
                Toast.makeText(this,"Hubo error con el Intent", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void getUsersLocation() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        String id = bundle.getString("id");
        myRef=database.getReference(PATH_USERS+id);
        val = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(usersMarker != null) {
                    usersMarker.remove();
                }
                    Usuario myuser = dataSnapshot.getValue(Usuario.class);
                    usersPosition = new LatLng(myuser.getLatitud(), myuser.getLongitud());
                    double distance = calcularDistancia(myPosition, usersPosition);
                    usersMarker = mMap.addMarker(new MarkerOptions().position(usersPosition).title(myuser.getNombre() + " "+ myuser.getApellido()+" - "+ distance+"km.").snippet(geoCoderSearchLatLang(usersPosition)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(usersPosition));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    calcularDistancia(myPosition, usersPosition);
                    Toast.makeText(Mapa.this, "La distancia de aquí al punto es de: "+distance+"km.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public double calcularDistancia(LatLng inicio, LatLng fin)
    {
        double latDistance = Math.toRadians(inicio.latitude - fin.latitude);
        double lngDistance = Math.toRadians(inicio.longitude - fin.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(myPosition.latitude)) * Math.cos(Math.toRadians(fin.latitude)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }

    public void solicitarPermiso(Activity context, String permiso, String justificacion, int idPermiso){
        if(ContextCompat.checkSelfPermission(context,permiso)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context,permiso)){
                Toast.makeText(this,justificacion,idPermiso);
            }
            ActivityCompat.requestPermissions(context,new String[]{permiso}, idPermiso);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocation();
                } else {
                    Toast.makeText(this, "Permiso denegado para ver mi localización", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getGPS();
            mLocationProvider.getLastLocation().addOnSuccessListener(this, new
                    OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.i("LOCATION", "onSuccess location");
                            if (location != null) {
                                myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                if(code == 1) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                }else{
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                }
                            }
                        }
                    });
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    final Location location = locationResult.getLastLocation();
                    if (location != null) {
                        myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        if(myPositionMarker != null) {
                            myPositionMarker.remove();
                        }
                        myPositionMarker = mMap.addMarker(new MarkerOptions().position(myPosition).title("Mi posición").snippet(geoCoderSearchLatLang(myPosition)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        //TODO cambiar mi posicion en la base de datos
                        myRef = database.getReference(PATH_USERS+mAuth.getCurrentUser().getUid());
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Usuario myUser = dataSnapshot.getValue(Usuario.class);
                                myUser.setLatitud(location.getLatitude());
                                myUser.setLongitud(location.getLongitude());
                                myRef.setValue(myUser);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("Mapa", "error en la consulta", databaseError.toException());
                            }
                        });
                    }
                }
            };
        }
    }

    public void getGPS()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse o) {
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {// Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult()
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(Mapa.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private String geoCoderSearchLatLang(LatLng latLng) {
        String address = "";
        try{
            List<Address> Results = mGeocoder.getFromLocation(latLng.latitude,latLng.longitude, 2);
            if(Results != null && Results.size() > 0)
            {
                address = Results.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void crearRuta(LatLng origen, LatLng destino) {
        new RutaTask(this, mMap, origen, destino).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_fire, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.logout){
            mAuth.signOut();
            Intent intent = new Intent(Mapa.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (itemClicked == R.id.estado){
            cambiarEstado();
        }else if (itemClicked == R.id.list){
            Intent intent = new Intent(Mapa.this, ListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void cambiarEstado() {
        myRef = database.getReference(PATH_USERS+mAuth.getCurrentUser().getUid());
        System.out.println("-------------"mAuth.getCurrentUser().getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario myUser = dataSnapshot.getValue(Usuario.class);
                    String id = dataSnapshot.getKey();
                    myRef2=database.getReference(PATH_DISP+id);
                    if(myUser.isActivo()){
                        myRef2.removeValue();

                        Toast.makeText(Mapa.this, "Estado cambiado a inactivo", Toast.LENGTH_SHORT).show();
                        myUser.setActivo(false);
                    }
                    else{
                        myRef2.setValue(myUser.getNombre() + " "+  myUser.getApellido());
                        Toast.makeText(Mapa.this, "Estado cambiado a activo", Toast.LENGTH_SHORT).show();
                        myUser.setActivo(true);
                    }
                    myRef.setValue(myUser);
                    Log.i("Mapa", "Encontró usuario: " + myUser.getNombre());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Mapa", "error en la consulta", databaseError.toException());
            }
        });
    }

    public void marcadores(){
        myRef = database.getReference(PATH_UBIS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    LocationT ubi = singleSnapshot.getValue(LocationT.class);
                    LatLng position = new LatLng(ubi.getLatitude(),ubi.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(position).title(ubi.getName()).snippet(geoCoderSearchLatLang(position)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Mapa", "error en la consulta", databaseError.toException());
            }
        });
    }

    private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones";
            String description = "mostrar el cambio de estado de un usuario";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
    //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
    // Register the channel with the system; you can't change the importance
    // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}