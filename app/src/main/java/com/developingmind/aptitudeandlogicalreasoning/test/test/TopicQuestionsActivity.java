package com.developingmind.aptitudeandlogicalreasoning.test.test;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.developingmind.aptitudeandlogicalreasoning.AdManager;
import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.ScoreEnum;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionModal;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionsGridAdapter;
import com.developingmind.aptitudeandlogicalreasoning.score.ScoreActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TopicQuestionsActivity extends AppCompatActivity {

    TextView question,question_no;
    FloatingActionButton bookmark,share,grid;
    LinearLayout options;
    Button previous,next;
    int count =0;
    List<QuestionModal> list = new ArrayList<>();
    int position = 0;
    private int score = 0;
    private int totalAttempted = 0;
    Dialog sharedialog;
    DialogMaker progressdialog;
    int matchedQuestionPosition;

    List<QuestionModal> bookmarklist = new ArrayList<QuestionModal>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private int limitQuestions = 10;

    Toolbar toolbar;

    Gson gson;

    DialogMaker exitDialog;

    GridView questionsGridView;
    Dialog questionsDialog;
    QuestionsGridAdapter gridAdapter;
    ArrayList<String> topicData;
    Boolean isAptitude;

    private CountDownTimer countDownTimer;

    private AdManager adManager;


    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private int time;
    private TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_competitive);

        isAptitude = getIntent().getBooleanExtra(Constants.isAptitude,true);
        limitQuestions = getIntent().getIntExtra("count",15);
        Bundle extrasBundle = getIntent().getExtras();
        topicData = extrasBundle.getStringArrayList("data");
        Log.d("Topic Data", topicData.toString());
        time = getIntent().getIntExtra("time",20);

        question = findViewById(R.id.question);
        timerText = findViewById(R.id.timer_text);
        question_no = findViewById(R.id.question_no);
        bookmark = findViewById(R.id.bookmark_btn);
        options = findViewById(R.id.options_container);
        share = findViewById(R.id.share_btn);
        next = findViewById(R.id.next_btn);
        previous = findViewById(R.id.previous_btn);
        toolbar = findViewById(R.id.toolbar);
        grid = findViewById(R.id.grid);
        setSupportActionBar(toolbar);
        question.setMovementMethod(new ScrollingMovementMethod());

        exitDialog = new DialogMaker(this,"Are you sure you want to Quit ?");

        sharedialog = new Dialog(this);
        sharedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressdialog = new DialogMaker(this);

        sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        getBookmark();

        adManager = (AdManager) getApplicationContext();


        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGrid();
            }
        });


        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()){
                    bookmarklist.remove(matchedQuestionPosition);
                    bookmark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }else{
                    QuestionModal q = list.get(position);
                    q.setGivenAns(null);
                    bookmarklist.add(q);
                    bookmark.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });


        list = new ArrayList<>();
        showDialog();

        getData();

    }

    private void setCountDownTimer(){
        countDownTimer = new CountDownTimer((1000*60)*time,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                Log.d("Timer",f.format(min) + ":" + f.format(sec));
                timerText.setText(f.format(min) + ":" + f.format(sec));
            }

            @Override
            public void onFinish() {
                nextActivity();
            }
        }.start();
    }

    private void nextActivity(){
        Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
        intent.putExtra(ScoreEnum.correctQuestions.toString(), score);
        intent.putExtra(ScoreEnum.totalQuestions.toString(), limitQuestions);
        intent.putExtra(ScoreEnum.totalAttempted.toString(), totalAttempted);
        intent.putExtra(ScoreEnum.totalWrong.toString(), totalAttempted - score);
        intent.putExtra(ScoreEnum.totalSkipped.toString(), limitQuestions - totalAttempted);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitDialog.dismiss();
        questionsDialog.dismiss();
        countDownTimer.cancel();
    }

    private void createQuestionsGrid(){
        questionsDialog = new Dialog(this);
        questionsDialog.setContentView(R.layout.questions_grid);
        questionsGridView = questionsDialog.findViewById(R.id.questions_grid_view);
        questionsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        questionsDialog.setCancelable(true);

        gridAdapter = new QuestionsGridAdapter(this,list,limitQuestions);
        questionsGridView.setAdapter(gridAdapter);

    }

    private void showGrid(){
        questionsDialog.show();
    }

    @Override
    public void onBackPressed() {
        exitDialog.getDialog().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
        progressdialog.getDialog().dismiss();
        exitDialog.getDialog().dismiss();
    }

    private void getData(){
        list.clear();
        String key;
        if (isAptitude){
            key = DatabaseEnum.aptitude.toString();
        }else {
            key = DatabaseEnum.logical.toString();
        }

        for (String topic:
             topicData) {
            firebaseFirestore.collection(key)
                    .document(topic)
                    .collection("questions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(TopicQuestionsActivity.this, "Please try after some time !!", Toast.LENGTH_SHORT).show();
                                dismissLoader();
                                finish();
                            }
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            if (!documentSnapshots.isEmpty()) {
                                for (DocumentSnapshot documentSnapshot: documentSnapshots) {
                                    Map<String,Object> object = documentSnapshot.getData();
                                    try {
                                        list.add(new QuestionModal(object.get("question").toString(),
                                                object.get("optionA").toString(),
                                                object.get("optionB").toString(),
                                                object.get("optionC").toString(),
                                                object.get("optionD").toString(),
                                                object.get("correctAns").toString(),
                                                object.get("explanation").toString(),
                                                documentSnapshot.getId(),
                                                documentSnapshot.getId()));
                                    } catch (Exception e) {
                                        Log.d("", e.getMessage());
                                    }
                                }
                                Collections.shuffle(list);
                            }
                            if(topic.equals(topicData.get(topicData.size()-1))){
                                // checking is questions are enough else taking from competitive
                                if(limitQuestions>list.size()){

                                }


                                playanim(question, 0, list.get(position).getQuestion());

                                for (int i = 0; i < 4; i++) {
                                    options.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            checkAnswer(((Button) v));
                                        }
                                    });
                                }

                                createQuestionsGrid();

                                next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                    next.setAlpha((float) 0.7);
                                        enabledoption(true);
                                        position++;
                                        previous.setEnabled(true);
                                        if (position == (limitQuestions - 1)) {
                                            next.setText("Submit");
                                        }
                                        if (position == limitQuestions) {
                                            nextActivity();
                                            return;
                                        }
                                        if (!list.get(position).getAnswered()) {
                                            next.setText("Skip");
                                        }
                                        count = 0;
                                        playanim(question, 0, list.get(position).getQuestion());
                                    }
                                });

                                previous.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                    enabledoption(true);
                                        enabledoption(true);
                                        if (position != 0) {
                                            position--;
                                            if (position == 0) {
                                                previous.setEnabled(false);
                                            }
                                            count = 0;
                                            playanim(question, 0, list.get(position).getQuestion());
                                        }
                                        if (position != (limitQuestions - 1) && list.get(position).getAnswered()) {
                                            next.setText("Next");
                                        } else if (position != (limitQuestions - 1) && !list.get(position).getAnswered()) {
                                            next.setText("Skip");
                                        }

                                        Log.d("Position", String.valueOf(position));
                                    }
                                });
                                dismissLoader();

                                setCountDownTimer();

                                share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ImageButton share_final,rewarded;
                                        final TextView credits;

                                        sharedialog.setContentView(R.layout.share_pop);
                                        share_final = sharedialog.findViewById(R.id.share_final_btn);
                                        rewarded = sharedialog.findViewById(R.id.rewarded_ad_btn);
                                        credits = sharedialog.findViewById(R.id.credits_txt);
                                        setCountText(credits);
                                        share_final.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (sharedPreferences.getInt("share_count",0)>0){
                                                    editor.putInt("share_count",sharedPreferences.getInt("share_count",0)-1);
                                                    editor.apply();
                                                    String body = "Q. " + convertTextfromHTML(list.get(position).getQuestion()) + "\n" +
                                                            convertTextfromHTML(list.get(position).getOptionA()) +"\n" +
                                                            convertTextfromHTML(list.get(position).getOptionB()) +"\n" +
                                                            convertTextfromHTML(list.get(position).getOptionC()) +"\n" +
                                                            convertTextfromHTML(list.get(position).getOptionD()) + "\n\n\n" +
                                                            "Test your own knowledge now !!" + "\n" +
                                                            getResources().getString(R.string.play_store_link);

                                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                                    intent.setType("text/plain");
                                                    intent.putExtra(Intent.EXTRA_TEXT,body);
                                                    startActivity(Intent.createChooser(intent,"Share via"));
                                                    setCountText(credits);
                                                }else{
                                                    Toast.makeText(getApplication(),"Don't have enough points",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        rewarded.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(!adManager.showRewardedAd(TopicQuestionsActivity.this,sharedPreferences,credits)){
                                                    adManager.loadRewardedAd();
                                                    Toast.makeText(TopicQuestionsActivity.this, "Video Not Available Yet !!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        sharedialog.show();

                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(topic.equals(topicData.get(topicData.size()-1)))
                                dismissLoader();
                        }
                    });
        }

    }

    private Spanned convertTextfromHTML(String str){
        return Html.fromHtml(str,Html.FROM_HTML_MODE_LEGACY);
    }

    public void setCountText(TextView text){
        String share = getResources().getString(R.string.share_text);
        share = share.concat(String.valueOf(sharedPreferences.getInt("share_count",0)));
        text.setText(share);
    }

    public void jumpTo(int pos){
        enabledoption(true);
        position=pos;
        previous.setEnabled(true);
        if(!list.get(position).getAnswered()){
            next.setText("Skip");
        }
        if(position == (limitQuestions-1)){
            next.setText("Submit");
        }
        count = 0;
        playanim(question,0,list.get(position).getQuestion());
        questionsDialog.dismiss();
    }

    private void getBookmark(){
        String json = sharedPreferences.getString("bookmarkCompetitive"+isAptitude,"");

        Type type = new TypeToken<List<QuestionModal>>(){}.getType();

        bookmarklist = gson.fromJson(json,type);
        if (bookmarklist == null){
            bookmarklist = new ArrayList<>();
        }
    }

    private void storeBookmarks(){
        int i=0;
        for (QuestionModal q:
                bookmarklist) {
            if (q.getGivenAns()!=null){
                bookmarklist.get(i).setGivenAns(null);
            }
            i++;
        }
        String json = gson.toJson(bookmarklist);
        editor.putString("bookmarkCompetitive"+isAptitude,json);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_info && list.get(position).getAnswered()){
            DialogMaker dialogMaker = new DialogMaker(TopicQuestionsActivity.this,"Explanation",list.get(position).getExplanation());
            dialogMaker.getDialog().show();
        }else{
            Toast.makeText(this, "Question not answered !!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.questions_menu,menu);
        return true;
    }

    private void showDialog(){
        if(!progressdialog.getDialog().isShowing())
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

    private void isQuestionAnswered(){
        Log.d("Position Answered",String.valueOf(position));
        if(list.get(position).getAnswered() && list.get(position).getGivenAns()!=null){
            enabledoption(false);
            Button selectedoption = list.get(position).getGivenAns();
            if (selectedoption.getText().toString().equals(list.get(position).getCorrectAns())) {
                selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                score++;
            } else {
                Log.d("Correct",list.get(position).getCorrectAns());
                Log.d("Correct",((Button) options.getChildAt(1)).getText().toString());
                selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                Button correctoption = (Button) options.findViewWithTag(list.get(position).getCorrectAns());
                correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }
        }
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
                                ((TextView)view).setText(convertTextfromHTML(data));
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
                        }else{
                            if(count == 4 && list.get(position).getAnswered()){
                                isQuestionAnswered();
                            }
                            if(!list.get(position).getVisited()){
                                list.get(position).setVisited(true);
                                Log.d("Animation Question Visited",String.valueOf(list.get(position).getVisited()));
                                gridAdapter.notifyDataSetChanged();
                            }
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
        Log.d("Button",selectedoption.getText().toString());
        enabledoption(false);
        if(position!=(limitQuestions-1))
            next.setText("Next");
        list.get(position).setGivenAns(selectedoption);
        list.get(position).setAnswered(true);
        if (selectedoption.getText().toString().equals(list.get(position).getCorrectAns())) {
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            score++;
        } else {
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctoption = (Button) options.findViewWithTag(list.get(position).getCorrectAns());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
        totalAttempted++;
        gridAdapter.notifyDataSetChanged();
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