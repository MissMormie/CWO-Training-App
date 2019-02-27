package nl.multimedia_engineer.cwo_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

/**
 * Takes care of checking login before showing content.
 */
public abstract class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private static final String TAG = BaseActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFireBaseAuth();
        if(!(this instanceof MainActivity || this instanceof LoginActivity || this instanceof CreateOrJoinGroupActivity)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        showingLoginIfNotLoggedIn(this);


        getGroupDataOrMakeGroup();
    }

    public void showingLoginIfNotLoggedIn(Context context) {
        if(context.getClass() == LoginActivity.class) {
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || currentUser.isAnonymous()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initializeFireBaseAuth() {
        FirebaseApp.initializeApp(this);
        FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Gets group data for this user, if no group data
     */
    private void getGroupDataOrMakeGroup() {
        // Check if we already have the required state data set
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(        sharedPreferences.contains(getString(R.string.pref_current_group_id))
                && !sharedPreferences.getString(getString(R.string.pref_current_group_id), "").isEmpty()
                && sharedPreferences.contains(getResources().getString(R.string.pref_current_group_name))
                && !sharedPreferences.getString(getString(R.string.pref_current_group_name), "").isEmpty()
                && sharedPreferences.contains(getResources().getString(R.string.pref_discipline))
                && !sharedPreferences.getString(getString(R.string.pref_discipline), "").isEmpty()
                && sharedPreferences.contains(getString(R.string.pref_current_user))
                && !sharedPreferences.getString(getString(R.string.pref_current_user), "").isEmpty()
        ) {
            // all group settings are available.
            return;
        }

        if(this instanceof CreateOrJoinGroupActivity) {
            return;
        }

        showProgressDialog();

        DatabaseReference myRef = DatabaseRefUtil.getUserGroupsRef(mAuth);
        final Context context = this;

        Query query = myRef.orderByChild("id").limitToFirst(1);

        // todo move this to a persistence class
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Map> map = (Map<String, Map>) dataSnapshot.getValue();

                if(map.isEmpty()) {
                    // User does not have a group
                    Intent intent = new Intent(context, CreateOrJoinGroupActivity.class);
                    intent.putExtra(CreateOrJoinGroupActivity.FIRST_GROUP, true);
                    startActivity(intent);
                } else {
                    // User does have a group but data was removed from device, adding again.
                    for(Map.Entry<String, Map> entry : map.entrySet()) {
                        Map<String, String> results = entry.getValue();
                        // Only need 1, to set as current group.
                        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_name), results.get("name")).commit();
                        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_id), results.get("id")).apply();
                        sharedPreferences.edit().putString(getResources().getString(R.string.pref_discipline), results.get("discipline")).apply();
                        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_user), mAuth.getUid()).apply();
                        break;
                    }
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                hideProgressDialog();
                // todo show error that connection is not working now.

            }
        });
    }


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(String title, String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(text);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    /**
     * Shows a progress dialog with a generic loading text. User showProgressDialog(String title, String text) if you want to set a specific message.
     */
    public void showProgressDialog() {
        showProgressDialog(getString(R.string.alert_dialog_title_loading), getString(R.string.alert_dialog_text_loading));
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Shows a alert dialog with a generic error text. User showErrorDialog(String title, String text) if you want to set a specific message.
     */
    public void showErrorDialog() {
        showErrorDialog(getString(R.string.alert_dialog_error_title), getString(R.string.alert_dialog_error_tekst));
    }

    public void showErrorDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text);
        builder.setTitle(title);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    public void showGeenCursisten() {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_dialog_no_cursist_text));
        builder.setTitle(getString(R.string.alert_dialog_no_cursist_title));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                dialog.cancel();
            }
        });
        builder.show();
        return;
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
