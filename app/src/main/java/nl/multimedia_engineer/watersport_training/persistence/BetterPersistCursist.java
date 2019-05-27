package nl.multimedia_engineer.watersport_training.persistence;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import nl.multimedia_engineer.watersport_training.dto.CursistDTO;
import nl.multimedia_engineer.watersport_training.dto.CursistPartialDTO;
import nl.multimedia_engineer.watersport_training.model.Cursist;
import nl.multimedia_engineer.watersport_training.util.DatabaseRefUtil;

public class BetterPersistCursist implements ReceiveFileUri {

    public interface SavedCursist {
        void onCursistSaved(Cursist cursist);
        void onCursistSaveFailed();
    }

    public interface DeletedCursist {
        void onCursistDeleted();
        void onCursistDeleteFailed();
    }

    final static String ACTION_SAVE = "actionSave";
    String action;
    final String groupId;
    private Cursist mCursist;
    private PersistCursist.SavedCursist mSavedCursistReceiver;

    public BetterPersistCursist(String groupId, Cursist cursist) {
        this.mCursist = cursist;
        this.groupId = groupId;
    }

    public void requestDeleteCursist(final Cursist cursist, final DeletedCursist receiver) {
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

        // todo delete images of mCursist
    }

    public void saveCursistWithPhotos(final PersistCursist.SavedCursist receiver) {
        action = ACTION_SAVE;
        this.mSavedCursistReceiver = receiver;

        setCursistId();

        // Callback for save Cursist Foto saves cursist.
        saveCursistFoto(groupId, mCursist);

    }

    public void saveCursist(final PersistCursist.SavedCursist receiver) {
        this.mSavedCursistReceiver = receiver;

        if(mCursist.getId() == null || mCursist.getId().isEmpty()) {
            setCursistId();
        }
        CursistPartialDTO cursistPartialDTO = new CursistPartialDTO(mCursist);
        CursistDTO cursistDTO = new CursistDTO(mCursist);

        // This should never happen, but if it does it'll mess up the database badly, so added just in case check.
        if(groupId == null || groupId.isEmpty() || mCursist.getId() == null || mCursist.getId().isEmpty()) {
            System.out.println("Cursist Id or Group Id not set");
            mSavedCursistReceiver.onCursistSaveFailed();
            return;
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("cursistenPerGroep/" + groupId + "/" + mCursist.getId(), cursistDTO);
        childUpdates.put("groepen/" + groupId + "/cursisten/" + mCursist.getId(), cursistPartialDTO);

        rootRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mSavedCursistReceiver.onCursistSaved(mCursist);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mSavedCursistReceiver.onCursistSaveFailed();
            }
        });

    }

    private void setCursistId() {
        if(mCursist.getId() == null || mCursist.getId().isEmpty()) {
            DatabaseReference groepenCursistenRef = DatabaseRefUtil.getGroepenCursisten(groupId);
            String cursistId = groepenCursistenRef.push().getKey();
            mCursist.setId(cursistId);
        }
    }

    private void saveCursistFoto(String groupId, Cursist cursist) {
        String path = groupId + "/cursisten/" + cursist.getId()+ "/foto";
        String suffix = ".jgp";

        Map map = new HashMap<>();

        if(cursist.getPhotoFileLarge() != null) {
            String namedPath = path +"_large" + suffix;
            map.put(namedPath, cursist.getPhotoFileLarge());
        }

        if(cursist.getPhotoFileNormal() != null) {
            String namedPath = path + suffix;
            map.put(namedPath, cursist.getPhotoFileNormal());
        }

        if(cursist.getPhotoFileThumbnail() != null) {
            String namedPath = path +"_thumbnail" + suffix;
            map.put(namedPath, cursist.getPhotoFileThumbnail());
        }

        if(!map.isEmpty()) {
            PersistPhoto persistPhoto = new PersistPhoto();
            persistPhoto.saveFiles(groupId, map, this);
        }
    }


    @Override
    public void onReceiveFileUri(Map<String, Uri> pathUriMap) {
        for(Map.Entry<String, Uri> entry : pathUriMap.entrySet()) {
            String firebasePath = entry.getKey();
            if(firebasePath.contains("_large")) {
                mCursist.setPhotoPathLarge(entry.getValue().toString());
            } else if(entry.getKey().contains("_thumbnail")) {
                mCursist.setPhotoPathThumbnail(entry.getValue().toString());
            } else {
                mCursist.setPhotoPathNormal(entry.getValue().toString());
            }
        }

        if(action.equals(ACTION_SAVE)) {
            saveCursist(mSavedCursistReceiver);
        }
    }

    @Override
    public void onReceiveFileUriFailed(String path) {
        mSavedCursistReceiver.onCursistSaveFailed();
    }


}
