package nl.multimedia_engineer.cwo_app.notification;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import nl.multimedia_engineer.cwo_app.MainActivity;
import nl.multimedia_engineer.cwo_app.R;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("Notification Service");
    }

    public static final String CHANNEL_ID = "CWO TRAINING";
    public static final String CHANNEL_NAME = "channel name";
    public static final String CHANNEL_DESCRIPTION = "reminders to fill out cwo app";

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Create an explicit intent for an Activity in your app
        Intent intention = new Intent(this, MainActivity.class);
        intention.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intention, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification from app")
                .setContentText("Text bladibla etc")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, builder.build());
    }
}
