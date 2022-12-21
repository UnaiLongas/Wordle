package org.cuatrovientos.wordle.activities;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cuatrovientos.wordle.R;
import org.cuatrovientos.wordle.adapter.RecyclerDataAdapter;
import org.cuatrovientos.wordle.data.data;
import org.cuatrovientos.wordle.model.Palabra;
import org.cuatrovientos.wordle.model.Puntuacion;
import org.cuatrovientos.wordle.utils.MyUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    final Integer TOTALPALABRAS = 6314;
    final Integer VALORAVERDE = 25;
    final Integer VALORANARANJA = 10;
    final Integer VALORAGRIS = 0;
    final Integer VALORINTENTO = 100;

    RealmResults<Palabra> realmResultPalabras;
    Palabra palRes;
    FloatingActionButton fabDN;
    EditText txtPlayer;
    Button btnSave, btnCancel,btnCancelar,btnConfirm;
    List<Button> teclado;
    Button btnChk, btnDel, btnA, btnB, btnC, btnD, btnE, btnF, btnG, btnH, btnI, btnJ, btnK, btnL,
            btnM, btnN, btnNN, btnO, btnP, btnQ, btnR, btnS, btnT, btnU, btnV, btnW, btnX, btnY, btnZ;

    RecyclerDataAdapter recyclerDataAdapter;
    Realm realm;

    Instant ini;
    Instant fin;


    //Metodos de validacion
    private boolean VldMismaLetra(Palabra palabra) {
        String pal = palabra.getPalabraStr();
        for (int i = 0; i < pal.length(); i++) {
            for (int j = 0; j < pal.length(); j++) {
                if (pal.charAt(i) == pal.charAt(j) && i != j) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean VldPalabraEnDict(Palabra palabra) {
        //realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Palabra pal = realm.where(Palabra.class)
                .equalTo("palabraStr", palabra.getPalabraStr()).and()
                .equalTo("adivinable", false).and()
                .equalTo("show", false)
                .findFirst();
        realm.commitTransaction();
        //realm.close();
        return pal != null;

    }

    private boolean VldPalabraCompleta(Palabra palabra) {
        return !palabra.getPalabraStr().contains(" ");
    }

    public String EsValida(Palabra pal) {
        //if(!VldMismaLetra() || !VldPalabraEnDict()){return false;}else{return true;}
        String err;
        if (!VldPalabraCompleta(pal)) {
            err = "La palabra introducida no esta completa";
            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG);
            return err;
        } else if (!VldMismaLetra(pal)) {
            err = "La palabra no puede contener letras repetidas";
            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG);
            return err;
        } else if (!VldPalabraEnDict(pal)) {
            err = "La palabra introducida no existe";
            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG);
            return err;
        } else {
            return "";
        }
    }

    //Calcular puntos
    @RequiresApi(api = Build.VERSION_CODES.O)
    // No entiendo por que pero me exige que ponga esta linea para que funcione el duration
    private Integer CalcPuntos() {
        Integer puntos = 0;

        //Puntos por letras acertadas(en la posicion o no)
        RealmResults<Palabra> palabras = realm.where(Palabra.class).equalTo("show", true).findAll();
        for (Palabra pal : palabras) {
            for (Integer num : pal.getLetraResList()) {
                switch (num) {
                    case 0:
                        puntos += VALORAVERDE;
                        break;
                    case 1:
                        puntos += VALORANARANJA;
                        break;
                    case 2:
                        puntos += VALORAGRIS;
                        break;
                    default:
                        puntos += 0;
                        break;
                }
            }
        }

        //Puntos por intentos
        puntos += (5 - palabras.size()) * VALORINTENTO;

        //Puntos por tiempo
        //Limite de tiempo 3 min, cada segundo entre lo que ha tardado y 180, +1 punto
        Duration d = Duration.between(ini, fin);
        long segundos = d.getSeconds();
        if (segundos < 180) {
            int sec = (int) segundos;
            puntos += 180 - sec;
        }

        return puntos;
    }

    //Metodos de escritura
    private void EscLetra(String let) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        //Cojo la ultima palabra que se esta escribiendo
        Palabra tmpPal = realm.where(Palabra.class).equalTo("show", true).findAll().last();
        //Meto la letra
        tmpPal.EscLetra(let);
        realm.insertOrUpdate(tmpPal);
        realm.commitTransaction();
        realm.close();
    }

    private void DelLetra() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        //Cojo la ultima palabra que se esta escribiendo
        Palabra tmpPal = realm.where(Palabra.class).equalTo("show", true).findAll().last();
        //Borro letra
        tmpPal.DelLetra();
        realm.insertOrUpdate(tmpPal);
        realm.commitTransaction();
        realm.close();
    }

    //Metodo para comprobar resultados
    private RealmList<Integer> ChkResults(Palabra palabra) {
        RealmList<Integer> results = new RealmList<Integer>(2, 2, 2, 2, 2);
        Palabra pal = palRes;
        for (int i = 0; i < palabra.getPalabraStr().length(); i++) {
            for (int j = 0; j < pal.getPalabraStr().length(); j++) {
                if (palabra.getPalabraStr().charAt(i) == pal.getPalabraStr().charAt(j) && i == j) {
                    results.set(i, 0);
                } else if (palabra.getPalabraStr().charAt(i) == pal.getPalabraStr().charAt(j)) {
                    results.set(i, 1);
                }
            }
        }
        return results;
    }

    //Metodo para cambiar de color las letras que se han usado
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ChgTeclado(Palabra pal) {
        List<String> letras = pal.getLetraList();
        List<Integer> results = pal.getLetraResList();

        for (int i = 0; i < 5; i++) {
            Integer num = results.get(i);
            for (int j = 0; j < teclado.size(); j++) {
                Button tmpBut = teclado.get(j);
                String let = tmpBut.getText().toString();
                if (letras.get(i).toLowerCase().equals(let.toLowerCase())) {
                    switch (num) {
                        case 0:
                            tmpBut.setBackgroundColor(Color.parseColor("#43A047"));
                            //tmpBut.setBackgroundResource(R.color.letPosLet);
                            break;
                        case 1:
                            tmpBut.setBackgroundColor(Color.parseColor("#E4A81D"));
                            //tmpBut.setBackgroundResource(R.color.letPos);
                            break;
                        case 2:
                            tmpBut.setBackgroundColor(Color.parseColor("#A6A6A6"));
                            //tmpBut.setBackgroundResource(R.color.grey);
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }
    }

    //Metodo para meter el nombre de usuario si gana e ir a la pantalla de resultado
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void FinGame(Boolean resultado) {

        if (resultado) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewInflated = LayoutInflater.from(this).inflate(R.layout.player_lyt, null);
            builder.setView(viewInflated);
            AlertDialog dialog = builder.create();
            dialog.show();
            txtPlayer = (EditText) viewInflated.findViewById(R.id.txtPlayer);
            btnSave = (Button) viewInflated.findViewById(R.id.btnSave);
            btnCancel = (Button) viewInflated.findViewById(R.id.btnCancel);

            Intent intent = new Intent(MainActivity.this, WinActivity.class);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Puntuacion punt = new Puntuacion(txtPlayer.getText().toString(), CalcPuntos(), true);
                    Integer id = punt.getId();
                    intent.putExtra("puntuacionID", id);
                    intent.putExtra("winned", true);
                    intent.putExtra("palabraRes", palRes.getPalabraStr());
                    dialog.dismiss();
                    startActivity(intent);
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Puntuacion punt = new Puntuacion(txtPlayer.getText().toString(), CalcPuntos(), true);
                    Integer id = punt.getId();
                    intent.putExtra("puntuacionID", id);
                    intent.putExtra("winned", true);
                    intent.putExtra("palabraRes", palRes.getPalabraStr());
                    dialog.dismiss();
                    realm.beginTransaction();
                    realm.copyToRealm(punt);
                    realm.commitTransaction();
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(MainActivity.this, WinActivity.class);
            Puntuacion punt = new Puntuacion("Player", CalcPuntos(), false);
            Integer id = punt.getId();
            intent.putExtra("puntuacionID", id);
            intent.putExtra("winned", false);
            intent.putExtra("palabraRes", palRes.getPalabraStr());
            startActivity(intent);
        }


    }


    //Metodo de comprobacion
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ChkPalabra() {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        //Cojo la ultima palabra que se esta escribiendo para comprobarla
        RealmResults<Palabra> tmpPals = realm.where(Palabra.class).equalTo("show", true).findAll();
        Palabra tmpPal = realm.where(Palabra.class).equalTo("show", true).findAll().last();
        realm.commitTransaction();

        //Comienzan las comprobaciones
        String res = EsValida(tmpPal);
        if (res != "") {
            Toast toast = Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG);
            return;
        }

        //Comprobar cada letra
        realm.beginTransaction();

        RealmList<Integer> result = ChkResults(tmpPal);
        tmpPal.setLetraResList(result);

        realm.insertOrUpdate(tmpPal);

        realm.commitTransaction();

        //Cambiar color del teclado
        ChgTeclado(tmpPal);

        //Dependiendo del resultado:
        if (tmpPals.size() == 5 && (result.contains(2) || result.contains(1))) {
            FinGame(false);
        } else if (!result.contains(2) && !result.contains(1)) {
            FinGame(true);
        } else {
            realm.beginTransaction();
            realm.copyToRealm(new Palabra("     ", false, true, new RealmList<Integer>(3, 3, 3, 3, 3)));
            realm.commitTransaction();
        }

        realm.close();
    }


    //Metodo para saber si esta en modo oscuro y poner la foto de la luna o el sol
    public boolean isNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    //Metodo para mostrar el dialogo para confirmar si cambiar de modo
    public void chgNightMode(Boolean night) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.lyt_dialog_chgdarkmode, null);
        builder.setView(viewInflated);
        AlertDialog dialog = builder.create();
        dialog.show();
        btnConfirm = (Button) viewInflated.findViewById(R.id.btnSave);
        btnCancelar = (Button) viewInflated.findViewById(R.id.btnCancel);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(night){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    fabDN.setImageResource(R.drawable.ic_moon_black_24);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    fabDN.setImageResource(R.drawable.ic_sun_white_24);
                }
                dialog.dismiss();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabDN = (FloatingActionButton) findViewById(R.id.fabDN);
        if (isNightMode(MainActivity.this)) {
            fabDN.setImageResource(R.drawable.ic_sun_white_24);
        } else {
            fabDN.setImageResource(R.drawable.ic_moon_black_24);
        }
        fabDN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chgNightMode(isNightMode(MainActivity.this));
            }
        });

        ini = Instant.now();

        btnChk = findViewById(R.id.btnChk);
        btnChk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fin = Instant.now();
                ChkPalabra();
            }
        });
        btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DelLetra();
            }
        });
        btnA = findViewById(R.id.btnA);
        btnA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("A");
            }
        });
        btnB = findViewById(R.id.btnB);
        btnB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("B");
            }
        });
        btnC = findViewById(R.id.btnC);
        btnC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("C");
            }
        });
        btnD = findViewById(R.id.btnD);
        btnD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("D");
            }
        });
        btnE = findViewById(R.id.btnE);
        btnE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("E");
            }
        });
        btnF = findViewById(R.id.btnF);
        btnF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("F");
            }
        });
        btnG = findViewById(R.id.btnG);
        btnG.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("G");
            }
        });
        btnH = findViewById(R.id.btnH);
        btnH.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("H");
            }
        });
        btnI = findViewById(R.id.btnI);
        btnI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("I");
            }
        });
        btnJ = findViewById(R.id.btnJ);
        btnJ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("J");
            }
        });
        btnK = findViewById(R.id.btnK);
        btnK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("K");
            }
        });
        btnL = findViewById(R.id.btnL);
        btnL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("L");
            }
        });
        btnM = findViewById(R.id.btnM);
        btnM.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("M");
            }
        });
        btnN = findViewById(R.id.btnN);
        btnN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("N");
            }
        });
        btnNN = findViewById(R.id.btnNN);
        btnNN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("Ñ");
            }
        });
        btnO = findViewById(R.id.btnO);
        btnO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("O");
            }
        });
        btnP = findViewById(R.id.btnP);
        btnP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("P");
            }
        });
        btnQ = findViewById(R.id.btnQ);
        btnQ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("Q");
            }
        });
        btnR = findViewById(R.id.btnR);
        btnR.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("R");
            }
        });
        btnS = findViewById(R.id.btnS);
        btnS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("S");
            }
        });
        btnT = findViewById(R.id.btnT);
        btnT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("T");
            }
        });
        btnU = findViewById(R.id.btnU);
        btnU.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("U");
            }
        });
        btnV = findViewById(R.id.btnV);
        btnV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("V");
            }
        });
        btnW = findViewById(R.id.btnW);
        btnW.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("W");
            }
        });
        btnX = findViewById(R.id.btnX);
        btnX.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("X");
            }
        });
        btnY = findViewById(R.id.btnY);
        btnY.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("Y");
            }
        });
        btnZ = findViewById(R.id.btnZ);
        btnZ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EscLetra("Z");
            }
        });

        teclado = new ArrayList<Button>() {
        };

        teclado.add(btnChk);
        teclado.add(btnDel);
        teclado.add(btnA);
        teclado.add(btnB);
        teclado.add(btnC);
        teclado.add(btnD);
        teclado.add(btnE);
        teclado.add(btnF);
        teclado.add(btnG);
        teclado.add(btnH);
        teclado.add(btnI);
        teclado.add(btnJ);
        teclado.add(btnK);
        teclado.add(btnL);
        teclado.add(btnM);
        teclado.add(btnN);
        teclado.add(btnNN);
        teclado.add(btnO);
        teclado.add(btnP);
        teclado.add(btnQ);
        teclado.add(btnR);
        teclado.add(btnS);
        teclado.add(btnT);
        teclado.add(btnU);
        teclado.add(btnV);
        teclado.add(btnW);
        teclado.add(btnX);
        teclado.add(btnY);
        teclado.add(btnZ);

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        //Si no ha nada en la base de datos, introduce todas las palabras
        RealmResults<Palabra> res = realm.where(Palabra.class).findAll();
        Boolean reloadBD = false;
        Boolean reloadPuntuaciones = false;

        if (reloadPuntuaciones) {
            realm.delete(Puntuacion.class);
        }

        if (res.isEmpty() || reloadBD) {
            System.out.println("------------------------------");
            System.out.println("Introduciendo datos en la BD");
            System.out.println("------------------------------");

            //Se vacia la BD
            realm.deleteAll();

            ArrayList<String> palabras = data.getWords();
            for (String pal : palabras) {
                Palabra p = new Palabra(pal, true, false, new RealmList<Integer>(3, 3, 3, 3, 3));
                realm.copyToRealm(p);
            }
            ArrayList<String> dict1 = data.getWordsFirstPart();
            for (String pal : dict1) {
                Palabra p = new Palabra(pal, false, false, new RealmList<Integer>(3, 3, 3, 3, 3));
                realm.copyToRealm(p);
            }
            ArrayList<String> dict2 = data.getWordsSecondPart();
            for (String pal : dict2) {
                Palabra p = new Palabra(pal, false, false, new RealmList<Integer>(3, 3, 3, 3, 3));
                realm.copyToRealm(p);
            }

            //Esta palabra esta vacia y la usare luego
            realm.copyToRealm(new Palabra("     ", false, true, new RealmList<Integer>(3, 3, 3, 3, 3)));
        } else {
            RealmResults<Palabra> rows = realm.where(Palabra.class).equalTo("show", true).findAll();
            rows.deleteAllFromRealm();
            realm.copyToRealm(new Palabra("     ", false, true, new RealmList<Integer>(3, 3, 3, 3, 3)));
        }

        //Me guardo la palabra resultado
        Random rnd = new Random();
        palRes = realm.where(Palabra.class).findAll().get(rnd.nextInt(TOTALPALABRAS));
        System.out.println("================================");
        System.out.println("Palabra resultado: " + palRes.getPalabraStr());
        System.out.println("================================");
        //palRes = realm.where(Palabra.class).findAll().get(rnd.nextInt(TOTALPALABRAS));

        //RealmResults<Palabra> realmResultPalabras = realm.where(Palabra.class).equalTo("palabraStr","     ").and().equalTo("adivinable",false).findAll();
        //realmResultPalabras = realm.where(Palabra.class).equalTo("palabraStr", "     ").and().equalTo("adivinable", false).findAll();

        realm.commitTransaction();


        //Aqui va el recycler
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvIntentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        realm.beginTransaction();
        realmResultPalabras = realm.where(Palabra.class).equalTo("show", true).findAll();
        //RecyclerDataAdapter recyclerDataAdapter = new RecyclerDataAdapter(realmResultPalabras);
        realm.commitTransaction();

        realmResultPalabras.addChangeListener(new RealmChangeListener<RealmResults<Palabra>>() {
            @Override
            public void onChange(RealmResults<Palabra> palabras) {
                recyclerDataAdapter.notifyDataSetChanged();
            }
        });

        recyclerDataAdapter = new RecyclerDataAdapter(realmResultPalabras);

        recyclerView.setAdapter(recyclerDataAdapter);


    }
}