package nl.multimedia_engineer.watersport_training.persistence;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PersistPhoto implements ReceiveFileUri {
    private ReceiveFileUri receivePhoto;
    private int photosToReceive = 0;
    private Map<String, Uri> pathUriMap;
    private int photosReceived = 0;


    public void saveFiles(String groupId, Map<String, File> pathFileMap, ReceiveFileUri receiver) {
        receivePhoto = receiver;
        photosToReceive = pathFileMap.size();

        for(Map.Entry<String, File> entry : pathFileMap.entrySet()) {
            saveFile(groupId, entry.getValue(), entry.getKey(), this);
        }
    }

    protected void saveFile(String groupId, File photoFile, final String path, final ReceiveFileUri receiver) {
        Uri uri = Uri.fromFile(photoFile);
        final StorageReference fotoRef = FirebaseStorage.getInstance().getReference().child(path);
        fotoRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map map = new HashMap<>();
                        map.put(path, uri);
                        receiver.onReceiveFileUri(map);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public static void getFile(final String path, final ReceiveFileUri receiver) {
        StorageReference fotoRef = FirebaseStorage.getInstance().getReference().child(path);
        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Map map = new HashMap<>();
                map.put(path, uri);
                receiver.onReceiveFileUri(map);
            }
        });
    }

    @Override  public void onReceiveFileUri(Map<String, Uri> pathUriMap) {
        photosReceived += pathUriMap.size();

        if(this.pathUriMap == null) {
            this.pathUriMap = new HashMap<>();
        }
        this.pathUriMap.putAll(pathUriMap);
        if(photosReceived >= photosToReceive) {
            receivePhoto.onReceiveFileUri(this.pathUriMap);
        }
    }

    @Override
    public void onReceiveFileUriFailed(String path) {
        photosReceived++;

        if(photosReceived >= photosToReceive) {
            if(pathUriMap != null && !pathUriMap.isEmpty()) {
                receivePhoto.onReceiveFileUri(pathUriMap);
            } else {
                receivePhoto.onReceiveFileUriFailed(path);
            }
        }
    }

}
