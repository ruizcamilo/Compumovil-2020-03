package com.example.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taller3.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Registro extends AppCompatActivity {

    public static final String PATH_USERS="users/";
    public static final int IMAGE_PICKER_REQUEST= 1 ;
    public static final int IMAGE_PICKER_ID = 2;
    public static final int MAP_PICKER_REQUEST = 3;
    public static final int MAP_PICKER_ID = 4;
    private static final int REQUEST_CHECK_SETTINGS = 5;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;

    private FusedLocationProviderClient mLocationProvider;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private Usuario myUser;

    private EditText nombre,apellido,email,contraseña,identificacion;
    private TextView tfoto, tubi;
    private LatLng ubicacion;
    private Button foto, maps, register;

    private Uri imagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        myRef = database.getReference(PATH_USERS);

        this.ubicacion = null;
        this.imagen = null;

        this.nombre = findViewById(R.id.nombreText);
        this.apellido =findViewById(R.id.apellidoText);
        this.email = findViewById(R.id.emailText);
        this.contraseña = findViewById(R.id.contraseñaText);
        this.identificacion = findViewById(R.id.idText);
        this.foto = findViewById(R.id.imgButton);
        this.maps = findViewById(R.id.ubiButton);
        this.register = findViewById(R.id.boton_registrarse);
        this.tfoto = findViewById(R.id.imgText);
        this.tubi = findViewById(R.id.ubiText);

        this.mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        this.mLocationRequest = new LocationRequest();

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarPermiso(Registro.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Necesito acceder al storage", IMAGE_PICKER_ID);
                selecImagen();
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarPermiso(Registro.this, Manifest.permission.ACCESS_FINE_LOCATION, "Necesito acceder a su ubicación", MAP_PICKER_ID);
                getMyLocation();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(email.getText().toString(), contraseña.getText().toString());
                System.out.println("SUPUESTAMENTE YA");
            }
        });
    }

    private void registerUser(String mail, String password) {
        if (validateForm()){
            mAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("Sign Up", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user!=null){ //Update user Info
                                    UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                    upcrb.setDisplayName(nombre.getText().toString());
                                    upcrb.setPhotoUri(imagen);
                                    user.updateProfile(upcrb.build());
                                    String path = uploadFile();
                                    myUser = new Usuario(nombre.getText().toString(),apellido.getText().toString(),email.getText().toString(),identificacion.getText().toString(),path,ubicacion);
                                    myRef=database.getReference(PATH_USERS+user.getUid());
                                    myRef.setValue(myUser);
                                    updateUI(user);
                                }

                            }
                            if (!task.isSuccessful()) {
                                Toast.makeText(Registro.this, "Auth failed"+ task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                                Log.e("Sign Up", task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(getBaseContext(), Mapa.class);
            Bundle bundle = new Bundle();
            bundle.putInt("codigo", 1);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String name = nombre.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nombre.setError("Required.");
            valid = false;
        } else {
            nombre.setError(null);
        }
        String lastname = apellido.getText().toString();
        if (TextUtils.isEmpty(lastname)) {
            apellido.setError("Required.");
            valid = false;
        } else {
            apellido.setError(null);
        }
        String mail = email.getText().toString();
        if (TextUtils.isEmpty(mail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }
        String pass = contraseña.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            contraseña.setError("Required.");
            valid = false;
        } else {
            contraseña.setError(null);
        }
        String id = identificacion.getText().toString();
        if (TextUtils.isEmpty(id)) {
            identificacion.setError("Required.");
            valid = false;
        } else {
            identificacion.setError(null);
        }
        if (ubicacion==null){
            ubicacion = new LatLng(75.0,24.0);
        }
        if (imagen == null){
            valid = false;
        }
        return valid;
    }

    private String uploadFile(){
        File f = new File(imagen.getPath());
        String imageName = f.getName();
        String path = "images/profile/"+mAuth.getUid()+"/"+ imageName;
        StorageReference imageRef = mStorageRef.child(path);
        imageRef.putFile(imagen)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("Sign Up", "Succesfully upload image");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
        return path;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    this.imagen = data.getData();
                    File f = new File(imagen.getPath());
                    this.tfoto.setText(f.getName());
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
                                ubicacion = new LatLng(location.getLatitude(), location.getLongitude());
                                tubi.setText(ubicacion.toString());
                            }
                        }
                    });
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        ubicacion = new LatLng(location.getLatitude(), location.getLongitude());
                        tubi.setText(ubicacion.toString());
                    }
                }
            };
        }
        else{
            ubicacion = null;
        }
    }

    public void selecImagen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent selecImagen = new Intent(Intent.ACTION_PICK);
            selecImagen.setType("image/*");
            startActivityForResult(selecImagen, IMAGE_PICKER_REQUEST);
        }
    }

    public void solicitarPermiso(Activity context, String permiso, String justificacion, int idPermiso){
        if(ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)){
                Toast.makeText(this, justificacion, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idPermiso);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case IMAGE_PICKER_ID:
                selecImagen();
                break;
            case MAP_PICKER_ID:
                getMyLocation();
                break;
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
                            resolvable.startResolutionForResult(Registro.this, REQUEST_CHECK_SETTINGS);
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
}
