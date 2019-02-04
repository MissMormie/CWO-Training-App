package nl.multimedia_engineer.cwo_app.util;

import android.content.Context;
import android.widget.Toast;

public class ConnectionIssuesUtil {

    public static void unableToConnect(Context context) {
        Toast.makeText(context, "Unable to connect", Toast.LENGTH_SHORT).show();
        // todo
    }


}
