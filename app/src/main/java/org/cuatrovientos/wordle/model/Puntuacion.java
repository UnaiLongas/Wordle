package org.cuatrovientos.wordle.model;

import org.cuatrovientos.wordle.app.MyApplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Puntuacion extends RealmObject {
    @PrimaryKey
    private int id;
    private boolean winned;
    private String jugador;
    private int puntos;
    private Date fecha;


    public Puntuacion() {
    }

    public Puntuacion(String jugador,int puntos,boolean winned) {
        this.id = MyApplication.puntuacionID.incrementAndGet();
        this.winned = winned;
        this.jugador = jugador;
        this.puntos = puntos;
        this.fecha = new Date();
    }

    public int getId() {
        return id;
    }

    public boolean isWinned() {return winned;}

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public Date getFecha() {return fecha;}
}
