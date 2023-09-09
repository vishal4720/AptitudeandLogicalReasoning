package com.developingmind.aptitudeandlogicalreasoning.login;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.DialogCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.HomeActivity;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText email,pass;
    TextView signUp;
    private TextInputLayout emailLayout,passLayout;
    private Button login;
    private DialogMaker dialogMaker;

    ContentLoadingProgressBar contentLoadingProgressBar;


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
        signUp = findViewById(R.id.signup);


        dialogMaker = new DialogMaker(LoginActivity.this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmailValid(email,emailLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPasswordValid(pass,passLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMaker.getDialog().show();
                if(isEmailValid(email,emailLayout) && isPasswordValid(pass,passLayout)) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    signIn(email.getText().toString().trim(), pass.getText().toString());
                }
            }
        });
    }

    private void signIn(String email, String password) {

        showProgressBar();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Welcome Back "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Incorrect Email / Password",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressBar();
                    }
                });
    }

    public void showProgressBar(){
        dialogMaker.getDialog().show();
    }

    public void hideProgressBar(){
        dialogMaker.getDialog().hide();
    }


    public static boolean isEmailValid(EditText email,TextInputLayout emailLayout) {
        Boolean isValid = true;
        if (TextUtils.isEmpty(email.getText())) {
            emailLayout.setError("Enter Email");
            isValid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            emailLayout.setError("Enter Valid Email");
            isValid = false;
        }else{
            emailLayout.getEndIconDrawable().setColorFilter(new LightingColorFilter(Color.GREEN,Color.GREEN));
            emailLayout.setError(null);
        }
        return isValid;
    }

    public static boolean isPasswordValid(EditText password,TextInputLayout passwordLayout){
        Boolean isValid = true;
        if(TextUtils.isEmpty(password.getText())){
            passwordLayout.setError("Enter Password");
            isValid=false;
        } else if (password.getText().toString().length()<6) {
            passwordLayout.setError("Password should be minimum of 6 digits");
            isValid=false;
        }
        else{
            passwordLayout.getEndIconDrawable().setColorFilter(new LightingColorFilter(Color.GREEN,Color.GREEN));
            passwordLayout.setError(null);
        }
        return isValid;
    }

}