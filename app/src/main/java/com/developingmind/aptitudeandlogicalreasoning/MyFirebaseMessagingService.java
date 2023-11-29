package com.developingmind.aptitudeandlogicalreasoning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.developingmind.aptitudeandlogicalreasoning.notification.NotificationModal;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        NotificationModal notificationModal;
        String title;
        Log.d("Background Notification",intent.getExtras().toString());
        try {
            title = intent.getExtras().getString("gcm.notification.title");
        }catch (Exception e){
            title =null;
        }

        String desc;
        try {
            desc = intent.getExtras().getString("gcm.notification.body");
        }catch (Exception e){
            desc =null;
        }

        String imageUrl;
        try {
            imageUrl = intent.getExtras().getString("gcm.notification.image");
        }catch (Exception e){
            imageUrl = null;
        }


        String categoryId,division,topicId,url;
        try {
            categoryId = intent.getExtras().getString("categoryId");
        }catch (Exception e){
            categoryId = null;
        }
        try {
            division = intent.getExtras().getString("division");
        }catch (Exception e){
            division = null;
        }
        try {
            topicId = intent.getExtras().getString("topicId");
        }catch (Exception e){
            topicId = null;
        }
        try {
            url = intent.getExtras().getString("url");
        }catch (Exception e){
            url = null;
        }


        if (categoryId!=null && division!=null && topicId!=null) {
            notificationModal = (new NotificationModal(title, desc, imageUrl, categoryId, division, topicId,""));
        }
        else if(categoryId!=null && division!=null){
            notificationModal = (new NotificationModal(title,desc,imageUrl,categoryId,division,"",""));
        }
        else if(categoryId!=null){
            notificationModal = (new NotificationModal(title,desc,imageUrl,categoryId,"","",""));
        }
        else if(url!=null)
            notificationModal = (new NotificationModal(title,desc,imageUrl,"","","",url));
        else
            notificationModal = (new NotificationModal(title,desc,imageUrl,"","","",""));

        if(title!=null && desc!=null)
            storeNotification(notificationModal);
    }

//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage message) {
//
//        NotificationModal notificationModal;
//        String title = message.getNotification().getTitle();
//        String desc = message.getNotification().getBody();
//        String imageUrl = null;
//        if(message.getNotification().getImageUrl()!=null){
//            imageUrl = message.getNotification().getImageUrl().toString();
//        }
//        String categoryId=null,division=null,topicId=null,url=null;
//        if(message.getData().get("categoryId")!=null){
//            categoryId = message.getData().get("categoryId").toString();
//        }
//        if (message.getData().get("division")!=null){
//            division = message.getData().get("division").toString();
//        }
//        if (message.getData().get("topicId")!=null){
//           topicId = message.getData().get("topicId").toString();
//        }
//        if (message.getData().get("url")!=null){
//            url = message.getData().get("url").toString();
//        }
//
//        if (categoryId!=null && division!=null && topicId!=null) {
//            notificationModal = (new NotificationModal(title, desc, imageUrl, categoryId, division, topicId,""));
//        }
//        else if(categoryId!=null && division!=null){
//            notificationModal = (new NotificationModal(title,desc,imageUrl,categoryId,division,"",""));
//        }
//        else if(categoryId!=null){
//            notificationModal = (new NotificationModal(title,desc,imageUrl,categoryId,"","",""));
//        }
//        else if(url!=null)
//            notificationModal = (new NotificationModal(title,desc,imageUrl,"","","",url));
//        else
//            notificationModal = (new NotificationModal(title,desc,imageUrl,"","","",""));
//
//
//        storeNotification(notificationModal);
//
//    }

    private void storeNotification(NotificationModal notificationModal){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Getting Previous Data
        String json = sharedPreferences.getString("notification","");
        Log.d("Notification",json);

        Type type = new TypeToken<List<NotificationModal>>(){}.getType();

        List<NotificationModal> tempNotificationModal = gson.fromJson(json,type);
        if (tempNotificationModal == null){
            tempNotificationModal = new ArrayList<>();
            tempNotificationModal.add(notificationModal);
        }else{
            tempNotificationModal.add(notificationModal);
        }
        json = gson.toJson(tempNotificationModal);
        Log.d("Notification Json",json);
        editor.putString("notification",json);
        editor.apply();
    }
}
