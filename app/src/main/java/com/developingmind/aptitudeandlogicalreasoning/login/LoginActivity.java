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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private EditText email,pass;
    private TextInputLayout emailLayout,passLayout;
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
        emailLayout = findViewById(R.id.email_layout);
        pass = findViewById(R.id.password);
        passLayout = findViewById(R.id.password_layout);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmailPasswordValid(email,pass,emailLayout,passLayout)) {
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
                            uploadData("Vishal","Patole","04/07/2000",user);
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

    private void uploadData(String first,String last,String dob,FirebaseUser user){
        boolean failed = false;

        firebaseFirestore = FirebaseFirestore.getInstance();
        Map<String, Object> u = new HashMap<>();
        u.put("first", first);
        u.put("last", last);
        u.put("dob", dob);

        // Creating New Collection in Firestore
        firebaseFirestore.collection("users")
                .document(user.getUid())
                .set(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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


    public static boolean isEmailPasswordValid(EditText email,EditText password,TextInputLayout emailLayout,TextInputLayout passwordLayout) {
        Boolean isValid = true;
        if (TextUtils.isEmpty(email.getText())) {
            emailLayout.setError("Enter Email");
            isValid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            emailLayout.setError("Enter Valid Email");
            isValid = false;
        }
        if(TextUtils.isEmpty(password.getText())){
            passwordLayout.setError("Enter Password");
            isValid=false;
        } else if (password.getText().toString().length()<=6) {
            passwordLayout.setError("Password should be minimum of 6 digits");
            isValid=false;
        }
        return isValid;
    }

}