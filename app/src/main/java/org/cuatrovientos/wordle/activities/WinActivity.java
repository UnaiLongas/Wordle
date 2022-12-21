package org.cuatrovientos.wordle.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cuatrovientos.wordle.R;
import org.cuatrovientos.wordle.adapter.RecyclerDataAdapterPuntuacion;
import org.cuatrovientos.wordle.model.Puntuacion;
import org.cuatrovientos.wordle.utils.MyUtils;

import java.nio.channels.Channel;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WinActivity extends AppCompatActivity {


    private static final String CHANNEL_ID = "patata";

    ImageView imgResults;
    TextView txtResults;
    TextView txtPalResult;
    FloatingActionButton fab, fabDN;

    NotificationManager notificationManager;
    Integer notId;

    RecyclerDataAdapterPuntuacion recyclerDataAdapterPuntuacion;

    public boolean isNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myChannel";
            String description = "He creado este canal para mostrar notificaciones";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        imgResults = (ImageView) findViewById(R.id.imgResult);
        txtResults = (TextView) findViewById(R.id.txtResult);
        txtPalResult = (TextView) findViewById(R.id.txtPalResult);
        fab = (FloatingActionButton) findViewById(R.id.fabReturn);
        fabDN = (FloatingActionButton) findViewById(R.id.fabDN);

        if (isNightMode(WinActivity.this)) {
            fabDN.setImageResource(R.drawable.ic_sun_white_24);
        } else {
            fabDN.setImageResource(R.drawable.ic_moon_black_24);
        }
        fabDN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNightMode(WinActivity.this)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    fabDN.setImageResource(R.drawable.ic_moon_black_24);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    fabDN.setImageResource(R.drawable.ic_sun_white_24);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer puntID = (Integer) extras.get("puntuacionID");
            Boolean winned = (Boolean) extras.get("winned");
            String palabraRes = (String) extras.get("palabraRes");

            //Realm realm = Realm.getDefaultInstance();
            //realm.beginTransaction();
            //Puntuacion puntuacion = realm.where(Puntuacion.class).equalTo("id",puntID).findFirst();
            //realm.commitTransaction();

            txtPalResult.setText("La palabra es: " + palabraRes);

            if (winned) {
                txtResults.setText(R.string.resWin);
                if (isNightMode(WinActivity.this)) {
                    imgResults.setBackgroundResource(R.drawable.ic_trofeo_white_24);
                } else {
                    imgResults.setBackgroundResource(R.drawable.ic_trofeo_24);
                }


            } else {
                txtResults.setText(R.string.resLoose);
                if (isNightMode(WinActivity.this)) {
                    imgResults.setBackgroundResource(R.drawable.ic_cruz_white_24);
                } else {
                    imgResults.setBackgroundResource(R.drawable.ic_cruz_24);
                }
            }


        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvPuntuaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        RealmResults<Puntuacion> realmResultPuntuaciones = realm.where(Puntuacion.class).sort("puntos", Sort.DESCENDING).findAll();

        realm.commitTransaction();

        realmResultPuntuaciones.addChangeListener(new RealmChangeListener<RealmResults<Puntuacion>>() {
            @Override
            public void onChange(RealmResults<Puntuacion> puntuaciones) {
                recyclerDataAdapterPuntuacion.notifyDataSetChanged();
            }
        });

        recyclerDataAdapterPuntuacion = new RecyclerDataAdapterPuntuacion(realmResultPuntuaciones);

        recyclerView.setAdapter(recyclerDataAdapterPuntuacion);

        if (isNightMode(WinActivity.this)) {
            fab.setImageResource(R.drawable.ic_baseline_chevron_left_24);
        } else {
            fab.setImageResource(R.drawable.ic_arrow_back_24);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WinActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        createNotificationChannel();



        if(notId == null){notId = 0;}else{notId += 1;}

        //NotificationCompat.Builder notificacion = MyUtils.genNotificationSimple(this,CHANNEL_ID,"Felicidades!","Reta a tus amigos a resolver la palábra del dia",R.mipmap.ic_wordle2_foreground);
        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_wordle2_foreground)
                .setContentTitle("Felicidades!")
                .setContentText("Reta a tus amigos a resolver la palábra del dia")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        notificationManager.notify(notId, notificacion.build());

        notId+=1;

        Intent intent = new Intent(WinActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_wordle2_foreground)
                .setContentTitle("Felicidades!")
                .setContentText("Reta a tus amigos a resolver la palábra del dia")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(notId, notificacion.build());

        notId+=1;

        Intent intent2 = new Intent(WinActivity.this, MainActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, intent2, 0);

        notificacion = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_wordle2_foreground)
                .setContentTitle("Felicidades!")
                .setContentText("Reta a tus amigos a resolver la palábra del dia")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.btnlet_layout_grey, "REPLAY",pendingIntent2);

        notificationManager.notify(notId, notificacion.build());



    }
}