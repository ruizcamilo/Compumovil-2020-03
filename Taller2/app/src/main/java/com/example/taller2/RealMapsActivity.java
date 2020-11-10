package com.example.taller2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.tasks.Tasks.await;

public class RealMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION = 2;
    private static final double RADIUS_OF_EARTH_KM = 6371;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationProvider;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Marker myPositionMarker, myMarker;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;
    private EditText mAddress;
    private Geocoder mGeocoder;
    private LatLng myPosition;
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude= -78.903968;
    public static final double upperRightLatitude= 11.983639;
    public static final double upperRightLongitude= -71.869905;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_maps);
        mLocationRequest = createLocationRequest();
        initLocation();
        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "porfis, es para el taller", LOCATION_PERMISSION);
        getLocation();
        setSensors();
        readAdresses();
    }

    public void initLocation(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        myPositionMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(4.65, -74.05)).title("Marcador en Bogotá"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(4.65, -74.05)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        getLocation();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(myMarker != null) {
                    myMarker.remove();
                }
                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(geoCoderSearchLatLang(latLng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                calcularDistancia(latLng);
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

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getGPS();
            mLocationProvider.getLastLocation().addOnSuccessListener(this, new
                    OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.i("LOCATION", "onSuccess location");
                            if (location != null) {
                                myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                            }
                        }
                    });
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        if(myPositionMarker != null) {
                            myPositionMarker.remove();
                        }
                        myPositionMarker = mMap.addMarker(new MarkerOptions().position(myPosition).title("Mi posición").snippet(geoCoderSearchLatLang(myPosition)).icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                    }
                }
            };
        }
        else {
            requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "porfis, es para el taller", LOCATION_PERMISSION);
        }
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Permiso denegado para ver la localización", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
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

    public void getGPS()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener <LocationSettingsResponse>() {
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
                        // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        try {// Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult()
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(RealMapsActivity.this, REQUEST_CHECK_SETTINGS);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates(); //Se encendió la localización!!!
                } else {
                    Toast.makeText(this, "Sin acceso a localización, hardware deshabilitado!", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); //tasa de refresco en milisegundos
        locationRequest.setFastestInterval(5000); //máxima tasa de refresco
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public void setSensors()
    {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 4000) {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(RealMapsActivity.this,R.raw.night));
                    } else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(RealMapsActivity.this, R.raw.day));
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }

    public void readAdresses(){
        mGeocoder = new Geocoder(getBaseContext());
        mAddress = findViewById(R.id.addressText);
        mAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    String addressString = mAddress.getText().toString();
                    if (!addressString.isEmpty()) {
                        try {
                            List<Address> addresses = mGeocoder.getFromLocationName( addressString, 2, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap != null) {
                                    if(myMarker != null) {
                                        myMarker.remove();
                                    }
                                    myMarker = mMap.addMarker(new MarkerOptions().position(position).title(geoCoderSearchLatLang(position)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                    calcularDistancia(position);
                                    return true;
                                }
                            } else {
                                Toast.makeText(RealMapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(RealMapsActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    public void calcularDistancia(LatLng destino)
    {
        double latDistance = Math.toRadians(myPosition.latitude - destino.latitude);
        double lngDistance = Math.toRadians(myPosition.longitude - destino.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(myPosition.latitude)) * Math.cos(Math.toRadians(destino.latitude)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        crearRuta(myPosition, destino);
        Toast.makeText(this, "La distancia de aquí al punto es de: "+Math.round(result*100.0)/100.0+"km.", Toast.LENGTH_LONG).show();
    }

    private void crearRuta(LatLng origen, LatLng destino) {
        mMap.clear();
        myPositionMarker = mMap.addMarker(new MarkerOptions().position(myPosition).title("Mi posición").snippet(geoCoderSearchLatLang(myPosition)).icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
        new RutaTask(this, mMap, origen, destino).execute();
    }

}