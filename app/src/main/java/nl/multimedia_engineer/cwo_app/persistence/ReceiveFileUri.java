package nl.multimedia_engineer.cwo_app.persistence;

import android.net.Uri;

import java.util.Map;

public interface ReceiveFileUri {
    void onReceiveFileUri(Map<String, Uri> pathUriMap);
    void onReceiveFileUriFailed(String path);
}

