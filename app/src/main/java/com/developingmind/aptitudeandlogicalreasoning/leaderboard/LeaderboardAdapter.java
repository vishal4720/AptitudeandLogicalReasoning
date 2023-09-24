package com.developingmind.aptitudeandlogicalreasoning.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.notification.NotificationAdapter;

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
        holder.setData(list.get(holder.getAdapterPosition()).getName(),list.get(holder.getAdapterPosition()).getScore(),holder.getAdapterPosition());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,score;
        ImageView medal;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.leaderboard_name);
            score = itemView.findViewById(R.id.leaderboard_Score);
            medal = itemView.findViewById(R.id.leaderboard_medal);
        }

        private void setData(String n,String s,int pos){
            name.setText(n);
            score.setText(s);
            if((pos+1) == 1){
                medal.setImageDrawable(context.getDrawable(R.drawable.ic_gold_medal));
            }else if((pos+1) == 2){
                medal.setImageDrawable(context.getDrawable(R.drawable.ic_silver_medal));
            }else if ((pos+1) == 3){
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
