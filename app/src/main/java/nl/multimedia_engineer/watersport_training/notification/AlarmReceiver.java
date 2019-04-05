package nl.multimedia_engineer.watersport_training.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Starts the notification service which sends a notification.
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
