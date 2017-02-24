package com.example.chus.aplicaciontrivial.logica;

/**
 * Created by Chus on 07/01/2017.
 */
public class Puntuacion implements Comparable<Puntuacion>{

    private String nombreUsuario;
    private Integer puntos;

    public Puntuacion(String nombreUsuario, Integer puntos) {
        this.nombreUsuario = nombreUsuario;
        this.puntos = puntos;
    }

    @Override
    public String toString() {
        return "Usuario:" + getNombreUsuario();
    }

    public Puntuacion() {
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    @Override
    public int compareTo(Puntuacion puntosComparar) {
        int comp = ((Puntuacion) puntosComparar).getPuntos();
        return comp - this.puntos;

    }
}

