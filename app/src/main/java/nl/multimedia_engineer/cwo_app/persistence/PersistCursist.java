package nl.multimedia_engineer.cwo_app.persistence;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import nl.multimedia_engineer.cwo_app.dto.CursistDTO;
import nl.multimedia_engineer.cwo_app.dto.CursistPartialDTO;
import nl.multimedia_engineer.cwo_app.model.Cursist;
import nl.multimedia_engineer.cwo_app.model.CursistPartial;
import nl.multimedia_engineer.cwo_app.model.Diploma;
import nl.multimedia_engineer.cwo_app.model.DiplomaEis;
import nl.multimedia_engineer.cwo_app.util.DatabaseRefUtil;

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
    }


    private static final String TAG = PersistCursist.class.getSimpleName();

    public static void saveCursist(String groupId, final Cursist cursist, final SavedCursist receiver) {
        DatabaseReference groepenCursistenRef = DatabaseRefUtil.getGroepenCursisten(groupId);
        if(cursist.getId() == null || cursist.getId().isEmpty()) {
            String cursistId = groepenCursistenRef.push().getKey();
            cursist.setId(cursistId);
        }

        if(cursist.getTempImgUri() != null) {
            saveCursistFoto(groupId, cursist);
        }

        CursistPartialDTO cursistPartialDTO = new CursistPartialDTO(cursist);
        CursistDTO cursistDTO = new CursistDTO(cursist);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("cursistenPerGroep/" + groupId + "/" + cursist.getId(), cursistDTO);
        childUpdates.put("groepen/" + groupId + "/cursisten/" + cursist.getId(), cursistPartialDTO);

        rootRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                receiver.onCursistSaved(cursist);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                receiver.onCursistSaveFailed();
            }
        });

    }

    public static boolean saveCursistFoto(String groupId, Cursist cursist) {
        if(cursist.getTempImgUri() == null) {
            return false;
        }

        String path = groupId + "/cursisten/" + cursist.getId();
        String cursistFotoPath = path + ".jgp";
        String cursistThumbnailPath = path + "_thumbnail.jgp";
        StorageReference fotoRef = FirebaseStorage.getInstance().getReference().child(cursistFotoPath);
        StorageReference fotoThumbRef = FirebaseStorage.getInstance().getReference().child(cursistThumbnailPath);

        fotoRef.putFile(cursist.getTempImgUri())
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    int i = 0;
                }
            });

        fotoThumbRef.putFile(cursist.getTempImgUri())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        int i = 0;
                    }
                });

        return true;
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
            Diploma diploma = new Diploma();
            diploma.setId(diplomaSnapShot.getValue(String.class));
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
