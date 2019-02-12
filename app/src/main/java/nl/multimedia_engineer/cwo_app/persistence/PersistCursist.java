package nl.multimedia_engineer.cwo_app.persistence;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nl.multimedia_engineer.cwo_app.dto.CursistDTO;
import nl.multimedia_engineer.cwo_app.dto.CursistPartialDTO;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class PersistCursist {
    public interface ReceiveCursistList {
        void receiveCursistList(List<Cursist> cursistList);
        void receiveCursistListFailed();
    }

    private static final String TAG = PersistCursist.class.getSimpleName();

    public static void saveCursist(String groupId, Cursist cursist) {
        DatabaseReference groepenCursistenRef = DatabaseRefUtil.getGroepenCursisten(groupId);
        String cursistId = groepenCursistenRef.push().getKey();
        cursist.setId(cursistId);

        CursistPartialDTO cursistPartialDTO = new CursistPartialDTO(cursist);

        groepenCursistenRef.child(cursistId).setValue(cursistPartialDTO);
        Log.d(TAG, "saveCursist: " + cursistId);

        DatabaseReference cursistGroepRef = DatabaseRefUtil.getCursistenPerGroepCursist(groupId, cursistId);
        CursistDTO cursistDTO = new CursistDTO(cursist);
        cursistGroepRef.setValue(cursistDTO);

    }

    /**
     * Cursist needs to contain cursistId.
     * @param groupId
     * @param cursist
     */
    public static void updateCursist(String groupId, Cursist cursist) {

    }

    // todo remove hardcoded values.
    public static void getCursistList(String groupId, final ReceiveCursistList receiver) {
        DatabaseReference databaseReference = DatabaseRefUtil.getCursistenPerGroep(groupId, false);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Cursist> cursistList = new ArrayList<>();
                for(DataSnapshot cursistSnapshot : dataSnapshot.getChildren()) {
                    Cursist cursist = cursistSnapshot.getValue(Cursist.class);
                    for(DataSnapshot diplomaSnapShot : cursistSnapshot.child("diplomas").getChildren()) {
                        Diploma diploma = diplomaSnapShot.getValue(Diploma.class);
                        cursist.addDiploma(diploma);
                    }

                    for(DataSnapshot behaaldeEisenSnapShot : cursistSnapshot.child("behaalde eisen").getChildren()) {
                        DiplomaEis diplomaEis = new DiplomaEis();
                        diplomaEis.setId((String) behaaldeEisenSnapShot.getValue());
                    }
                    cursistList.add(cursist);
                }


                receiver.receiveCursistList(cursistList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.receiveCursistListFailed();
            }
        });
    }

    /**
     *
     * @param groupId
     * @param cursistID
     * @param examenEisId
     * @param delete, indicates if the value needs to be added or deleted.
     */
    public static void updateCursistBehaaldExamenEis(String groupId, String cursistID, String examenEisId, boolean delete) {
        DatabaseReference databaseReference = DatabaseRefUtil.getBehaaldeEisCursist(groupId, cursistID, examenEisId);
        if(delete == false) {
            databaseReference.setValue(examenEisId);
        } else {
            databaseReference.removeValue();
        }
    }
}
