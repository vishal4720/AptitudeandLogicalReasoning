package com.developingmind.aptitudeandlogicalreasoning.solvedProblems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionsActivity;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuizzesCategoryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SolvedProblemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    SolvedProblemAdapter adapter;
    List<ScoreModal> scoreModal = new ArrayList<>();
    String categoryId;
    DialogMaker progressdialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solved_problem);

        categoryId = getIntent().getStringExtra("title");
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(categoryId);

        progressdialog = new DialogMaker(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SolvedProblemAdapter(scoreModal,this);
        recyclerView.setAdapter(adapter);

        getData();
    }


    private void showDialog(){
        progressdialog.getDialog().show();
    }

    private void dismissLoader(){
        if(progressdialog.getDialog().isShowing())
            progressdialog.getDialog().hide();
    }

    private void getData(){
        showDialog();
        FirebaseFirestore.getInstance().collection(DatabaseEnum.aptitude.toString())
                .document(categoryId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String,Object> que = documentSnapshot.getData();
                            if (que!=null) {
                                Iterator<Map.Entry<String, Object>> iterator = que.entrySet().iterator();
                                scoreModal.clear();
                                while (iterator.hasNext()){
                                    Map.Entry<String, Object> x = iterator.next();
                                    JSONObject object = new JSONObject((Map) x.getValue());
                                    try {
                                        scoreModal.add(new ScoreModal(object.getString("question").toString(),
                                                object.getString("correctAns").toString(),
                                                object.getString("explanation").toString()));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                adapter = new SolvedProblemAdapter(scoreModal, SolvedProblemActivity.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(SolvedProblemActivity.this, "No Question right now. Come Back Later", Toast.LENGTH_SHORT).show();

                            }
                        }
                        dismissLoader();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissLoader();
                        Toast.makeText(SolvedProblemActivity.this, "Something went wrong. Try after some time !!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}