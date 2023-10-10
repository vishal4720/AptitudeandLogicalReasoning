package com.developingmind.aptitudeandlogicalreasoning.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.notification.NotificationAdapter;
import com.developingmind.aptitudeandlogicalreasoning.profile.Gender;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private Context context;
    private List<LeaderboardModal> list;

    public LeaderboardAdapter(@NonNull Context context, List<LeaderboardModal> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_item,parent,false);
        return new LeaderboardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position),position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,score;
        ImageView medal;
        ShapeableImageView profileIcon;

        LinearLayoutCompat layoutCompat;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.leaderboard_name);
            score = itemView.findViewById(R.id.leaderboard_Score);
            medal = itemView.findViewById(R.id.leaderboard_medal);
            profileIcon = itemView.findViewById(R.id.profile_icon);
            layoutCompat = itemView.findViewById(R.id.leaderboard_item_layout);
        }

        private void setData(LeaderboardModal leaderboardModal,int pos){
            name.setText(leaderboardModal.getName());
            score.setText(leaderboardModal.getScore());

            if(leaderboardModal.getProfile() != null){
                Picasso.get().load(leaderboardModal.getProfile()).into(profileIcon);
            }else{
                if(leaderboardModal.getGender().equals(Gender.Male.toString())){
                    profileIcon.setImageDrawable(context.getDrawable(R.drawable.male_avatar));
                }else{
                    profileIcon.setImageDrawable(context.getDrawable(R.drawable.female_avatar));
                }
            }

            if((pos+1) == 1){
                layoutCompat.getLayoutParams().height = layoutCompat.getLayoutParams().height + 40;
                profileIcon.getLayoutParams().width = profileIcon.getLayoutParams().width + 40;
                medal.setImageDrawable(context.getDrawable(R.drawable.ic_gold_medal));
            }else if((pos+1) == 2){
                layoutCompat.getLayoutParams().height = layoutCompat.getLayoutParams().height + 30;
                profileIcon.getLayoutParams().width = profileIcon.getLayoutParams().width + 30;
                medal.setImageDrawable(context.getDrawable(R.drawable.ic_silver_medal));
            }else if ((pos+1) == 3){
                layoutCompat.getLayoutParams().height = layoutCompat.getLayoutParams().height + 20;
                profileIcon.getLayoutParams().width = profileIcon.getLayoutParams().width + 20;
                medal.setImageDrawable(context.getDrawable(R.drawable.ic_bronze_medal));
            }else{
                medal.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
