package com.developingmind.aptitudeandlogicalreasoning.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.notification.NotificationAdapter;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionModal;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private Context context;
    private List<QuestionModal> list;

    public BookmarkAdapter(@NonNull Context context,List<QuestionModal> list){
        this.context = context;
        this.list = list;
    }

    private TextView question,answer,explanation;
    private TextView optionA,optionB,optionC,optionD;

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bookmark_item,parent,false);
        question = view.findViewById(R.id.question);
        optionA = view.findViewById(R.id.optionA);
        optionB = view.findViewById(R.id.optionB);
        optionC = view.findViewById(R.id.optionC);
        optionD = view.findViewById(R.id.optionD);
//        answer = view.findViewById(R.id.answer);
//        explanation = view.findViewById(R.id.explanation);
        return new BookmarkAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        question.setText("Q."+(position+1)+") "+list.get(position).getQuestion());
        optionA.setText("A - "+list.get(position).getOptionA());
        optionB.setText("B - "+list.get(position).getOptionB());
        optionC.setText("C - "+list.get(position).getOptionC());
        optionD.setText("D - "+list.get(position).getOptionD());
//        answer.setVisibility(View.GONE);
//        explanation.setVisibility(View.GONE);
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
