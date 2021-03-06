package nl.multimedia_engineer.watersport_training.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseRefUtil {
    private static final String TAG = DatabaseRefUtil.class.getSimpleName();
    public static String USERS = "users";
    public static String DISCIPLINES = "disciplines";
    public static String GROUPS = "groepen";
    public static String EXAMENEISEN = "exameneisen";
    public static String CURSISTEN = "cursisten";
    public static String CURSISTEN_PER_GROUP = "cursistenPerGroep";
    public static String BEHAALDE_EISEN = "behaaldeEisen";

    // ------------------------------------- Users ------------------------------------------------0

    public static DatabaseReference getUserRef(FirebaseAuth firebaseAuth) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(USERS).child(firebaseAuth.getCurrentUser().getUid());
    }

    public static DatabaseReference getUserGroupsRef(FirebaseAuth firebaseAuth) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        return db.getReference().child(USERS).child(firebaseAuth.getCurrentUser().getUid()).child(GROUPS);
    }

    // ------------------------------------ Disciplines --------------------------------------------

    public static DatabaseReference getDisciplinesRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(DISCIPLINES);
    }

    // ------------------------------------ Groepen --------------------------------------------

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


    // ----------------------------------- Cursisten ---------------------------------------- //
    public static DatabaseReference getGroepenCursisten(String groupId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(GROUPS).child(groupId).child(CURSISTEN);
    }


    public static DatabaseReference getCursistenPerGroepCursistBehaaldeEisen(String groupId, String cursistId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(CURSISTEN_PER_GROUP).child(groupId).child(cursistId).child(BEHAALDE_EISEN);
    }

    public static DatabaseReference getBehaaldeEisCursist(String groupId, String cursistId, String eisId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(CURSISTEN_PER_GROUP).child(groupId).child(cursistId).child(BEHAALDE_EISEN).child(eisId);
    }

    public static DatabaseReference getDiplomaCursist(String groupId, String cursistId, String diplomaId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(CURSISTEN_PER_GROUP).child(groupId).child(cursistId).child("diplomas").child(diplomaId);
    }

    public static DatabaseReference getCursistPartial(String groupId, String cursistId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(GROUPS).child(groupId).child(CURSISTEN).child(cursistId);
    }

    public static DatabaseReference getCursist(String groupId, String cursistId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(CURSISTEN_PER_GROUP).child(groupId).child(cursistId);
    }

    /**
     * @param groupId
     * @return
     */
    public static DatabaseReference getCursistenPerGroep(String groupId) {
        // todo is verborgen is false, filter results.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(CURSISTEN_PER_GROUP).child(groupId);
    }

    // ---------------------------------  Diploma eisen -------------------------------------- //

    public static DatabaseReference getExamenEisen(String discipline) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(EXAMENEISEN).child(discipline);
    }

    public static DatabaseReference getSpecificExamDemands(String discipline, String exam) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(EXAMENEISEN).child(discipline).child(exam);
    }

}
