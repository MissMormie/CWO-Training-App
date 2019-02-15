package nl.multimedia_engineer.cwo_app.persistence;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.dto.CursistDTO;
import nl.multimedia_engineer.cwo_app.dto.CursistPartialDTO;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.CursistPartial;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

public class PersistCursist {
    public interface ReceiveCursistList {
        void receiveCursistList(List<Cursist> cursistList);
        void receiveCursistListFailed();
    }

    public interface ReceiveCursistPartialList {
        void receiveCursistPartialList(List<CursistPartial> cursistPartialList);
        void receiveCursistPartialListFailed();
    }

    public interface ReceiveCursist{
        void receiveCursist(Cursist cursist);
        void receiveCursistFailed();
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
                    Cursist cursist = getCursist(cursistSnapshot);
                    if(cursist != null) {
                        cursistList.add(cursist);
                    }
                }
                receiver.receiveCursistList(cursistList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.receiveCursistListFailed();
            }
        });
    }

    public static void getCursistPartialList(String groupId, final ReceiveCursistPartialList receiver) {
        DatabaseReference groepenCursistenRef = DatabaseRefUtil.getGroepenCursisten(groupId);
        groepenCursistenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, CursistPartial>> type = new GenericTypeIndicator<HashMap<String, CursistPartial>>() {};
                HashMap<String, CursistPartial> result = dataSnapshot.getValue(type);
                List<CursistPartial> cursistList = new ArrayList<>();
                if(result == null) { // Er zijn nog geen cursisten in deze groep
                    receiver.receiveCursistPartialList(null);
                    return;
                }
                for(Map.Entry<String, CursistPartial> entry : result.entrySet()) {
                    entry.getValue().setId(entry.getKey());
                    cursistList.add(entry.getValue());
                }

                receiver.receiveCursistPartialList(cursistList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.receiveCursistPartialListFailed();
            }
        });

    }

    public static void getCursist(String groupId, String cursistId, final ReceiveCursist receiver) {
        DatabaseReference cursistRef = DatabaseRefUtil.getCursist(groupId, cursistId);
        cursistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                receiver.receiveCursist(getCursist(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.receiveCursistFailed();
            }
        });
    }

    private static Cursist  getCursist(DataSnapshot cursistSnapshot) {
        Cursist cursist = cursistSnapshot.getValue(Cursist.class);
        for(DataSnapshot diplomaSnapShot : cursistSnapshot.child("diplomas").getChildren()) {
            Diploma diploma = new Diploma();
            diploma.setId(diplomaSnapShot.getValue(String.class));
            cursist.addDiploma(diploma);
        }

        for(DataSnapshot behaaldeEisenSnapShot : cursistSnapshot.child("behaalde eisen").getChildren()) {
            DiplomaEis diplomaEis = new DiplomaEis();
            diplomaEis.setId((String) behaaldeEisenSnapShot.getValue());
            cursist.addDiplomeEis(diplomaEis);
        }

        return cursist;
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
        if(delete) {
            databaseReference.removeValue();
        } else {
            databaseReference.setValue(examenEisId);
        }
    }

    /**
     * Adds the diploma to cursist info, or removes it if delete is true
     * @param groupId
     * @param curistId
     * @param diplomaId
     * @param delete
     */
    public static void saveCursistDiploma(String groupId, String curistId, String diplomaId, boolean delete) {
        DatabaseReference databaseReference = DatabaseRefUtil.getDiplomaCursist(groupId, curistId, diplomaId);
        if(delete) {
            databaseReference.removeValue();
        } else {
            databaseReference.setValue(diplomaId);
        }
    }


}
