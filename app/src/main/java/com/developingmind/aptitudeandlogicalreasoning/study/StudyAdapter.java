package com.developingmind.aptitudeandlogicalreasoning.study;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.ViewHolder>{


    private List<StudyModal> list;
    Context context;


    public StudyAdapter(List<StudyModal> String, Context context){
        this.list = String;
        this.context = context;
    }
    @NonNull
    @Override
    public StudyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.solved_problem_item,parent,false);

        return new StudyAdapter.ViewHolder(view);
    }

    private int lastPosition = -1;
    @Override
    public void onBindViewHolder(@NonNull StudyAdapter.ViewHolder holder, int position) {
        StudyModal studyModal = list.get(position);
        holder.setData(studyModal,position, Color.valueOf(context.getResources().getColor(R.color.black)));

        Animation animation = AnimationUtils.loadAnimation(context, (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,desc,explanation;
        ShapeableImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.question);
            desc = itemView.findViewById(R.id.answer);
            explanation = itemView.findViewById(R.id.explanation);
            explanation.setVisibility(View.GONE);
            imageView = itemView.findViewById(R.id.imageView);

        }
        private void setData(StudyModal studyModal, final int position, Color color){
            this.title.setText(Html.fromHtml( ("<html><font color="+ color +"><b>"+(position+1)+ ". "+ studyModal.getTitle() + "</b></font></html>"),Html.FROM_HTML_MODE_LEGACY));
            if(studyModal.getDescription()!="")
                this.desc.setText(Html.fromHtml( ("<html><font color=#C36A0A>"+ studyModal.getDescription() + "</font></html>"),Html.FROM_HTML_MODE_LEGACY));
            else
                this.desc.setVisibility(View.GONE);
            Log.d("",studyModal.getDescription());
            if (studyModal.getImg()!=null && studyModal.getImg()!=""){
                Log.d("","Inside Image");
                Picasso.get().load(studyModal.getImg()).into(this.imageView);
                imageView.setVisibility(View.VISIBLE);
            }

        }
    }

}
