package org.cuatrovientos.wordle.model;

import android.provider.CalendarContract;

import org.cuatrovientos.wordle.app.MyApplication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Palabra extends RealmObject {

    @PrimaryKey
    private int id;
    private String palabraStr;
    private RealmList<String> letraList;
    private RealmList<Integer> letraResList;
    private boolean adivinable;
    private boolean show;

    //Getters y Setters
    public Palabra() {
    }

    public Palabra(String palabra) {
        this.id = MyApplication.palabraID.incrementAndGet();
        this.palabraStr = palabra.toUpperCase();

    }

    public Palabra(String palabra, boolean adivinable) {
        this.id = MyApplication.palabraID.incrementAndGet();
        this.palabraStr = palabra.toUpperCase();
        this.adivinable = adivinable;
    }

    public Palabra(String palabra, boolean adivinable, boolean show) {
        this.id = MyApplication.palabraID.incrementAndGet();
        this.palabraStr = palabra.toUpperCase();
        this.adivinable = adivinable;
        this.show = show;
    }

    public Palabra(String palabra, boolean adivinable, boolean show, RealmList<Integer> letraRes) {
        this.id = MyApplication.palabraID.incrementAndGet();
        this.palabraStr = palabra.toUpperCase();
        this.letraResList = letraRes;
        this.adivinable = adivinable;
        this.show = show;
    }

    public String getPalabraStr() {
        return palabraStr;
    }

    public void setPalabraStr(String palabraStr) {
        this.palabraStr = palabraStr;
        LetrasPalabra();
    }

    public RealmList<String> getLetraList() {
        RealmList<String> lets = new RealmList<String>();
        for (String s :this.getPalabraStr().split("")){lets.add(s);};
        return lets;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public RealmList<Integer> getLetraResList() {
        return letraResList;
    }

    public void setLetraResList(RealmList<Integer> letraResList) {
        this.letraResList = letraResList;
    }

    //Metodo para separar el string en las 5 letras
    private void LetrasPalabra() {
        String[] letras = this.palabraStr.split("");
        for (int i = 0; i < letras.length; i++) {
            this.letraList.add(letras[i]);
        }
    }

    //Metodo para escribir una letra
    public void EscLetra(String let) {
        setPalabraStr(this.palabraStr.replaceFirst(" ", let));
    }

    //Metodo para borrar una letra
    private String replaceLast(String str, String old, String neu) {
        String miStr = "";
        for (int i = str.length() - 1; i > -1; i--) {
            miStr += str.charAt(i);
        }
        miStr = miStr.replaceFirst(old, neu);
        str = "";
        for (int i = miStr.length() - 1; i > -1; i--) {
            str += miStr.charAt(i);
        }
        return str;
    }

    public void DelLetra() {
        if (palabraStr.indexOf(" ") == 0) {
            return;
        }
        if (this.palabraStr.contains(" ")) {
            setPalabraStr(replaceLast(palabraStr, String.valueOf(palabraStr.charAt(palabraStr.indexOf(" ") - 1)), " "));
            //setPalabraStr(palabraStr.replaceFirst(String.valueOf(palabraStr.charAt(palabraStr.indexOf(" ")-1))," "));
        } else {
            setPalabraStr(replaceLast(palabraStr, String.valueOf(palabraStr.charAt(palabraStr.length() - 1)), " "));
            //setPalabraStr(palabraStr.replaceFirst(String.valueOf(palabraStr.charAt(palabraStr.length()-1))," "));
        }

    }


}
