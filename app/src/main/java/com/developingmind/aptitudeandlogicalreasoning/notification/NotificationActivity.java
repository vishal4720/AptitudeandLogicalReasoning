package com.developingmind.aptitudeandlogicalreasoning.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionModal;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    Toolbar toolbar;
    List<NotificationModal> notificationModalList = new ArrayList<NotificationModal>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();

        getNotificationData();
    }

    private void getNotificationData(){
        String json = sharedPreferences.getString("notification","");

        Log.d("Notification",json);

        Type type = new TypeToken<List<NotificationModal>>(){}.getType();

        notificationModalList = gson.fromJson(json,type);
        if (notificationModalList == null){
            notificationModalList = new ArrayList<NotificationModal>();
        }else{
            List<NotificationModal> noti = new ArrayList<>();
            for (NotificationModal n:
                 notificationModalList) {
                if(n.getNotificationTitle()!=null && !n.getNotificationTitle().isEmpty()){
                    noti.add(n);
                }
            }
            notificationModalList.clear();
            notificationModalList.addAll(noti);
            Collections.reverse(notificationModalList);
            notificationAdapter = new NotificationAdapter(this,notificationModalList);
            recyclerView.setAdapter(notificationAdapter);
        }
    }
}