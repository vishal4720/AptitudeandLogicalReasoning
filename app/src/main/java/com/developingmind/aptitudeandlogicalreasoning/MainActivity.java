package com.developingmind.aptitudeandlogicalreasoning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.developingmind.aptitudeandlogicalreasoning.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null)
            startActivity(new Intent(this, LoginActivity.class));
        else
            startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}