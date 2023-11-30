package com.developingmind.aptitudeandlogicalreasoning.test.test;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.AdManager;
import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopicSelectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static List<TopicSelectionModal> list;

    Button submit;

    Dialog questionCountDialog;

    DialogMaker progressdialog;
    List<String> items = new ArrayList<>();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private Toolbar toolbar;
    private TopicSelectionAdapter topicSelectionAdapter;

    Boolean isAptitude = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_topic);

        recyclerView = findViewById(R.id.recycler_view);
        progressdialog = new DialogMaker(this);
        toolbar = findViewById(R.id.toolbar);
        submit = findViewById(R.id.submit_btn);

        setSupportActionBar(toolbar);

        isAptitude = getIntent().getBooleanExtra(Constants.isAptitude,true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                finish();
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();

        topicSelectionAdapter = new TopicSelectionAdapter(list,this);
        recyclerView.setAdapter(topicSelectionAdapter);

        showDialog();
        createDialog();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("List", topicSelectionAdapter.getSelectedList().toString());

                if(topicSelectionAdapter.getSelectedList().size()>0) {
                    showquestionDialog();
                }
                else
                    Toast.makeText(TopicSelectionActivity.this, "Select Topic", Toast.LENGTH_SHORT).show();
            }
        });

        String key;
        if (isAptitude){
            key = DatabaseEnum.aptitude.toString();
        }else{
            key = DatabaseEnum.logical.toString();
        }

        firebaseFirestore.collection(key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                           QuerySnapshot queryDocumentSnapshots = task.getResult();
                           List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot doc:
                                 documentSnapshots) {
                                Map<String,Object> map = doc.getData();
                                list.add(new TopicSelectionModal(map.size(),doc.getId()));
                            }
                            topicSelectionAdapter.notifyDataSetChanged();

                           dismissDialog();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        questionCountDialog.dismiss();
        progressdialog.dismiss();

    }
    private Integer questionCount =0;

    private void createDialog(){
        questionCountDialog = new Dialog(this);
        questionCountDialog.setContentView(R.layout.select_question_dialog);
        questionCountDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.ic_loader));
        questionCountDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        questionCountDialog.setCancelable(true);
        Spinner spinner = questionCountDialog.findViewById(R.id.spinner);
        items.add("15");
        items.add("30");
        items.add("45");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);


        TextView count = questionCountDialog.findViewById(R.id.time_allotted);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionCount = Integer.parseInt(items.get(position));
                count.setText(String.valueOf(questionCount+5)+" mins");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        questionCountDialog.findViewById(R.id.start_quiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopicSelectionActivity.this,TopicQuestionsActivity.class);

                intent.putExtra(Constants.isAptitude,isAptitude);
                Bundle b = new Bundle();
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < topicSelectionAdapter.getSelectedList().size(); i++) {
                    arrayList.add(topicSelectionAdapter.getSelectedList().get(i).getTopic());
                }
                b.putStringArrayList("data",arrayList);
                intent.putExtras(b);
                intent.putExtra("count",questionCount);
                intent.putExtra("time",questionCount+5);

                AdManager adManager = (AdManager) getApplicationContext();
                InterstitialAd interstitialAd = adManager.getmInterstitialAd();
                if(interstitialAd!=null && !adManager.isPurchased){
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            adManager.loadInterstitialAd();
                            super.onAdShowedFullScreenContent();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            adManager.loadInterstitialAd();
                            startActivity(intent);
                            finish();
                            super.onAdDismissedFullScreenContent();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            adManager.loadInterstitialAd();
                            startActivity(intent);
                            finish();
                            super.onAdFailedToShowFullScreenContent(adError);
                        }
                    });
                    interstitialAd.show(TopicSelectionActivity.this);

                }else {
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public List<TopicSelectionModal> getTopicSelectedList(){
        return topicSelectionAdapter.getSelectedList();
    }

    private void showquestionDialog(){
        questionCountDialog.show();
    }

    private void hideQuestionDialog(){
        questionCountDialog.hide();
    }

    private void showDialog(){
        progressdialog.getDialog().show();
    }

    private void dismissDialog(){
        progressdialog.getDialog().dismiss();
    }
}