package nl.multimedia_engineer.watersport_training.persistence;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.watersport_training.dto.GroupDTO;
import nl.multimedia_engineer.watersport_training.model.GroupPartial;
import nl.multimedia_engineer.watersport_training.model.User;
import nl.multimedia_engineer.watersport_training.util.DatabaseRefUtil;

public class PersistGroepen {
    public interface ReceiveUserGroepen {
        void onReceiveUserGroepen(List<GroupPartial> groupPartialList);
        void onReceiveUserGroepenFailed();
    }

    public interface SavedUserGroepen {
        void onSuccesSavedUserGroup(GroupPartial group);
        void onFailedSavedUserGroup();
    }

    public interface JoinGroup {
        void onSuccessJoinedGroup(GroupPartial group);
        void onFailedJoinedGroup();
        void onJoinGroupDoesNotExist();
    }

    public static void removeGroupForUser(final FirebaseAuth auth, final String groupId) {
        // Check if user is only user:
        DatabaseReference usersRef = DatabaseRefUtil.getGroepRef(groupId).child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 1) {
                    List userList = new ArrayList();
                    for (DataSnapshot user :dataSnapshot.getChildren()) {
                        userList.add(user.getValue());
                    }

                    removeGroupFromUsers(userList, groupId);

                } else {
                    // more users, just delete reference for this user.

                    DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(groupId);
                    groupRef.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo
            }
        });


    }


    // todo !important should also remove all images.
    public static void removeGroupForAllUsers(final FirebaseAuth auth, final String groupId) {
        // Get all users of Group
        DatabaseReference usersRef = DatabaseRefUtil.getGroepRef(groupId).child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List userList = new ArrayList();
                for (DataSnapshot user :dataSnapshot.getChildren()) {
                        userList.add(user.getValue());
                }

                removeGroupFromUsers(userList, groupId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo
            }
        });
    }

    private static void removeGroupFromUsers(List<String> users, String groupId) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("cursistenPerGroep/" + groupId, null);
        childUpdates.put("groepen/" + groupId, null);
        childUpdates.put("removeImages/groepen/" + groupId, groupId);
        for(String user : users) {
            childUpdates.put("users/" + user + "/groepen/" + groupId, null);
        }

        rootRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // todo
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // todo
            }
        });
    }

    /**
     *
     * @param auth
     * @param groupName
     * @return true if succesful
     */
    public static void createGroup(FirebaseAuth auth, final String groupName, final String discipline, final SavedUserGroepen receiver) {
        final DatabaseReference pushedRef = DatabaseRefUtil.getUserGroupsRef(auth).push();
        // prepare objects
        final GroupPartial groupPartial = new GroupPartial(pushedRef.getKey(), groupName, discipline);
        final GroupDTO groupDTO = new GroupDTO(groupPartial, new User(auth.getUid()));

        // Create database ref and childupdates
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();

        String userGroupPath = "users/" + auth.getUid() + "/groepen/" + groupPartial.getId();
        String groepenPath = "groepen/" + groupPartial.getId();

        childUpdates.put(userGroupPath, groupPartial);
        childUpdates.put(groepenPath, groupDTO);

        // Run update
        rootRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                receiver.onSuccesSavedUserGroup(groupPartial);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                receiver.onFailedSavedUserGroup();
            }
        });
    }


    public static void joinGroup(final FirebaseAuth auth, final String groupName, final String accessCode, final JoinGroup receiver) {
        // First add group to user, otherwise no access to check if group exists.
        final DatabaseReference userGroupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(accessCode);

        final GroupPartial groupPartial = new GroupPartial();
        groupPartial.setId(accessCode);
        groupPartial.setName(groupName);
        userGroupRef.setValue(groupPartial);

        // Checking for discipline so we don't have to retrieve all the data.
        DatabaseReference groepRef = DatabaseRefUtil.getGroepDisciplineRef(accessCode);

        groepRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If there is a value all is fine, otherwise get rid of the previously made group.
                if(dataSnapshot.exists()) {
                    groupPartial.setDiscipline(dataSnapshot.getValue(String.class)   );
                    joinGroupSaveData(auth, groupPartial, receiver);

                    return;
                }

                // removing group from the person.
                userGroupRef.removeValue();
                receiver.onJoinGroupDoesNotExist();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // anything but permissions denied. Probably connection errors
                receiver.onFailedJoinedGroup();
            }
        });
    }

    public static void joinGroupSaveData(FirebaseAuth auth, final GroupPartial groupPartial, final JoinGroup receiver) {

        // Create database ref and childupdates
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();

        String userGroupPath = "users/" + auth.getUid() + "/groepen/" + groupPartial.getId();
        String groepenPath = "groepen/" + groupPartial.getId() + "/users/" + auth.getUid();

        childUpdates.put(userGroupPath, groupPartial);
        childUpdates.put(groepenPath, auth.getUid());

        // Run update
        rootRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                receiver.onSuccessJoinedGroup(groupPartial);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                receiver.onFailedJoinedGroup();
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

    public static void renameGroup(FirebaseAuth auth, GroupPartial group) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(group.getId()).child("name");
        groupRef.setValue(group.getName());
    }
}
