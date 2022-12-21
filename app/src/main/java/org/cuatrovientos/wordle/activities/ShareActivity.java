package org.cuatrovientos.wordle.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import org.cuatrovientos.wordle.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ShareActivity extends AppCompatActivity {

    Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = "+34 626111111";
                String mensaje = "Prueba a jugar wordle";

                PackageManager packageManager = ShareActivity.this.getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                String url = null;
                try {
                    url = "https://api.whatsapp.com/send?phone=" + numero + "&text=" + URLEncoder.encode(mensaje, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    startActivity(i);
                }
            }

        });
    }
}