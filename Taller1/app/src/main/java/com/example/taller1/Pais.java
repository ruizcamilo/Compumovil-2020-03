package com.example.taller1;

public class Pais {

    private String capital;
    private String nombre;
    private String nombre_int;
    private String siglas;
    private String URLbandera;

    public Pais(String capital, String nombre, String nombre_int, String siglas, String url) {
        this.capital = capital;
        this.nombre = nombre;
        this.nombre_int = nombre_int;
        this.siglas = siglas;
        this.URLbandera=url;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre_int() {
        return nombre_int;
    }

    public void setNombre_int(String nombre_int) {
        this.nombre_int = nombre_int;
    }

    public String getSiglas() {
        return siglas;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }

    public String getURLbandera() {
        return URLbandera;
    }

    public void setURLbandera(String URLbandera) {
        this.URLbandera = URLbandera;
    }
    @Override
    public String toString() {
        return nombre;
    }
}
