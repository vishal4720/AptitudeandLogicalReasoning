package com.developingmind.aptitudeandlogicalreasoning.test.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;

import java.util.ArrayList;
import java.util.List;

public class TopicSelectionAdapter extends RecyclerView.Adapter<TopicSelectionAdapter.ViewHolder> {
    private List<TopicSelectionModal> list;
    private List<TopicSelectionModal> selectedList = new ArrayList<TopicSelectionModal>();
    private Integer totalCount =0;
    private Context context;
    private CardView cardView;
    private ImageView icon;

    public TopicSelectionAdapter(List<TopicSelectionModal> list, Context context){
        this.list = list;
        this.context = context;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.topic_selection_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(list.get(position),position);


        Animation animation = AnimationUtils.loadAnimation(context, (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<TopicSelectionModal> getSelectedList(){
        return selectedList;
    }

    public Integer getTotalCount(){
        return totalCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,questionCount;
        ImageView icon;
        CheckBox checkBox;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.topic_title);
            questionCount = itemView.findViewById(R.id.topic_question_count);
            cardView = itemView.findViewById(R.id.card_topic_selection);
            icon = itemView.findViewById(R.id.topic_icon);
            checkBox = itemView.findViewById(R.id.checkBox);

        }
        private void setData(final TopicSelectionModal modal, final int position){
            this.title.setText(modal.getTopic());
//            if(modal.getQuestionCount()>15){
//                this.questionCount.setText("Total Questions : 15");
//            }else{
                this.questionCount.setText("Total Questions : "+modal.getQuestionCount());
//            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                    if(!checkBox.isChecked() && selectedList.contains(list.get(position))){
                        selectedList.remove(modal);
                        totalCount-=modal.getQuestionCount();
                    }else{
                        totalCount+=modal.getQuestionCount();
                        selectedList.add(modal);
                    }
                }
            });
        }
    }
}
