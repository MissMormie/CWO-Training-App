package nl.multimedia_engineer.cwo_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

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
        showingLoginIfNotLoggedIn();

    }

    public boolean showingLoginIfNotLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isAnonymous()) {
            // TODO send to login activity.
            return true;
        }
        return false;
    }

    public void initializeFireBaseAuth() {
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
    }

}
