package org.cuatrovientos.wordle.utils;


import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.cuatrovientos.wordle.R;
import org.cuatrovientos.wordle.activities.MainActivity;
import org.cuatrovientos.wordle.activities.ShareActivity;
import org.cuatrovientos.wordle.activities.WinActivity;

public class MyUtils {
    public static Dialog onCreateDialog(Bundle savedInstanceState, Activity act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle(R.string.dialogBanner)
                .setMessage(R.string.dialogQuestion)
                .setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // START THE GAME!
                    }
                })
                .setNegativeButton(R.string.dialogNo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

    public static NotificationCompat.Builder genNotificationSimple(Context th, String CHANNEL_ID, String title, String text, Integer icon) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(th, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        return builder;
    }

    public static NotificationCompat.Builder genNotificationAction(Context th, String CHANNEL_ID, String title, String text, Integer icon) {
        Intent intent = new Intent(th, ShareActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(th, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(th, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    public static NotificationCompat.Builder genNotificationActionButton(
            Context th, String CHANNEL_ID, String title, String text, Integer icon
    ) {
        Intent intent = new Intent(th, ShareActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(th, 0, intent, 0);

        PendingIntent pendInten = PendingIntent.getActivity(th, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(th, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.btnlet_layout_grey, "Compartir",pendInten);
        return builder;
    }

}
