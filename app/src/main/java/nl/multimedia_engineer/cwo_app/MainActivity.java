package nl.multimedia_engineer.cwo_app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.notification.AlarmReceiver;
import nl.multimedia_engineer.cwo_app.notification.NotificationService;
import nl.multimedia_engineer.cwo_app.util.ConnectionIssuesUtil;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;
import nl.multimedia_engineer.cwo_app.util.DateUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getGroupDataOrMakeGroup();

        testFunctionShowAlarmDialog();

        // todo move this to settings and add time
//        setNotification();
    }

    private void testFunctionShowAlarmDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("message");
        dialogBuilder.setTitle("alert");
        dialogBuilder.setNeutralButton("btn ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton("btn nee ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Gets group data for this user, if no group data
     */
    private void getGroupDataOrMakeGroup() {
        // Check if we already have the required group data.
        final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        if(sharedPreferences.contains(getResources().getString(R.string.pref_current_group_id)) &&
           sharedPreferences.contains(getResources().getString(R.string.pref_current_group_name))) {
            // all group settings are available.
            return;
        }
        showLoading(true);

        DatabaseReference myRef = DatabaseRefUtil.getUserGroupsRef(mAuth);
        final Context context = this;

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                if(map.isEmpty()) {
                    // User does not have a group
                    Intent intent = new Intent(context, CreateOrJoinGroupActivity.class);
                    startActivity(intent);
                } else {
                    // User does have a group but data was removed from device, adding again.
                    for(Map.Entry<String, String> entry : map.entrySet()) {
                        // Only need 1, to set as current group.
                        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_name), entry.getValue()).commit();
                        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_id), entry.getKey()).commit();
                        break;
                    }
                }

                showLoading(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                ConnectionIssuesUtil.unableToConnect(context);
                showLoading(false);
            }
        });
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
            case R.id.action_maintain_groups:
                Intent startGroupActivity = new Intent(this, GroupActivity.class);
                startActivity(startGroupActivity);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    public void onClickCursistenLijst(View view) {
//        Context context = this;
//        Class destinationClass = CursistListActivity.class;
//        Intent intent = new Intent(context, destinationClass);
//        startActivity(intent);
    }

    public void onClickNieuweTraining(View view) {
//        Context context = this;
//        Class destinationClass = TrainingActivity.class;
//        Intent intent = new Intent(context, destinationClass);
//        startActivity(intent);
    }

    public void onClickNieuweCursist(View view) {
//        Context context = this;
//        Class destinationClass = CreateCursistActivity.class;
//        Intent intent = new Intent(context, destinationClass);
//        startActivity(intent);
    }

    public void onClickUitgevenDiploma(View view) {
//        Context context = this;
//        Class destinationClass = DiplomaUitgevenActivity.class;
//        Intent intent = new Intent(context, destinationClass);
//        startActivity(intent);
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
