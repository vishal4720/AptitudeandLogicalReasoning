package com.developingmind.aptitudeandlogicalreasoning.login;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.HomeActivity;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileEnum;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText email,pass;
    TextView signUp;
    private TextInputLayout emailLayout,passLayout;
    private Button login;
    private DialogMaker dialogMaker;


    private SignInButton googleButton;
    private BeginSignInRequest signInRequest;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    GoogleSignInAccount account;


    private static int oneTapSignIn = 100;

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
        googleButton = (SignInButton) findViewById(R.id.google_button);

        firebaseAuth = FirebaseAuth.getInstance();

        dialogMaker = new DialogMaker(LoginActivity.this);

        googleBtnUi();

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
                if(isEmailValid(email,emailLayout) && isPasswordValid(pass,passLayout)) {
                    signIn(email.getText().toString().trim(), pass.getText().toString());
                }
            }
        });
    }

    private void googleBtnUi() {
        // Initialize sign in options the client-id is copied form google-services.json file
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(googleSignInClient.getSignInIntent(),oneTapSignIn);
            }
        });

        for (int i = 0; i < googleButton.getChildCount(); i++) {
            View v = googleButton.getChildAt(i);

            if (v instanceof TextView)
            {
                TextView tv = (TextView) v;
                tv.setText("Sign In with Google");
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == oneTapSignIn){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = signInAccountTask.getResult(ApiException.class);
                if(account!=null){
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                    firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                updateDatabase(task.getResult().getUser());
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                Log.d("Exception",signInAccountTask.getException().getMessage());
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
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
                            updateDatabase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Incorrect Email / Password",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressBar();
                    }
                });
    }

    private void updateDatabase(FirebaseUser user){
        FirebaseFirestore.getInstance().collection(DatabaseEnum.users.toString())
                .document(user.getUid().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Intent intent;
                            try {
                                Map<String, Object> map = documentSnapshot.getData();
                                Log.d("Map", map.get(ProfileEnum.fname.toString()).toString());
                                Toast.makeText(LoginActivity.this, "Welcome Back " + map.get(ProfileEnum.fname.toString()).toString(), Toast.LENGTH_SHORT).show();
                                if(account!=null && account.getPhotoUrl()!=null){
                                    updateProfile(account.getPhotoUrl());
                                }
                                intent = new Intent(LoginActivity.this, HomeActivity.class);

                            }catch(Exception e){
                                Log.d("Exception",e.getMessage());
                                Toast.makeText(LoginActivity.this, "Please complete Sign Up", Toast.LENGTH_SHORT).show();
                                intent = new Intent(LoginActivity.this,SignUpActivity.class);
                                intent.putExtra(Constants.isLogin,true);
                                intent.putExtra(Constants.profile,account.getPhotoUrl());
                            }
                            hideProgressBar();
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showProgressBar(){
        dialogMaker.getDialog().show();
    }

    public void hideProgressBar(){
        dialogMaker.getDialog().hide();
    }

    private void updateProfile(Uri profile){
        FirebaseFirestore.getInstance().collection(DatabaseEnum.users.toString())
                .document(firebaseAuth.getUid().toString())
                .update(ProfileEnum.profile.toString(),profile.toString());
    }

    public static boolean isEmailValid(EditText email,TextInputLayout emailLayout) {
        Boolean isValid = true;
        if (TextUtils.isEmpty(email.getText())) {
            emailLayout.setError("Email cannot be empty");
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
            passwordLayout.setError("Password cannot be empty");
            isValid=false;
        } else if (password.getText().toString().length()<6) {
            passwordLayout.setError("Password should be minimum of 6 digits");
            isValid=false;
        }
        else{
//            passwordLayout.getEndIconDrawable().setColorFilter(new LightingColorFilter(Color.GREEN,Color.GREEN));
            passwordLayout.setError(null);
        }
        return isValid;
    }

}