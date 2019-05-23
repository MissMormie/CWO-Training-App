package nl.multimedia_engineer.watersport_training.persistence;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.model.CursistPartial;
import nl.multimedia_engineer.watersport_training.model.Diploma;
import nl.multimedia_engineer.watersport_training.model.DiplomaEis;
import nl.multimedia_engineer.watersport_training.util.DatabaseRefUtil;

public class PersistCursist {
    public interface ReceiveCursistList {
        void onReceiveCursistList(List<Cursist> cursistList);
        void onReceiveCursistListFailed();
    }

    public interface ReceiveCursistPartialList {
        void onReceiveCursistPartialList(List<CursistPartial> cursistPartialList);
        void onReceiveCursistPartialListFailed();
    }

    public interface ReceiveCursist{
        void onReceiveCursist(Cursist cursist);
        void onReceiveCursistFailed();
    }

    public interface SavedCursist {
        void onCursistSaved(Cursist cursist);
        void onCursistSaveFailed();
    }

    public interface DeletedCursist {
        void onCursistDeleted();
        void onCursistDeleteFailed();
    }

    private static final String TAG = PersistCursist.class.getSimpleName();


    public static void requestDeleteCursist(final String groupId, final Cursist cursist, final DeletedCursist receiver) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("cursistenPerGroep/" + groupId + "/" + cursist.getId(), null);
        childUpdates.put("groepen/" + groupId + "/cursisten/" + cursist.getId(), null);
        rootRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                receiver.onCursistDeleted();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                receiver.onCursistDeleteFailed();
            }
        });

        // Firebase does not allow to delete a folder, so we need to delete the various images directly.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference cursistFilesRef = storage.getReference().child(groupId).child("cursisten").child(cursist.getId());
        deleteFile(cursistFilesRef, cursist.getPhotoPathLarge());
        deleteFile(cursistFilesRef, cursist.getPhotoPathNormal());
        deleteFile(cursistFilesRef, cursist.getPhotoPathThumbnail());
    }


    private static void deleteFile(StorageReference cursistFileRef, String urlPath) {
        if(urlPath == null)
            return;

        Uri uri = Uri.parse(urlPath);
        String filename = new File(uri.getPath()).getName();
        cursistFileRef.child(filename).delete();
    }

    @Deprecated
    public static void getCursistList(String groupId, final ReceiveCursistList receiver) {
        getCursistList(groupId, receiver, true);
    }

    @Deprecated
    private static void doGetCursistListQuery(Query query, final ReceiveCursistList receiver, final @Nullable String startAtId ) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Cursist> cursistList = new ArrayList<>();
                for(DataSnapshot cursistSnapshot : dataSnapshot.getChildren()) {
                    Cursist cursist = getCursist(cursistSnapshot);
                    if(cursist != null) {
                        cursistList.add(cursist);
                    }
                }
                if(startAtId != null ) {
                    cursistList.remove(0);
                }
                receiver.onReceiveCursistList(cursistList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.onReceiveCursistListFailed();
            }
        });
    }

    // TODO should move these to Persist Cursist List
    public static void getCursistList(String groupId, final ReceiveCursistList receiver, boolean includeVerborgen) {
        DatabaseReference databaseReference = DatabaseRefUtil.getCursistenPerGroep(groupId);
        Query query;
        if(!includeVerborgen) {
            query = databaseReference.orderByChild("verborgen").equalTo(false);
        } else {
            query = databaseReference.orderByChild("voornaam");
        }
        doGetCursistListQuery(query, receiver, null);
    }



    public static void getCursistPartialList(String groupId, final ReceiveCursistPartialList receiver) {
        DatabaseReference groepenCursistenRef = DatabaseRefUtil.getGroepenCursisten(groupId);
        Query query = groepenCursistenRef.orderByChild("voornaam");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, CursistPartial>> type = new GenericTypeIndicator<HashMap<String, CursistPartial>>() {};
                HashMap<String, CursistPartial> result = dataSnapshot.getValue(type);
                List<CursistPartial> cursistList = new ArrayList<>();
                if(result == null) { // Er zijn nog geen cursisten in deze groep
                    receiver.onReceiveCursistPartialList(null);
                    return;
                }

                for(Map.Entry<String, CursistPartial> entry : result.entrySet()) {
                    entry.getValue().setId(entry.getKey());
                    cursistList.add(entry.getValue());
                }

                receiver.onReceiveCursistPartialList(cursistList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.onReceiveCursistPartialListFailed();
            }
        });
    }

    public static void getCursist(String groupId, String cursistId, final ReceiveCursist receiver) {
        DatabaseReference cursistRef = DatabaseRefUtil.getCursist(groupId, cursistId);
        cursistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                receiver.onReceiveCursist(getCursist(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                receiver.onReceiveCursistFailed();
            }
        });
    }

    static Cursist  getCursist(DataSnapshot cursistSnapshot) {
        Cursist cursist = cursistSnapshot.getValue(Cursist.class);
        for(DataSnapshot diplomaSnapShot : cursistSnapshot.child("diplomas").getChildren()) {
            Diploma diploma = new Diploma(diplomaSnapShot.getValue(String.class));
            cursist.addDiploma(diploma);
        }

        for(DataSnapshot behaaldeEisenSnapShot : cursistSnapshot.child(DatabaseRefUtil.BEHAALDE_EISEN).getChildren()) {
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
     * @param cursist
     * @param diplomaId
     * @param delete
     */
    public static void saveCursistDiploma(String groupId, Cursist cursist, String diplomaId, boolean delete) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        String diplomaPath = "cursistenPerGroep/" + groupId + "/" + cursist.getId() + "/diplomas/" +diplomaId;
        String cursistenPerGroepHoogsteDiploma = "cursistenPerGroep/" + groupId + "/" + cursist.getId() + "/hoogsteDiploma";
        String hoogsteDiplomaPath = "groepen/" + groupId + "/cursisten/" + cursist.getId() + "/hoogsteDiploma";

        if(delete) {
            childUpdates.put(diplomaPath, null);
        } else {
            childUpdates.put(diplomaPath, diplomaId);
        }

        childUpdates.put(hoogsteDiplomaPath, cursist.getHoogsteDiploma());
        childUpdates.put(cursistenPerGroepHoogsteDiploma, cursist.getHoogsteDiploma());
        rootRef.updateChildren(childUpdates);
    }

    public static void updateCursistVerborgen(String groupId, String cursistId, boolean verborgen) {
        DatabaseReference cursistRef = DatabaseRefUtil.getCursist(groupId, cursistId).child("verborgen");
        cursistRef.setValue(verborgen);

        DatabaseReference cursistPartialRef = DatabaseRefUtil.getCursistPartial(groupId, cursistId).child("verborgen");
        cursistPartialRef.setValue(verborgen);
    }

    public static void updateCursistPaspoort(String groupId, String cursistId, Long paspoort) {
        DatabaseReference cursistRef = DatabaseRefUtil.getCursist(groupId, cursistId).child("paspoort");
        if(paspoort == null || paspoort == 0L) {
            cursistRef.removeValue();
        } else {
            cursistRef.setValue(paspoort);
        }
    }
}
