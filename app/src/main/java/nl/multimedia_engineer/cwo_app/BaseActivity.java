package nl.multimedia_engineer.cwo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import nl.multimedia_engineer.cwo_app.util.ConnectionIssuesUtil;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

/**
 * Takes care of checking login before showing content.
 */
public abstract class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFireBaseAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        showingLoginIfNotLoggedIn(this);
        getGroupDataOrMakeGroup();
    }

    public boolean showingLoginIfNotLoggedIn(Context context) {
        if(context.getClass() == LoginActivity.class) {
            return true;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || currentUser.isAnonymous()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void initializeFireBaseAuth() {
        FirebaseApp.initializeApp(this);
        FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    protected void showLoading(boolean show) {
        if(show) {
            Toast.makeText(this, "Loading data, please wait", Toast.LENGTH_SHORT).show();
        }
        // TODO
    }

    /**
     * Gets group data for this user, if no group data
     */
    private void getGroupDataOrMakeGroup() {
        // Check if we already have the required group data.
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.contains(getResources().getString(R.string.pref_current_group_id)) &&
                sharedPreferences.contains(getResources().getString(R.string.pref_current_group_name))) {
            // all group settings are available.
            return;
        }
        if(this instanceof  CreateOrJoinGroupActivity) {
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

}
