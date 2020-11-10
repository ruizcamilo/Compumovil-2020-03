package com.example.taller3;

import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class Usuario {

    private String nombre;
    private String apellido;
    private String email;
    private String identificacion;
    private LatLng ubicacion;
    private boolean activo;


    public Usuario(String nombre, String apellido, String email, String identificacion, LatLng ubicacion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.identificacion = identificacion;
        this.ubicacion = ubicacion;
        this.activo = false;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isActivo() { return activo; }

    public void setActivo(boolean activo) { this.activo = activo; }
}
