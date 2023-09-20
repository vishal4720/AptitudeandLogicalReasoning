package com.developingmind.aptitudeandlogicalreasoning.quiz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.solvedProblems.SolvedProblemActivity;

import java.util.List;

public class QuizzesCategoryAdapter extends RecyclerView.Adapter<QuizzesCategoryAdapter.ViewHolder> {
    private List<String> quizzesModalList;
    Context context;
    CardView cardView;
    Boolean isAptitude,isPractice;

    public QuizzesCategoryAdapter(List<String> quizzesModalList, Context context,Boolean isAptitude,Boolean isPractice){
        this.quizzesModalList = quizzesModalList;
        this.context = context;
        this.isAptitude = isAptitude;
        this.isPractice = isPractice;
    }


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
    }

    @Override
    public int getItemCount() {
        return quizzesModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.quizzes_title);
            cardView = itemView.findViewById(R.id.card_quizzes_category);

        }
        private void setData(final String title, final int position){
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
