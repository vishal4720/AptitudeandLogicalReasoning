package com.developingmind.aptitudeandlogicalreasoning.quiz;

import static com.developingmind.aptitudeandlogicalreasoning.HomeActivity.icons;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.solvedProblems.SolvedProblemActivity;

import java.util.Base64;
import java.util.List;

public class QuizzesCategoryAdapter extends RecyclerView.Adapter<QuizzesCategoryAdapter.ViewHolder> {
    private List<String> quizzesModalList;
    Context context;
    CardView cardView;
    Boolean isAptitude,isPractice;
    ImageView icon;

    public QuizzesCategoryAdapter(List<String> quizzesModalList, Context context,Boolean isAptitude,Boolean isPractice){
        this.quizzesModalList = quizzesModalList;
        this.context = context;
        this.isAptitude = isAptitude;
        this.isPractice = isPractice;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.quizzes_category,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(quizzesModalList.get(position),position);


        Animation animation = AnimationUtils.loadAnimation(context, (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return quizzesModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.quizzes_title);
            cardView = itemView.findViewById(R.id.card_quizzes_category);
            icon = itemView.findViewById(R.id.category_icon);

        }
        private void setData(final String title, final int position){
//            byte[] decodedBytes = Base64.getDecoder().decode(icons);
//            icon.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));

//            if (position>=0 && position<=2){
//                cardView.setCardBackgroundColor(itemView.getResources().getColor(R.color.pop));
//            }
            this.title.setText(title);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if(isAptitude){
                        if(isPractice){
                            intent = new Intent(itemView.getContext(), QuestionsActivity.class);
                            intent.putExtra("title",title);
                            intent.putExtra("position",position);
                        }else{
                            intent = new Intent(itemView.getContext(), SolvedProblemActivity.class);
                            intent.putExtra("title",title);
                            intent.putExtra("position",position);
                        }
                    }
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
