package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import nl.multimedia_engineer.cwo_app.model.Group;
import nl.multimedia_engineer.cwo_app.persistence.PersistGroepen;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class CreateOrJoinGroupActivity extends BaseActivity implements PersistGroepen.SavedUserGroepen {
    private static final String TAG = CreateOrJoinGroupActivity.class.getName();
    private final String discipline = "windsurfen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_group);
    }

    /**
     * Creates new group in db, both in the user node as in the group node.
     * @param view
     */
    public void onClickNewGroup(View view) {
        // Check name filled out.
        String groupName = ((EditText) findViewById(R.id.editText_newGroup)).getText().toString();

        if(groupName.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.text_fields_filled_wrong), Toast.LENGTH_SHORT).show();
            return;
        }
        PersistGroepen.createGroup(mAuth, groupName, discipline, this);
        showProgressDialog();
    }

    public void onClickJoinGroup(View view) {
        final String accessCode = ((EditText) findViewById(R.id.editText_accessCode)).getText().toString();
        final String groupName = ((EditText) findViewById(R.id.editText_joinGroupName)).getText().toString();
        // when allowing more disciplines this needs to be changed.

        final Group group = new Group(discipline, "groupName");
        group.setId(accessCode);

        if(accessCode.isEmpty() || groupName.isEmpty() ) {
            Toast.makeText(this, getResources().getString(R.string.text_fields_filled_wrong), Toast.LENGTH_SHORT).show();
            return;
        } else if(accessCode.length() < 10) {
            Toast.makeText(this, getResources().getString(R.string.error_msg_access_code_wrong), Toast.LENGTH_SHORT).show();
        }

        // todo Check group exists. But how, without adding the group there is no access..
        // Add check, remove maybe in 1 transaction?
        // for now not so great workaround.
        final DatabaseReference userGroupRef = DatabaseRefUtil.getUserGroupsRef(mAuth).child(accessCode);
        userGroupRef.setValue(groupName);

        // Checking for discipline so we don't have to retrieve all the data.
        DatabaseReference groepRef = DatabaseRefUtil.getGroepDisciplineRef(accessCode);


        final Context context = this;
        // todo move this to a persistence class.
        groepRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If there is a value all is fine, otherwise get rid of the previously made group.
                if(dataSnapshot.exists()) {
                    addGroupToSharedPreferences(accessCode, groupName, discipline);
                    onSuccesSavedUserGroup(group);
                    return;
                }

                // removing group from the person.
                userGroupRef.removeValue();
                showErrorDialog(getString(R.string.alert_dialog_error_title), getString(R.string.alert_dialog_error_tekst));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // anything but permissions denied. Probably connection errors
                showErrorDialog();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void addGroupToSharedPreferences(String groupId, String groupName, String discipline) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_id), groupId).apply();
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_name), groupName).apply();
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_discipline), discipline).apply();
    }

    // ------------------ implements PersistGroepen.SavedUserGroepen -------------------------------

    @Override
    public void onSuccesSavedUserGroup(Group group) {
        hideProgressDialog();
        // When addings more disciplines, this needs to be not hardcoded. Ok for now.
        addGroupToSharedPreferences(group.getId(), group.getName(), discipline);
        finish();
    }

    @Override
    public void onFailedSavedUserGroup() {
        hideProgressDialog();
        showErrorDialog();
    }
}
