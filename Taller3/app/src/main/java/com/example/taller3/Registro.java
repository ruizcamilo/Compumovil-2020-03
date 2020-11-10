package com.example.taller3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.io.FileNotFoundException;

public class Registro extends AppCompatActivity {

    public static final String PATH_USERS="users/";
    public static final int IMAGE_PICKER_REQUEST= 1 ;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;

    private Usuario user;

    private EditText nombre,apellido,email,contraseña,identificacion;
    private LatLng ubicacion;
    private Button foto;

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

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickImage = new Intent(Intent.ACTION_PICK);
                pickImage.setType("image/*");
                startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);;
            }
        });
    }

    private void registerUser(String mail, String password) {
        if (validateForm()){
            user = new Usuario(nombre.getText().toString(),apellido.getText().toString(),email.getText().toString(),identificacion.getText().toString(),ubicacion);
            mAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String key = myRef.push().getKey();
                                myRef=database.getReference(PATH_USERS+key);
                                myRef.setValue(user);
                                Log.d("Sign Up", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                String imgPath = uploadFile();
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user!=null){ //Update user Info
                                    UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                    upcrb.setDisplayName(nombre.getText().toString());
                                    upcrb.setPhotoUri(Uri.parse(imgPath));
                                    user.updateProfile(upcrb.build());
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
            intent.putExtra("user", currentUser.getEmail());
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
            valid = false;
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
                }
        }
    }
}
