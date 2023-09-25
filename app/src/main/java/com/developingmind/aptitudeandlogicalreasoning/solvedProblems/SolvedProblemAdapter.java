package com.developingmind.aptitudeandlogicalreasoning.solvedProblems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;

import java.util.List;

public class SolvedProblemAdapter extends RecyclerView.Adapter<SolvedProblemAdapter.ViewHolder>{


    private List<ScoreModal> list;
    Context context;


    public SolvedProblemAdapter(List<ScoreModal> String, Context context){
        this.list = String;
        this.context = context;
    }
    @NonNull
    @Override
    public SolvedProblemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.solved_problem_item,parent,false);

        return new SolvedProblemAdapter.ViewHolder(view);
    }

    private int lastPosition = -1;
    @Override
    public void onBindViewHolder(@NonNull SolvedProblemAdapter.ViewHolder holder, int position) {
        ScoreModal scoreModal = list.get(position);
        holder.setData(scoreModal.getQuestion(),scoreModal.getAnswer(),scoreModal.getExplanation(),position, Color.valueOf(context.getResources().getColor(R.color.black)));

        Animation animation = AnimationUtils.loadAnimation(context, (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView question,answer,explanation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            explanation = itemView.findViewById(R.id.explanation);

        }
        private void setData(final String question,final String answer,final String explanation, final int position, Color color){
            this.question.setText(Html.fromHtml( ("<html><font color="+ color +"><b>Q"+(position+1)+ ". "+ question + "</b></font></html>"),Html.FROM_HTML_MODE_LEGACY));
            this.answer.setText(Html.fromHtml( ("<html><font color=#C36A0A><b> Answer : </b>"+ answer + "</font></html>"),Html.FROM_HTML_MODE_LEGACY));
            this.explanation.setText(Html.fromHtml( ("<html><font color=#E551FF><b>" + "Explanation : </b><br>"+ explanation + "</font></html>"),Html.FROM_HTML_MODE_LEGACY));
        }
    }

}
