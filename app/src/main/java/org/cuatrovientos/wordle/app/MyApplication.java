package org.cuatrovientos.wordle.app;

import android.app.Application;

import org.cuatrovientos.wordle.model.Palabra;
import org.cuatrovientos.wordle.model.Puntuacion;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {

    public static AtomicInteger palabraID = new AtomicInteger();
    public static AtomicInteger puntuacionID = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();

        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        palabraID = getIdByTable(realm, Palabra.class);
        puntuacionID = getIdByTable(realm, Puntuacion.class);
        //Aqui irian la demas tablas si las hubiera
        //OtracosaID = getIdByTable(realm, Tarjeta.class);
        realm.close();
    }

    private void setUpRealmConfig(){
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }

    private <T extends RealmObject> AtomicInteger getIdByTable (Realm realm, Class<T>anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();

        if (results.size()>0){
            return new AtomicInteger(results.max("id").intValue());
        }
        else{
            return new AtomicInteger(0);
        }

    }
}
