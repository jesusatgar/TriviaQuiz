package com.example.chus.aplicaciontrivial.logica;

import android.net.Uri;

/**
 * Created by Chus on 26/12/2016.
 */

public class Pregunta {

    public String pregunta;
    public String respuesta;
    public String uriAudio;
    public String uriImagen;
    public String[] todasRespuestas;
    public boolean reproducible;
    public boolean reconVoz;
    public boolean preguntaLocalizacion;



    public Pregunta(String pregunta, String respuesta,boolean reproducible,boolean reconVoz,
                    String[] todasRespuestas,String uriAudio,boolean preguntaLocalizacion,String uriImagen) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.reproducible = reproducible;
        this.reconVoz = reconVoz;
        this.todasRespuestas = todasRespuestas;
        this.uriAudio = uriAudio;
        this.uriImagen = uriImagen;
        this.preguntaLocalizacion = preguntaLocalizacion;
    }

    public String getPregunta() {
        return pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public boolean getReproducible(){ return reproducible;}

    public boolean isReconVoz() {
        return reconVoz;
    }

    public String getUriAudio(){ return uriAudio ; }

    public String getUriImagen(){ return uriImagen ; }

    public String[] getTodasRespuestas() {
        return todasRespuestas;
    }

    public boolean esRespuestaValida(String respuestaElegida){ return (respuestaElegida.equals(getRespuesta())); }

    public boolean isLocalizacion() { return preguntaLocalizacion; }
}
