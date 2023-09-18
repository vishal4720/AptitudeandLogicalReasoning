package com.developingmind.aptitudeandlogicalreasoning.solvedProblems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuizzesCategoryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SolvedProblemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    SolvedProblemAdapter adapter;
    List<ScoreModal> scoreModal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solved_problem);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SolvedProblemAdapter(scoreModal,this);
        recyclerView.setAdapter(adapter);

        getData();
    }

    private void getData(){
        FirebaseFirestore.getInstance().collection(DatabaseEnum.aptitude_solvedProblems.toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                            scoreModal.clear();
                            for (DocumentSnapshot d:
                                 documentSnapshots) {
                                scoreModal.add(new ScoreModal(d.get("question").toString(),
                                        d.get("answer").toString(),
                                        d.get("explanation").toString()));
                            }
                            adapter = new SolvedProblemAdapter(scoreModal,SolvedProblemActivity.this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}