package com.developingmind.aptitudeandlogicalreasoning.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuizzesCategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<NotificationModal> list;

    public NotificationAdapter(@NonNull Context context,List<NotificationModal> notificationModal){
        this.context=context;
        this.list = notificationModal;
    }

    private int lastPosition = -1;
    private TextView title,description;
    private ImageView notiImage;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_item,parent,false);
        title = view.findViewById(R.id.notification_title);
        description = view.findViewById(R.id.notification_desc);
        notiImage = view.findViewById(R.id.notification_image);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        title.setText(list.get(position).getNotificationTitle());
        description.setText(list.get(position).getNotificationDescription());
        notiImage.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView){
            super(itemView);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
