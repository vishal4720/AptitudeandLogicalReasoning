package com.developingmind.aptitudeandlogicalreasoning.solvedProblems;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    @Override
    public void onBindViewHolder(@NonNull SolvedProblemAdapter.ViewHolder holder, int position) {
        ScoreModal scoreModal = list.get(position);
        holder.setData(scoreModal.getQuestion(),scoreModal.getAnswer(),scoreModal.getExplanation(),position);
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
        private void setData(final String question,final String answer,final String explanation, final int position){
            this.question.setText(Html.fromHtml( ("<html><font color=#000000><b>Q"+(position+1)+ ". "+ question + "</b></font></html>"),Html.FROM_HTML_MODE_LEGACY));
            this.answer.setText(Html.fromHtml( ("<html><font color=#C36A0A><b> Answer : </b>"+ answer + "</font></html>"),Html.FROM_HTML_MODE_LEGACY));
            this.explanation.setText(Html.fromHtml( ("<html><font color=#E551FF><b>" + "Explanation : </b><br>"+ explanation + "</font></html>"),Html.FROM_HTML_MODE_LEGACY));
        }
    }

}
