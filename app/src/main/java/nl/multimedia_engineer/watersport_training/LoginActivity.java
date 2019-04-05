package nl.multimedia_engineer.watersport_training;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import nl.multimedia_engineer.watersport_training.util.ValidationUtil;

public class LoginActivity extends BaseActivity {
    private final String TAG = "LoginActivity";
    private final String STATE_LOGIN = "Login";
    private final String STATE_REGISTER = "Register";
    private String currentState = STATE_LOGIN;

    // UI elements
    private EditText editEmail, editPassword;
    private Button btnAction;
    private TextView textSwitchLoginRegister;
    private ScrollView svTermsAndConditions;
    private CheckBox cbConditions;
    private TextView tvConditions;

    private ProgressBar progressBar;

    // On click listeners
    View.OnClickListener textSwitchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switchState();
        }
    };

    View.OnClickListener btnActionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doAction();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnAction = findViewById(R.id.btn_login);
        textSwitchLoginRegister = findViewById(R.id.text_login_register_switch);
        svTermsAndConditions = findViewById(R.id.sv_terms_and_conditions);
        progressBar = findViewById(R.id.pb_login);
        cbConditions = findViewById(R.id.checkBox_conditions);
        tvConditions = findViewById(R.id.tv_conditions);

        // Set on click listeners.
        setUserInteraction(true);
    }

    private void doAction() {
        if(!fieldsFilledCorrectly()) {
            Toast.makeText(this, R.string.text_fields_filled_wrong,Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentState.equals(STATE_LOGIN)) {
            login();
        } else {
            register();
        }
    }

    private boolean fieldsFilledCorrectly() {
        if(currentState == STATE_REGISTER) {
            if(!cbConditions.isChecked()) {
                return false;
            }
        }
        return (!editPassword.getText().toString().isEmpty() &&
                ValidationUtil.isValidEmailAddress(editEmail.getText().toString()));
    }

    private void login(){
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        setUserInteraction(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMenu(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            setUserInteraction(true);
                        }
                    }
                });
    }

    /**
     * sets onClickListeners to on or off.
     * @param active
     */


    private void setUserInteraction(boolean active) {
        if(active) {
            textSwitchLoginRegister.setOnClickListener(textSwitchClickListener);
            btnAction.setOnClickListener(btnActionClickListener);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            textSwitchLoginRegister.setOnClickListener(null);
            btnAction.setOnClickListener(null);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void register() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        setUserInteraction(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMenu(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            setUserInteraction(true);
                        }
                    }
                });
    }

    private void goToMenu(FirebaseUser user) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Different user logging in. Removing current preferences.
        if(!sharedPreferences.getString(getString(R.string.pref_current_user),"").equals(user.getUid())) {
            sharedPreferences.edit().remove(getString(R.string.pref_current_group_id)).apply();
            sharedPreferences.edit().remove(getString(R.string.pref_current_group_name)).apply();
            sharedPreferences.edit().remove(getString(R.string.pref_discipline)).apply();
            sharedPreferences.edit().putString(getResources().getString(R.string.pref_current_user), user.getUid()).apply();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void switchState() {
        if(currentState.equals(STATE_LOGIN)) {
            currentState = STATE_REGISTER;
            btnAction.setText(R.string.btn_register);
            svTermsAndConditions.setVisibility(View.VISIBLE);
            tvConditions.setVisibility(View.VISIBLE);
            cbConditions.setVisibility(View.VISIBLE);
            textSwitchLoginRegister.setText(R.string.text_register);
        } else {
            currentState = STATE_LOGIN;
            svTermsAndConditions.setVisibility(View.GONE);
            tvConditions.setVisibility(View.GONE);
            cbConditions.setVisibility(View.GONE);
            btnAction.setText(R.string.btn_login);
            textSwitchLoginRegister.setText(R.string.text_login);
        }
    }
}
