package com.developingmind.aptitudeandlogicalreasoning.notification;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionsActivity;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuizCategoryActivity;
import com.developingmind.aptitudeandlogicalreasoning.solvedProblems.SolvedProblemActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<NotificationModal> list;

    public NotificationAdapter(@NonNull Context context,List<NotificationModal> notificationModal){
        this.context=context;
        this.list = notificationModal;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(list.get(position),position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title,description;
        private ImageView notiImage;
        private LinearLayoutCompat layout;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.notification_title);
            description = itemView.findViewById(R.id.notification_desc);
            notiImage = itemView.findViewById(R.id.notification_image);
            layout = itemView.findViewById(R.id.notification_item);
        }

        private void setData(NotificationModal notificationModal,int pos){
            title.setText(notificationModal.getNotificationTitle());
            description.setText(notificationModal.getNotificationDescription());
            if(!notificationModal.getNotificationImage().isEmpty())
                Picasso.get().load(notificationModal.getNotificationImage()).into(notiImage);
            else
                notiImage.setVisibility(View.GONE);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!notificationModal.getCategoryId().isEmpty() && !notificationModal.getDivision().isEmpty() && !notificationModal.getTopicId().isEmpty()){
                        // Directly Open the Content
                        Intent intent = null;
                        switch (notificationModal.getDivision()){
                            case Constants.isPractice:
                                intent = new Intent(context, QuestionsActivity.class);
                                intent.putExtra(Constants.isAptitude, notificationModal.getCategoryId().trim().equals(Constants.isAptitude));
                                intent.putExtra("title",notificationModal.getTopicId().trim());
                                break;
                            case Constants.isStudy:
                                intent = new Intent(context, SolvedProblemActivity.class);
                                intent.putExtra(Constants.isSolvedProblems,false);
                                intent.putExtra(Constants.isAptitude, notificationModal.getCategoryId().trim().equals(Constants.isAptitude));
                                intent.putExtra("title",notificationModal.getTopicId().trim());
                                break;
                            case Constants.isSolvedProblems:
                                intent = new Intent(context, SolvedProblemActivity.class);
                                intent.putExtra(Constants.isAptitude, notificationModal.getCategoryId().trim().equals(Constants.isAptitude));
                                intent.putExtra("title",notificationModal.getTopicId().trim());
                                break;
                            case Constants.isTips:
                                break;
                        }
                        try {
                            context.startActivity(intent);
                        }catch (Exception ignored) {

                        }
                    }else if(!notificationModal.getCategoryId().isEmpty() && !notificationModal.getDivision().isEmpty()){
                        // Open category (Aptitude or Logical) and highlight
                        Intent intent = null;
                        switch (notificationModal.getDivision()){
                            case Constants.isPractice:
                            case Constants.isSolvedProblems:
                            case Constants.isStudy:
                                intent = new Intent(context, QuizCategoryActivity.class);
                                intent.putExtra(Constants.isAptitude, notificationModal.getCategoryId().trim().equals(Constants.isAptitude));
                                intent.putExtra(Constants.isPractice,notificationModal.getDivision().trim().equals(Constants.isPractice));
                                intent.putExtra(Constants.isStudy,notificationModal.getDivision().trim().equals(Constants.isStudy));
                                break;
                            case Constants.isTips:
                                break;
                        }
                        try {
                            context.startActivity(intent);
                        }catch (Exception ignored) {

                        }
                    }
                    else if(!notificationModal.getCategoryId().isEmpty()){
                        // Open Category Only Aptitude or Logical

                    } else if (!notificationModal.getUrl().isEmpty()) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(notificationModal.getUrl()));
                        context.startActivity(i);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
