package com.developingmind.aptitudeandlogicalreasoning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.developingmind.aptitudeandlogicalreasoning.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        getFirebaseMessagingTokken();

        SharedPreferences purchaseSharedPreference = getSharedPreferences("PurchasePref",MODE_PRIVATE);
        AdManager adManager = (AdManager) getApplicationContext();
        adManager.setIsPurchased(purchaseSharedPreference.getBoolean("purchase",false));
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
        if (sharedPreferences.getInt("share_count",-1)<0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("share_count",3);
            editor.apply();
        }

        if(firebaseAuth.getCurrentUser() == null)
            startActivity(new Intent(this, LoginActivity.class));
        else
            startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void getFirebaseMessagingTokken(){
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        Log.d("Tokken",task.getResult());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tokken",e.getMessage());
                    }
                });
    }
}