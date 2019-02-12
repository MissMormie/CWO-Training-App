package nl.multimedia_engineer.cwo_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import nl.multimedia_engineer.cwo_app.model.Group;
import nl.multimedia_engineer.cwo_app.util.ConnectionIssuesUtil;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class CreateOrJoinGroupActivity extends BaseActivity {
    private static final String TAG = CreateOrJoinGroupActivity.class.getName();

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

        // Todo, make transaction to make this one db query.
        // Add group to user
        DatabaseReference pushedRef = DatabaseRefUtil.getUserGroupsRef(mAuth).push();
        pushedRef.setValue(groupName);


        // Add to groups
        String groupId = pushedRef.getKey();

        if(groupId == null) {
            Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            return;
        }

        Group group = new Group("windsurfen", groupName);
        DatabaseRefUtil.getGroepenRef().child(groupId).setValue(group);

        addGroupToSharedPreferences(groupId, groupName);
    }

    public void onClickJoinGroup(View view) {
        final String accessCode = ((EditText) findViewById(R.id.editText_accessCode)).getText().toString();
        final String groupName = ((EditText) findViewById(R.id.editText_joinGroupName)).getText().toString();

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
        groepRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If there is a value all is fine, otherwise get rid of the previously made group.
                if(dataSnapshot.exists()) {
                    addGroupToSharedPreferences(accessCode, groupName);
                    return;
                }

                // removing group from the person.
                userGroupRef.removeValue();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // anything but permissions denied. Probably connection errors
                // todo make generic handle firebase db error
                Toast.makeText(context, getResources().getText(R.string.error_message), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void addGroupToSharedPreferences(String groupId, String groupName) {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);

         // Using commit because we'll need these values on the next screen.
        sp.edit().putString(getResources().getString(R.string.pref_current_group_name), groupName).commit();
        sp.edit().putString(getResources().getString(R.string.pref_current_group_id), groupId).commit();

    }
}
