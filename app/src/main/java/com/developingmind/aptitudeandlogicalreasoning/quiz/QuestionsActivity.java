package com.developingmind.aptitudeandlogicalreasoning.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.ScoreEnum;
import com.developingmind.aptitudeandlogicalreasoning.score.ScoreActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    TextView question,question_no;
    FloatingActionButton bookmark;
    LinearLayout options;
    Button share,next;
    int count =0;
    List<QuestionModal> list = new ArrayList<>();
    int position = 0;
    private int score = 0;
    private int totalAttempted = 0;
    Dialog sharedialog;
    DialogMaker progressdialog;
    int matchedQuestionPosition;
    private String categoryId;

    List<QuestionModal> bookmarklist = new ArrayList<QuestionModal>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private int limitQuestions = 10;

    Toolbar toolbar;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        question = findViewById(R.id.question);
        question_no = findViewById(R.id.question_no);
        bookmark = findViewById(R.id.bookmark_btn);
        options = findViewById(R.id.options_container);
        share = findViewById(R.id.share_btn);
        next = findViewById(R.id.next_btn);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedialog = new Dialog(this);
        sharedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        categoryId = getIntent().getStringExtra("title");
        progressdialog = new DialogMaker(this);
        Log.d("ID",categoryId);

        sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        getBookmark();

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()){
                    bookmarklist.remove(matchedQuestionPosition);
                    bookmark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }else{
                    bookmarklist.add(list.get(position));
                    bookmark.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });


        list = new ArrayList<>();
        showDialog();
        firebaseFirestore.collection(getResources().getString(R.string.collection_name))
                .document(categoryId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(QuestionsActivity.this, "Please try after some time !!", Toast.LENGTH_SHORT).show();
                            dismissLoader();
                            finish();
                        }
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Map<String,Object> que = documentSnapshot.getData();
                        Log.d("",documentSnapshot.toString());
                        if (que!=null) {
                            Iterator<Map.Entry<String, Object>> iterator = que.entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<String, Object> x = iterator.next();
                                JSONObject object = new JSONObject((Map) x.getValue());
                                try {
                                    list.add(new QuestionModal(object.getString("question"),
                                            object.getString("optionA"),
                                            object.getString("optionB"),
                                            object.getString("optionC"),
                                            object.getString("optionD"),
                                            object.getString("correctAns"),
                                            "ID"));
                                } catch (JSONException e) {
                                    Log.d("", e.getMessage());
                                }
                            }
                            Collections.shuffle(list);


                            playanim(question,0,list.get(position).getQuestion());
                            for (int i=0;i<4;i++){
                                options.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkAnswer(((Button)v));
                                    }
                                });
                            }

                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    next.setEnabled(false);
                                    next.setAlpha((float) 0.7);
                                    enabledoption(true); 
                                    position++;
                                    if (position == limitQuestions){
//                                        if (interstitial!=null){
//                                            interstitial.show(getParent());
//                                        }
                                        Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
                                        intent.putExtra(ScoreEnum.correctQuestions.toString(),score);
                                        intent.putExtra(ScoreEnum.totalQuestions.toString(),limitQuestions);
                                        intent.putExtra(ScoreEnum.totalAttempted.toString(),totalAttempted);
                                        intent.putExtra(ScoreEnum.totalWrong.toString(),totalAttempted-score);
                                        intent.putExtra(ScoreEnum.totalSkipped.toString(),limitQuestions-totalAttempted);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    count = 0;
                                    playanim(question,0,list.get(position).getQuestion());
                                }
                            });
                            dismissLoader();

                        }else {
                            dismissLoader();
                            Toast.makeText(QuestionsActivity.this, "No Question right now. Come Back Later", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        dismissLoader();
                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.questions_menu,menu);
        return true;
    }

    private void showDialog(){
        progressdialog.getDialog().show();
    }

    private void dismissLoader(){
        if(progressdialog.getDialog().isShowing())
            progressdialog.getDialog().hide();
    }

    private boolean modelMatch(){
        boolean matched = false;
        int i =0;
        for (QuestionModal modal : bookmarklist){
            if (modal.getQuestion().equals(list.get(position).getQuestion())
                    && modal.getCorrectAns().equals(list.get(position).getCorrectAns())){
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;
        }
        return matched;
    }

    private void playanim(final View view, final int value, final String data){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (value == 0 && count < 4){
                            String option ="";
                            switch (count){
                                case 0:
                                    option = list.get(position).getOptionA();
                                    break;
                                case 1:
                                    option = list.get(position).getOptionB();
                                    break;
                                case 2:
                                    option = list.get(position).getOptionC();
                                    break;
                                case 3:
                                    option = list.get(position).getOptionD();
                                    break;
                            }
                            playanim(options.getChildAt(count),0,option);
                            count++;
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        if (value == 0){
                            try {
                                ((TextView)view).setText(data);
                                question_no.setText(String.valueOf(position+1)+"/"+String.valueOf(limitQuestions));

                                if (modelMatch()){
                                    bookmark.setImageDrawable(getDrawable(R.drawable.bookmark));
                                }else{
                                    bookmark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                                }

                            }catch (ClassCastException ex){
                                ((Button)view).setText(data);
                            }
                            view.setTag(data);
                            playanim(view,1,data);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void checkAnswer(Button selectedoption){
        enabledoption(false);
//        next.setEnabled(true);
        next.setText("Next");
        next.setAlpha(1);
        if (selectedoption.getText().toString().equals(list.get(position).getCorrectAns())){
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            score++;
        }else{
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctoption = (Button) options.findViewWithTag(list.get(position).getCorrectAns());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
        totalAttempted++;
    }

    private void enabledoption(boolean enable){
        for(int i = 0; i<4; i++) {
            options.getChildAt(i).setEnabled(enable);
            if (enable) {
                options.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
            }
        }
    }
}