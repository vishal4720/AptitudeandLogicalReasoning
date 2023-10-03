package com.developingmind.aptitudeandlogicalreasoning.quiz;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.test.competitive.CompetitiveQuestionsActivity;
import com.developingmind.aptitudeandlogicalreasoning.test.test.TopicQuestionsActivity;

import java.util.List;

public class QuestionsGridAdapter extends ArrayAdapter<QuestionModal> {

    private List<QuestionModal> list;
    private Context context;
    private int limit;


    public QuestionsGridAdapter(@NonNull Context context,List<QuestionModal> list,int limit){
        super(context,0,list);
        this.context = context;
        this.list = list;
        this.limit = limit;
    }

    @Override
    public int getCount() {
        return limit;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.questions_grid_item, parent, false);
        }
        QuestionModal questionModal = list.get(position);
        Button questionItem = listitemView.findViewById(R.id.question_grid_item_id);
        questionItem.setText(String.valueOf(position+1));
        if(questionModal.getAnswered()){
            questionItem.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.dark_green)));
        } else if (questionModal.getVisited()) {
            questionItem.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
        }

        questionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof QuestionsActivity)
                    ((QuestionsActivity) context).jumpTo(position);
                else if(context instanceof CompetitiveQuestionsActivity)
                    ((CompetitiveQuestionsActivity) context).jumpTo(position);
                else if (context instanceof TopicQuestionsActivity) {
                    ((TopicQuestionsActivity) context).jumpTo(position);
                }
            }
        });

        return listitemView;
    }
}
