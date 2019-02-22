package nl.multimedia_engineer.cwo_app.persistence;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        void onFailedSavedUserGroup();
    }


    public static void removeGroupForUser(FirebaseAuth auth, String groupId) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(groupId);
        groupRef.removeValue();
    }

    public static void removeGroupForAllUsers(FirebaseAuth auth, String groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("deleted", true);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("cursistenPerGroep/" + groupId, map);
        childUpdates.put("groepen/" + groupId, map);
        childUpdates.put("users/" + auth.getUid() + "/groepen/" + groupId, null);
        rootRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


      // todo look into transaction for this
//
//        // remove first from general, otherwise i'm not allowed to remove it from general anymore.
//        DatabaseReference cursistenPerGroepRef = DatabaseRefUtil.getCursistenPerGroep(groupId);
//
//        cursistenPerGroepRef.setValue(map);
//
//        DatabaseReference groepRef = DatabaseRefUtil.getGroepRef(groupId);
//        groepRef.setValue(map);
//
//        removeGroupForUser(auth, groupId);
    }

    /**
     *
     * @param auth
     * @param groupName
     * @return true if succesful
     */
    public static void createGroup(FirebaseAuth auth, final String groupName, final String discipline, final SavedUserGroepen receiver) {
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
                Group group = new Group(discipline, groupName);
                group.setId(groupId);
                receiver.onSuccesSavedUserGroup(group);

                DatabaseRefUtil.getGroepenRef().child(groupId).child("discipline").setValue(discipline);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                receiver.onFailedSavedUserGroup();
            }
        });

    }

    public static void addExistingGroupToUser(FirebaseAuth auth, GroupPartial groupPartial) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(groupPartial.getId());
        groupRef.setValue(groupPartial);

    }

    public static void getUserGroepenPartial(FirebaseAuth auth, final ReceiveUserGroepen receiver) {
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
