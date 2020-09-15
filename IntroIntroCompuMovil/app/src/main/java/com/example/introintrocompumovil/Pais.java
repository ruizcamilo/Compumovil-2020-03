package com.example.introintrocompumovil;

public class Pais {

    private String capital;
    private String nombre;
    private String nombre_int;
    private String siglas;

    public Pais(String capital, String nombre, String nombre_int, String siglas) {
        this.capital = capital;
        this.nombre = nombre;
        this.nombre_int = nombre_int;
        this.siglas = siglas;
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

    @Override
    public String toString() {
        return "Pais{" +
                "capital='" + capital + '\'' +
                ", nombre='" + nombre + '\'' +
                ", nombre_int='" + nombre_int + '\'' +
                ", siglas='" + siglas + '\'' +
                '}';
    }

}
