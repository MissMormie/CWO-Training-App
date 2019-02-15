package nl.multimedia_engineer.cwo_app.persistence;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class PersistGroepen {

    public static void removeGroupForUser(FirebaseAuth auth, String groupId) {
        DatabaseReference groupRef = DatabaseRefUtil.getUserGroupsRef(auth).child(groupId);
        groupRef.removeValue();
    }

    public static void removeGroupForAllUsers(FirebaseAuth auth, String groupId) {
        // todo look into transaction for this
        Map<String, Boolean> map = new HashMap<>();
        map.put("deleted", true);

        // remove first from general, otherwise i'm not allowed to remove it from general anymore.
        DatabaseReference cursistenPerGroepRef = DatabaseRefUtil.getCursistenPerGroep(groupId);

        cursistenPerGroepRef.setValue(map);

        DatabaseReference groepRef = DatabaseRefUtil.getGroepRef(groupId);
        groepRef.setValue(map);

        removeGroupForUser(auth, groupId);

    }
}
