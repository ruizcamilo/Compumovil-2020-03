package com.example.taller3.model;

import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class Usuario {

    private String nombre;
    private String apellido;
    private String email;
    private String identificacion;


    private Double latitud;
    private Double longitud;
    private boolean activo;

    public Usuario(){

    }
    public Usuario(String nombre, String apellido, String email, String identificacion, Double latitud, Double longitud) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.identificacion = identificacion;
        this.latitud = latitud;
        this.longitud = longitud;
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

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
