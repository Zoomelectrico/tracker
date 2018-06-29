package com.tracker.tracker.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tracker.tracker.Modelos.Rutina;

public class NotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Rutina rutina = intent.getParcelableExtra("RUTINA");

        Log.e("RUTINA", rutina.toMap().toString());

        Notification notification = intent.getParcelableExtra("NOTIFICATION");
        int id = intent.getIntExtra("NOTIFICATION_ID", 0);
        if (notificationManager != null && notification != null) {
            notificationManager.notify(id, notification);
        }
    }
}
