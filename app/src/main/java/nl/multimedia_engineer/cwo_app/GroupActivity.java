package nl.multimedia_engineer.cwo_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import nl.multimedia_engineer.cwo_app.dto.UserGroupPartialList;
import nl.multimedia_engineer.cwo_app.model.GroupPartial;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class GroupActivity extends BaseActivity implements GroupAdapter.GroupItemClickListener {
    final static String TAG = "GroupActivity";
    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private UserGroupPartialList userGroupPartialList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_groups);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        final GroupAdapter.GroupItemClickListener listener = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String groupId = sharedPreferences.getString(getResources().getString(R.string.pref_current_group_id), "");

        // get groups from user.
        // Read from the database
        DatabaseReference groupRef = DatabaseRefUtil.getUserRef(mAuth);

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                userGroupPartialList = dataSnapshot.getValue(UserGroupPartialList.class);

                // todo check not null.


                mAdapter = new GroupAdapter(userGroupPartialList, listener, groupId);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void onClickAddGroup(View view) {
        Class destinationClass = CreateOrJoinGroupActivity.class;
        Intent intent = new Intent(this, destinationClass);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int position) {
        GroupPartial group = userGroupPartialList.getGroepen().get(position);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_id), group.getId()).apply();
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_name), group.getName()).apply();

        mAdapter.setCurrentActiveGroupId(group.getId());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemEditClicked(final int position) {
        final GroupPartial groupPartial = userGroupPartialList.getGroepen().get(position);
        String name = groupPartial.getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alert_dialog_title_update));
        builder.setMessage(getResources().getString(R.string.alert_dialog_text_update_group));
        final Context context = this;

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(name);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                userGroupPartialList.getGroepen().get(position).setName(newName);
                mAdapter.notifyItemChanged(position);
                // Save name to db.
                saveGroupNameToDb(groupPartial);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveGroupNameToDb(GroupPartial group) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(mAuth).child(group.getId());
        groupRef.setValue(group.getName());

    }

    @Override
    public void onItemDeleteClicked(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.alert_dialog_text_delete_group);
        dialogBuilder.setTitle(R.string.alert_dialog_title_delete);
        dialogBuilder.setPositiveButton(R.string.alert_dialog_text_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserGroupFromDB(userGroupPartialList.getGroepen().get(position));
                // todo delete from db and recyclerview.
                dialog.cancel();
            }
        });

        dialogBuilder.setNegativeButton(R.string.alert_dialog_text_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void deleteUserGroupFromDB(GroupPartial group) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(mAuth).child(group.getId());
        groupRef.removeValue();

    }
}
