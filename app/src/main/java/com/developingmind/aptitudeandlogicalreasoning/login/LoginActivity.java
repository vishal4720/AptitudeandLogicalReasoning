package com.developingmind.aptitudeandlogicalreasoning.login;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText email,pass;
    private Button login;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmailPasswordValid(email,pass)) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    createAccount(email.getText().toString().trim(), pass.getText().toString());
                }
            }
        });
    }

    private void createAccount(String email, String password) {
//        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressBar();
//
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

//                        hideProgressBar();
                    }
                });
    }

    private void signIn(String email, String password) {
//        Log.d(TAG, "signIn:" + email);
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressBar();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                            checkForMultiFactorFailure(task.getException());
                        }

//                        if (!task.isSuccessful()) {
//                            mBinding.status.setText(R.string.auth_failed);
//                        }
//                        hideProgressBar();
                    }
                });
    }


    public static boolean isEmailPasswordValid(EditText email,EditText password) {
        Boolean isValid = true;
        if (TextUtils.isEmpty(email.getText())) {
            email.setError("Enter Email");
            isValid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.setError("Enter Valid Email");
            isValid = false;
        }
        if(TextUtils.isEmpty(password.getText())){
            password.setError("Enter Password");
            isValid=false;
        } else if (password.getText().toString().length()<=6) {
            password.setError("Password should be minimum of 6 digits");
            isValid=false;
        }
        return isValid;
    }

}