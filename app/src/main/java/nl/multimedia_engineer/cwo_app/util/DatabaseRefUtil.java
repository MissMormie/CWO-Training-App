package nl.multimedia_engineer.cwo_app.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseRefUtil {
    private static String USERS = "users";
    private static String DISCIPLINES = "disciplines";
    private static String GROUPS = "groepen";
    private static String EXAMDEMANDS = "exameneisen";


    public static DatabaseReference getParentRef() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        return db.getReference();
    }

    public static DatabaseReference getUserRef(FirebaseAuth firebaseAuth) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(USERS).child(firebaseAuth.getCurrentUser().getUid());
    }

    public static DatabaseReference getUserGroupsRef(FirebaseAuth firebaseAuth) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        return db.getReference().child(USERS).child(firebaseAuth.getCurrentUser().getUid()).child(GROUPS);
    }

    public static DatabaseReference getDisciplinesRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(DISCIPLINES);
    }


    public static DatabaseReference getGroepenRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(GROUPS);
    }

    public static DatabaseReference getGroepRef(String groupId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(GROUPS).child(groupId);
    }

    public static DatabaseReference getGroepDisciplineRef(String groupId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(GROUPS).child(groupId).child("discipline");
    }

    public static DatabaseReference getDisciplineExamDemands(String discipline) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(EXAMDEMANDS).child(discipline);
    }

    public static DatabaseReference getSpecificExamDemands(String discipline, String exam) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(EXAMDEMANDS).child(discipline).child(exam);
    }


}
