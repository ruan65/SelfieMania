package com.gmail.ruan65.selfiemania;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class TimeToGetSelfieService extends IntentService {

    private static final int NOTIF_ID = 1;


    public TimeToGetSelfieService() {
        super("TimeToGetSelfieService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();

        Intent notifIntent = new Intent(this, Preview.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

        Notification notification = new Notification.Builder(this)

                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_photo_camera_white_24dp)
                .setWhen(when)

                .setTicker("It's time to take Selfie")
                .setContentTitle("You are so pretty (handsome)!")
                .setContentText("Push it to open Selfie-taker. Demonstrate yourself to the World!")

                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)

                .build();

        nm.notify(NOTIF_ID, notification);
    }

}
