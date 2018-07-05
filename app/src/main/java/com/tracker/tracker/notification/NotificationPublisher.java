package com.tracker.tracker.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.tracker.tracker.MainActivity;
import com.tracker.tracker.modelos.Rutina;
import com.tracker.tracker.modelos.Usuario;

public class NotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Rutina rutina = intent.getParcelableExtra("RUTINA");
        Notification notification = intent.getParcelableExtra("NOTIFICATION");
        Usuario usuario = intent.getParcelableExtra("user");
        int id = intent.getIntExtra("NOTIFICATION_ID", rutina.describeContents());
        if (notification != null) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("user", usuario);
            i.putExtra("RUTINA", rutina);
            context.startActivity(i);
            notificationManager.notify(id, notification);
        }



    }
}
