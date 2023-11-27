package com.developingmind.aptitudeandlogicalreasoning.bookmark;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.AdManager;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionModal;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private Context context;
    private List<QuestionModal> list;
    private AdManager adManager;
    private Dialog deleteDialog;
    private FragmentManager fragmentManager;

    public BookmarkAdapter(@NonNull Context context, List<QuestionModal> list, SharedPreferences sharedPreferences, AdManager adManager, FragmentManager supportFragmentManager){
        this.context = context;
        this.list = list;
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
        fragmentManager = supportFragmentManager;
        this.adManager = adManager;
        sharedialog = new Dialog(context);
        sharedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createDialog();
    }

    private TextView question;
    private TextView optionA,optionB,optionC,optionD;
    private ImageButton deleteBookmark,shareBookmark;
    Dialog sharedialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bookmark_item,parent,false);
        return new BookmarkAdapter.ViewHolder(view);
    }

    private void createDialog(){
        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.dialog_quit);
        deleteDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        deleteDialog.setCancelable(true);
        deleteDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.category_bg));
        ((TextView) deleteDialog.findViewById(R.id.quit_title)).setText("Delete");
        TextView desc = ((TextView) deleteDialog.findViewById(R.id.quit_desc));
        desc.setText("Are you Sure ?");
        desc.setVisibility(View.VISIBLE);
        ((Button) deleteDialog.findViewById(R.id.no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(holder);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        int position =0;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            optionA = itemView.findViewById(R.id.optionA);
            optionB = itemView.findViewById(R.id.optionB);
            optionC = itemView.findViewById(R.id.optionC);
            optionD = itemView.findViewById(R.id.optionD);
            deleteBookmark = itemView.findViewById(R.id.bookmark_delete);
            shareBookmark = itemView.findViewById(R.id.bookmark_share);
            Log.d("Adapter List Size", String.valueOf(list.size()));
        }

        private void setData(ViewHolder holder) {

            QuestionModal questionModal = list.get(holder.getAdapterPosition());
            question.setText(Html.fromHtml("Q." + (holder.getAdapterPosition() + 1) + ") " + questionModal.getQuestion(), Html.FROM_HTML_MODE_LEGACY));
            optionA.setText(Html.fromHtml("A - " + questionModal.getOptionA(), Html.FROM_HTML_MODE_LEGACY));
            optionB.setText(Html.fromHtml("B - " + questionModal.getOptionB(), Html.FROM_HTML_MODE_LEGACY));
            optionC.setText(Html.fromHtml("C - " + questionModal.getOptionC(), Html.FROM_HTML_MODE_LEGACY));
            optionD.setText(Html.fromHtml("D - " + questionModal.getOptionD(), Html.FROM_HTML_MODE_LEGACY));

            deleteBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(holder.getAdapterPosition());
                }
            });

            shareBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share(holder.getAdapterPosition());
                }
            });

            ((Button) deleteDialog.findViewById(R.id.yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(position);
                }
            });
        }

        private void showDeleteDialog(int position){
            this.position = position;
            deleteDialog.show();
        }

        private void delete(int position){
            Log.d("Adapter Position Before", String.valueOf(position));
            ((PracticeBookmarkFragment)fragmentManager.getFragments().get(0)).deleteBookmark(position);
            Log.d("Adapter Position After", String.valueOf(position));
            deleteDialog.dismiss();
        }

        private void share(int position) {
            ImageButton share_final, rewarded;
            final TextView credits;

            sharedialog.setContentView(R.layout.share_pop);
            share_final = sharedialog.findViewById(R.id.share_final_btn);
            rewarded = sharedialog.findViewById(R.id.rewarded_ad_btn);
            credits = sharedialog.findViewById(R.id.credits_txt);
            setCountText(credits);
            share_final.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferences.getInt("share_count", 0) > 0) {
                        editor.putInt("share_count", sharedPreferences.getInt("share_count", 0) - 1);
                        editor.apply();
                        String body = "Q. " + list.get(position).getQuestion() + "\n" +
                                list.get(position).getOptionA() + "\n" +
                                list.get(position).getOptionB() + "\n" +
                                list.get(position).getOptionC() + "\n" +
                                list.get(position).getOptionD() + "\n\n\n" +
                                "Test your own knowledge now !!" + "\n" +
                                context.getResources().getString(R.string.play_store_link);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, body);
                        context.startActivity(Intent.createChooser(intent, "Share via"));
                        setCountText(credits);
                    } else {
                        Toast.makeText(context, "Don't have enough points", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            rewarded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!adManager.showRewardedAd((Activity) context, sharedPreferences, credits)) {
                        adManager.loadRewardedAd();
                        Toast.makeText(context, "Video Not Available Yet !!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            sharedialog.show();

        }
        private void setCountText(TextView text){
            String share = context.getResources().getString(R.string.share_text);
            share = share.concat(String.valueOf(sharedPreferences.getInt("share_count",0)));
            text.setText(share);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
