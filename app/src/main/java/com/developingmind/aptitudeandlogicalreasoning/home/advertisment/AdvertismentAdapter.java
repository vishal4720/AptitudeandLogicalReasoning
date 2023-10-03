package com.developingmind.aptitudeandlogicalreasoning.home.advertisment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdvertismentAdapter extends RecyclerView.Adapter<AdvertismentAdapter.ViewHolder> {

    Context context;
    List<AdvertismentModal> list;

    public AdvertismentAdapter(@NonNull Context context,List<AdvertismentModal> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdvertismentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_advertisment_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertismentAdapter.ViewHolder holder, int position) {
        Picasso.get().load(list.get(position).getImageUrl())
                        .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(list.get(holder.getAdapterPosition()).getRedirctUrl()));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView imageView;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.advertisment_image);
        }
    }
}
