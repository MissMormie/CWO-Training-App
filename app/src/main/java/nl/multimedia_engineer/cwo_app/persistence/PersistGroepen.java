package nl.multimedia_engineer.cwo_app.persistence;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.model.Group;
import nl.multimedia_engineer.cwo_app.model.GroupPartial;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class PersistGroepen {
    public interface ReceiveUserGroepen {
        void onReceiveUserGroepen(List<GroupPartial> groupPartialList);
        void onReceiveUserGroepenFailed();
    }

    public interface SavedUserGroepen {
        void onSuccesSavedUserGroup(Group group);
    }


    public static void removeGroupForUser(FirebaseAuth auth, String groupId) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(groupId);
        groupRef.removeValue();
    }

    public static void removeGroupForAllUsers(FirebaseAuth auth, String groupId) {
        // todo look into transaction for this
        Map<String, Object> map = new HashMap<>();
        map.put("deleted", true);
        map.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());

        // remove first from general, otherwise i'm not allowed to remove it from general anymore.
        DatabaseReference cursistenPerGroepRef = DatabaseRefUtil.getCursistenPerGroep(groupId);

        cursistenPerGroepRef.setValue(map);

        DatabaseReference groepRef = DatabaseRefUtil.getGroepRef(groupId);
        groepRef.setValue(map);

        removeGroupForUser(auth, groupId);

    }

    /**
     *
     * @param auth
     * @param groupName
     * @return true if succesful
     */
    public static void createGroup(FirebaseAuth auth, final String groupName, final SavedUserGroepen receiver) {
        // Add group to user
        final DatabaseReference pushedRef = DatabaseRefUtil.getUserGroupsRef(auth).push();
        GroupPartial group = new GroupPartial(pushedRef.getKey(), groupName);
        pushedRef.setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String groupId = pushedRef.getKey();
                if(groupId == null) {
                    return;
                }
                Group group = new Group("windsurfen", groupName);
                group.setId(groupId);
                receiver.onSuccesSavedUserGroup(group);

                // Todo, make transaction to make this one db query.
                DatabaseRefUtil.getGroepenRef().child(groupId).child("discipline").setValue("windsurfen");
            }
            // todo deal with failure.
        });

    }

    public static void addExistingGroupToUser(FirebaseAuth auth, GroupPartial groupPartial) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(groupPartial.getId());
        groupRef.setValue(groupPartial);

    }

    public static void getUserGroepen(FirebaseAuth auth, final ReceiveUserGroepen receiver) {
        DatabaseReference userGroupsRef = DatabaseRefUtil.getUserGroupsRef(auth);
        userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GroupPartial> groupPartialList = new ArrayList<>();
                for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    GroupPartial groupPartial = groupSnapshot.getValue(GroupPartial.class);
                    groupPartial.setId(groupSnapshot.getKey());
                    groupPartialList.add(groupPartial);
                }

                receiver.onReceiveUserGroepen(groupPartialList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.onReceiveUserGroepenFailed();
            }
        });

    }
}
