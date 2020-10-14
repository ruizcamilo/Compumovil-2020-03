package com.example.taller2;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION = 1;
    private static final double RADIUS_OF_EARTH_KM = 6371;
    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationProvider;
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;
    EditText mAddress;
    Geocoder mGeocoder;
    LatLng myPosition;
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude= -78.903968;
    public static final double upperRightLatitude= 11.983639;
    public static final double upperRigthLongitude= -71.869905;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initLocation();
        setSensors();
        readAdresses();

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
                            List<Address> addresses = mGeocoder.getFromLocationName( addressString, 2, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRigthLongitude);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap != null) {
                                    mMap.addMarker(new MarkerOptions().position(position).title("Resultado Busqueda").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                    calcularDistancia(position);
                                    return true;
                                }
                            } else {
                                Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    public void initLocation(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
    }

    public void setSensors(){
        System.out.println("ENTRO");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    System.out.println("ENTRO X2");
                    if (event.values[0] < 5000) {
                        Log.i("MAPS", "DARK MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this,R.raw.night));
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.day));
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "porfis, es para el taller", 1);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(geoCoderSearchLatLang(latLng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously*
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Permiso denegado para ver la localización, ahora le mostraré Bogotá", Toast.LENGTH_SHORT).show();
                    LatLng bogota = new LatLng(4.65, -74.05);
                    mMap.addMarker(new MarkerOptions().position(bogota).title("Marcador en Bogotá"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                }
                return;
            }
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationProvider.getLastLocation().addOnSuccessListener(this, new
                    OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.i("LOCATION", "onSuccess location");
                            if (location != null) {
                                Log.i(" LOCATION ", "Longitud: " + location.getLongitude());
                                Log.i(" LOCATION ", "Latitud: " + location.getLatitude());
                                myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(myPosition).title("Estoy aquí"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                            }
                        }
                    });
        }
    }

    public void calcularDistancia(LatLng destino)
    {
        double latDistance = Math.toRadians(myPosition.latitude - destino.latitude);
        double lngDistance = Math.toRadians(myPosition.longitude - destino.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(myPosition.latitude)) * Math.cos(Math.toRadians(destino.latitude)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        Toast.makeText(this, "La distancia de aquí al punto es de: "+Math.round(result*100.0)/100.0+"km.", Toast.LENGTH_SHORT).show();
        crearRuta(myPosition, destino);
    }

    private void crearRuta(LatLng origen, LatLng destino) {
        new TaskDirectionRequest().execute(buildRequestUrl(origen,destino));
    }
    private String buildRequestUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = "AIzaSyAKd8WanILs1dx__3PnfiJkBl296_8hnEc";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key="+APIKEY;
        Log.d("TAG", url);
        return url;
    }

    /**
     * Request direction from Google Direction API
     *
     * @param requestedUrl see {@link #buildRequestUrl(LatLng, LatLng)}
     * @return JSON data routes/direction
     */
    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }

    //Get JSON data from Google Direction
    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            //Json object parsing
            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);
        }
    }


    //Parse JSON Object from Google Direction API & display it on Map
    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }
}