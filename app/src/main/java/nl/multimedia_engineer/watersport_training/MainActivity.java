package nl.multimedia_engineer.watersport_training;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import nl.multimedia_engineer.watersport_training.notification.AlarmReceiver;
import nl.multimedia_engineer.watersport_training.notification.NotificationService;
import nl.multimedia_engineer.watersport_training.util.DateUtil;

public class MainActivity extends BaseActivity {

    TextView tv_active_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        tv_active_group = findViewById(R.id.tv_active_group);
    }

    @Override
    public void onStart() {
        super.onStart();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String activeGroup = sharedPreferences.getString(getResources().getString(R.string.pref_current_group_name), "");
//        getSupportActionBar().setTitle(activeGroup);
        String text = activeGroup;
        tv_active_group.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.action_logout:
                mAuth.signOut();
                Intent startLoginActivity = new Intent(this, LoginActivity.class);
                startActivity(startLoginActivity);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    public void onClickCursistenLijst(View view) {
        Context context = this;
        Class destinationClass = CursistListActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void onClickNieuweTraining(View view) {
        Context context = this;
        Class destinationClass = TrainingActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void onClickNieuweCursist(View view) {
        Context context = this;
        Class destinationClass = CreateCursistActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void onClickUitgevenDiploma(View view) {
        Context context = this;
        Class destinationClass = DiplomaUitgevenActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);
    }

    public void onClickShowDisciplines(View view) {
        Context context = this;
        Class destinationClass = DemandsPerDisciplineActivity.class;
        Intent intent = new Intent(context, destinationClass);
        startActivity(intent);

    }

    public void onClickGroepenBeheren(View view) {
        Intent startGroupActivity = new Intent(this, GroupActivity.class);
        startActivity(startGroupActivity);
    }

    private void setNotification() {
        // Low overhead, needs to happen before any notifications can be send.
        createNotificationChannel();

        scheduleAlarm();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NotificationService.CHANNEL_NAME;
            String description = NotificationService.CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationService.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleAlarm() {
        Toast.makeText(this, "Scheduling alarm", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set up periodic alarm every wednesday night at 8:30
        Calendar cal = DateUtil.nextOccurence(DateUtil.WeekDays.WEDNESDAY.getInt(), 20, 30);

        long millisToFirstAlarm = cal.getTimeInMillis() - System.currentTimeMillis();
        long intervalMillis = 1000 * 60 * 24 * 7;

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, millisToFirstAlarm, intervalMillis, pIntent);
    }
}
