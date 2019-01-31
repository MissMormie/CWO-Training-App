package nl.multimedia_engineer.cwo_app;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {
    final private String TAG = "LoginActivity";
    final private String STATE_LOGIN = "Login";
    final private String STATE_REGISTER = "Register";

    private EditText editEmail, editPassword;
    private Button btnAction;
    private TextView textSwitchLoginRegister;

    private String currentState = STATE_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnAction = findViewById(R.id.btn_login);
        textSwitchLoginRegister = findViewById(R.id.text_login_register_switch);

        // Set on click listener to switch between login and register.
        View.OnClickListener textSwitchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchState();
            }
        };
        textSwitchLoginRegister.setOnClickListener(textSwitchClickListener);

        View.OnClickListener btnActionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction();
            }
        };
        btnAction.setOnClickListener(btnActionClickListener);
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
        return (!editPassword.getText().toString().isEmpty() &&
            !editEmail.getText().toString().isEmpty());
    }

    private void login(){
        // todo
    }

    private void register() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void switchState() {
        if(currentState.equals(STATE_LOGIN)) {
            currentState = STATE_REGISTER;
            btnAction.setText(R.string.btn_register);
            textSwitchLoginRegister.setText(R.string.text_register);
        } else {
            currentState = STATE_LOGIN;
            btnAction.setText(R.string.btn_login);
            textSwitchLoginRegister.setText(R.string.text_login);
        }
    }
}
