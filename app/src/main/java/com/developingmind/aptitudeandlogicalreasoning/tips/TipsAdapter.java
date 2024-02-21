package com.developingmind.aptitudeandlogicalreasoning.tips;

import android.animation.Animator;
import android.content.Context;
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

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder>{


    private List<TipsModal> list;
    Context context;

    public TipsAdapter(List<TipsModal> String, Context context){
        this.list = String;
        this.context = context;
    }
    @NonNull
    @Override
    public TipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tips_item,parent,false);

        return new TipsAdapter.ViewHolder(view);
    }

    private int lastPosition = -1;
    @Override
    public void onBindViewHolder(@NonNull TipsAdapter.ViewHolder holder, int position) {
        TipsModal tipsModal = list.get(position);
        holder.setData(tipsModal,position);

        Animation animation = AnimationUtils.loadAnimation(context, (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,desc;
        ShapeableImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tips_title);
            desc = itemView.findViewById(R.id.tips_desc);
            imageView = itemView.findViewById(R.id.tips_image);

        }
        private void setData(TipsModal tipsModal, final int position){
            this.title.setText(Html.fromHtml( ("<html><b>"+(position+1)+ ". "+ tipsModal.getTitle() + "</b></html>"),Html.FROM_HTML_MODE_LEGACY));
            if(tipsModal.getDescription()!=null && !tipsModal.getDescription().isEmpty()) {
                this.desc.setVisibility(View.VISIBLE);
                this.desc.setText(Html.fromHtml(("<html>" + tipsModal.getDescription() + "</html>"), Html.FROM_HTML_MODE_LEGACY));
                Log.d("Desc",tipsModal.getDescription());
            }
            if (tipsModal.getImg()!=null && !tipsModal.getImg().isEmpty()){
                Picasso.get().load(tipsModal.getImg()).into(this.imageView);
                imageView.setVisibility(View.VISIBLE);
            }



        }
    }

}
