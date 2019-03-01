package nl.multimedia_engineer.cwo_app.persistence;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class PersistPhoto {

    public interface ReceivePhoto {
        void onReceivePhotoUri(Uri uri);
    }

    protected static void savePhoto(String groupId, File photoFile, String path) {
        Uri uri = Uri.fromFile(photoFile);
        StorageReference fotoRef = FirebaseStorage.getInstance().getReference().child(path);

        // Should add success & failure listener when I intend to do something with the result.
        fotoRef.putFile(uri);
    }

    public static void getPhoto(String path, final ReceivePhoto receiver) {
        StorageReference fotoRef = FirebaseStorage.getInstance().getReference().child(path);
        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                receiver.onReceivePhotoUri(uri);
            }
        });
    }
}
