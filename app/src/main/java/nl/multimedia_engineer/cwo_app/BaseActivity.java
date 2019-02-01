package nl.multimedia_engineer.cwo_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Takes care of checking login before showing content.
 */
public abstract class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFireBaseAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        showingLoginIfNotLoggedIn(this);
    }

    public boolean showingLoginIfNotLoggedIn(Context context) {
        if(context.getClass() == LoginActivity.class) {
            return true;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null || currentUser.isAnonymous()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void initializeFireBaseAuth() {
        FirebaseApp.initializeApp(this);
        FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

}
