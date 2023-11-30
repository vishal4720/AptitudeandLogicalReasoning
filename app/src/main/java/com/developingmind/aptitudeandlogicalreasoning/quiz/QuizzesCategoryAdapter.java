package com.developingmind.aptitudeandlogicalreasoning.quiz;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import com.developingmind.aptitudeandlogicalreasoning.AdManager;
import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.solvedProblems.SolvedProblemActivity;
import com.developingmind.aptitudeandlogicalreasoning.tips.TipsActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.util.List;

public class QuizzesCategoryAdapter extends RecyclerView.Adapter<QuizzesCategoryAdapter.ViewHolder> {
    private List<String> quizzesModalList;
    Context context;
    CardView cardView;
    Boolean isAptitude,isPractice,isStudy,isSolved;
    ImageView icon;

    public QuizzesCategoryAdapter(List<String> quizzesModalList, Context context,Boolean isAptitude,Boolean isPractice,Boolean isStudy,Boolean isSolved){
        this.quizzesModalList = quizzesModalList;
        this.context = context;
        this.isAptitude = isAptitude;
        this.isPractice = isPractice;
        this.isStudy = isStudy;
        this.isSolved = isSolved;
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
        private void setData(String title, int position){
//            byte[] decodedBytes = Base64.getDecoder().decode(icons);
//            icon.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));

//            if (position>=0 && position<=2){
//                cardView.setCardBackgroundColor(itemView.getResources().getColor(R.color.pop));
//            }
            this.title.setText(title);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("On Click",String.valueOf(isAptitude));
                    Log.d("On Click",String.valueOf(isPractice));
                    Log.d("On Click",String.valueOf(isStudy));
                    AdManager adManager = (AdManager) context.getApplicationContext();
                    InterstitialAd interstitialAd = adManager.getmInterstitialAd();
                    if (interstitialAd!=null && !adManager.isPurchased) {
                        ((QuizCategoryActivity)context).showDialog();
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                adManager.loadInterstitialAd();
                                super.onAdShowedFullScreenContent();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                jumpToNextActivity(title, position);
                                super.onAdDismissedFullScreenContent();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                adManager.loadInterstitialAd();
                                jumpToNextActivity(title, position);
                                super.onAdFailedToShowFullScreenContent(adError);
                            }
                        });
                        interstitialAd.show((Activity) context);
                    }else {
                        jumpToNextActivity(title,position);
                    }
                }
            });
        }

        private void jumpToNextActivity(String title,int position){
            Intent intent = null;
            if (isPractice) {
                intent = new Intent(itemView.getContext(), QuestionsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("position", position);
                intent.putExtra(Constants.isAptitude, isAptitude);
            } else if (isStudy) {
                intent = new Intent(itemView.getContext(), SolvedProblemActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("position", position);
                intent.putExtra(Constants.isSolvedProblems, false);
                intent.putExtra(Constants.isAptitude, isAptitude);
            } else if (isSolved){
                intent = new Intent(itemView.getContext(), SolvedProblemActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("position", position);
                intent.putExtra(Constants.isAptitude, isAptitude);
            }else{
                intent = new Intent(itemView.getContext(), TipsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("position", position);
                intent.putExtra(Constants.isAptitude, isAptitude);
            }
            ((QuizCategoryActivity)context).dismissDialog();
            context.startActivity(intent);
        }
    }
}
