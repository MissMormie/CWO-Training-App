package nl.multimedia_engineer.cwo_app;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import nl.multimedia_engineer.cwo_app.model.GroupPartial;
import nl.multimedia_engineer.cwo_app.persistence.PersistGroepen;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;
import nl.multimedia_engineer.cwo_app.util.PreferenceUtil;

public class GroupActivity extends BaseActivity
        implements GroupAdapter.GroupItemClickListener,
                    PersistGroepen.ReceiveUserGroepen {

    final static String TAG = "GroupActivity";
    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<GroupPartial> userGroupPartialList;
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

        showProgressDialog();
        PersistGroepen.getUserGroepenPartial(mAuth, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdapter != null) {
            String groupId = PreferenceUtil.getPreferenceString(this, getString(R.string.pref_current_group_id), "");
            mAdapter.setCurrentActiveGroupId(groupId);
        }

    }

    public void onClickAddGroup(View view) {
        Class destinationClass = CreateOrJoinGroupActivity.class;
        Intent intent = new Intent(this, destinationClass);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int position) {
        GroupPartial group = userGroupPartialList.get(position);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_id), group.getId()).apply();
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_group_name), group.getName()).apply();
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_discipline), group.getDiscipline()).apply();
        sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_user), mAuth.getUid()).apply();

        mAdapter.setCurrentActiveGroupId(group.getId());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemEditClicked(final int position) {
        final GroupPartial groupPartial = userGroupPartialList.get(position);
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
                userGroupPartialList.get(position).setName(newName);
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
        dialogBuilder.setPositiveButton(R.string.alert_dialog_delete_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verifyDeleteAll(userGroupPartialList.get(position).getId(), position);
                dialog.cancel();
            }
        });

        dialogBuilder.setNeutralButton(R.string.alert_dialog_delete_for_me, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PersistGroepen.removeGroupForUser(mAuth, userGroupPartialList.get(position).getId());
                userGroupPartialList.remove(position);
                mAdapter.notifyDataSetChanged();
                setNewActiveGroup();
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

    @Override
    public void onItemShareClicked(int position) {
        String id = userGroupPartialList.get(position).getId();

        String resource = getResources().getString(R.string.alert_dialog_share_group_text);
        String text = String.format(resource, id);

        // Copy group code to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.clip_data_group_code), id);
        clipboard.setPrimaryClip(clip);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton(getString(R.string.alert_dialog_text_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        })
            .setTitle(getString(R.string.alert_dialog_share_group_title))
            .setMessage(text)
            .show();
    }

    private void setNewActiveGroup() {
        if(!userGroupPartialList.isEmpty()) {
            onItemClicked(0);
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().remove(getResources().getString(R.string.pref_current_group_id)).apply();
            sharedPreferences.edit().remove(getResources().getString(R.string.pref_current_group_name)).apply();
        }
    }

    private void verifyDeleteAll(final String groupId, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.alert_dialog_verify_delete_all);
        dialogBuilder.setTitle(R.string.alert_dialog_title_delete);
        dialogBuilder.setPositiveButton(R.string.alert_dialog_delete_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PersistGroepen.removeGroupForAllUsers(mAuth, groupId);
                setNewActiveGroup();
                userGroupPartialList.remove(position);
                mAdapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });

        dialogBuilder.setNegativeButton(R.string.alert_dialog_text_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    // ----------------------------------- Implementation ReceivedGroepen --------------------------

    @Override
    public void onReceiveUserGroepen(List<GroupPartial> groupPartialList) {
        hideProgressDialog();
        if(groupPartialList == null) {
            showErrorDialog();
            return;
        }
        userGroupPartialList = groupPartialList;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String groupId = sharedPreferences.getString(getResources().getString(R.string.pref_current_group_id), "");

        mAdapter = new GroupAdapter(userGroupPartialList, this, groupId);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onReceiveUserGroepenFailed() {
        hideProgressDialog();
        showErrorDialog();
    }
}
