package com.developingmind.aptitudeandlogicalreasoning.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.HomeActivity;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.profile.Gender;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileEnum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout fname,lname,email,pass,repass,date;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Button register;


    private DialogMaker dialogMaker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        fname = findViewById(R.id.fname_layout_signup);
        lname = findViewById(R.id.lname_layout_signup);
        email = findViewById(R.id.email_layout_signup);
        pass = findViewById(R.id.password_layout_signup);
        repass = findViewById(R.id.repassword_layout_signup);
        date = findViewById(R.id.date_layout_signup);
        register = findViewById(R.id.signup_btn);

        dialogMaker = new DialogMaker(SignUpActivity.this);

        date.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                date.getEditText().setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                            },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                        // at last we are calling show to
                        // display our date picker dialog.
                        datePickerDialog.show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmailValid(email) && isPasswordValid(pass,repass) && !fname.getEditText().getText().toString().isEmpty()
                && !lname.getEditText().getText().toString().isEmpty() && !date.getEditText().getText().toString().isEmpty() ) {
                    showProgressBar();
                    Map<String, Object> u = new HashMap<>();
                    u.put(ProfileEnum.fname.toString(), fname.getEditText().getText().toString().trim());
                    u.put(ProfileEnum.lname.toString(), lname.getEditText().getText().toString().trim());
                    u.put(ProfileEnum.dob.toString(), date.getEditText().getText().toString().trim());
                    u.put(ProfileEnum.gender.toString(), Gender.Female.toString());
                    firebaseAuth = FirebaseAuth.getInstance();
                    createAccount(email.getEditText().getText().toString().trim(), pass.getEditText().getText().toString().trim(), u);
                }
            }

        });

    }




    private void createAccount(String email, String password, Map<String, Object> u) {
//        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressBar();
//
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            uploadData(u,user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                        }
                    }
                });
    }

    private void uploadData(Map<String, Object> u,FirebaseUser user){
        boolean failed = false;

        firebaseFirestore = FirebaseFirestore.getInstance();

        // Creating New Collection in Firestore
        firebaseFirestore.collection(DatabaseEnum.users.toString())
                .document(user.getUid())
                .set(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        hideProgressBar();
                        Toast.makeText(SignUpActivity.this, "Welcome " + u.get(ProfileEnum.fname.toString()), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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



    public boolean isEmailValid(TextInputLayout emailLayout) {
        Boolean isValid = true;
        if (TextUtils.isEmpty(emailLayout.getEditText().getText())) {
            emailLayout.setError("Enter Email");
            isValid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailLayout.getEditText().getText()).matches()) {
            emailLayout.setError("Enter Valid Email");
            isValid = false;
        }else{
            emailLayout.getEndIconDrawable().setColorFilter(new LightingColorFilter(Color.GREEN,Color.GREEN));
            emailLayout.setError(null);
        }
        return isValid;
    }

    public boolean isPasswordValid(TextInputLayout passwordLayout ,TextInputLayout repassLayout ){
        Boolean isValid = true;
        if(TextUtils.isEmpty(passwordLayout.getEditText().getText())){
            passwordLayout.setError("Enter Password");
            isValid=false;
        } else if (passwordLayout.getEditText().getText().toString().length()<6) {
            passwordLayout.setError("Password should be minimum of 6 digits");
            isValid=false;
        } else if (!repassLayout.getEditText().getText().toString().equals(passwordLayout.getEditText().getText().toString())) {
            repassLayout.setError("Password does not match");
            isValid=false;

        } else{
            passwordLayout.getEndIconDrawable().setColorFilter(new LightingColorFilter(Color.GREEN,Color.GREEN));
            passwordLayout.setError(null);
        }
        return isValid;
    }
}

