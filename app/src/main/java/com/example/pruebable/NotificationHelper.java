package com.example.pruebable;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Creación y envío de notificación push
     */
    public void createNotification(String title, String descripcion, String urlImagen, int id) {
        Intent intent =new Intent(mContext , ViewGeneratorActivity.class);
        Random rand = new Random();
        int n = rand.nextInt(1000);
        intent.putExtra("majorMeraki",String.valueOf(id));
        intent.putExtra("mensajeNotificacion", descripcion);
        intent.putExtra("urlImagen", urlImagen);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(mContext,0, intent, n);
        mBuilder = new NotificationCompat.Builder(mContext);

        mBuilder.setSmallIcon(R.drawable.ic_stat_rt);
        mBuilder.setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(descripcion))
                .setContentText(descripcion)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);
        mBuilder.build();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        Log.d("INFO","Id Not: " + n);
        mNotificationManager.notify(n, mBuilder.build());
    }
}
