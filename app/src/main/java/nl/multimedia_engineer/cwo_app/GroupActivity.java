package nl.multimedia_engineer.cwo_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import nl.multimedia_engineer.cwo_app.dto.UserGroupPartialList;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class GroupActivity extends BaseActivity implements ItemClickListener {
    final static String TAG = "GroupActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        final ItemClickListener listener = this;

        // get groups from user.
        // Read from the database
        DatabaseReference groupRef = DatabaseRefUtil.getUserRef(mAuth);

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                UserGroupPartialList userGroupPartialList = dataSnapshot.getValue(UserGroupPartialList.class);

                // todo check not null.


                mAdapter = new GroupAdapter(userGroupPartialList, listener);
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
    public void onPositionClicked(int position) {

    }
}
