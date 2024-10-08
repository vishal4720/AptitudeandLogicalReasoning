package com.developingmind.aptitudeandlogicalreasoning.solvedProblems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.study.StudyAdapter;
import com.developingmind.aptitudeandlogicalreasoning.study.StudyModal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SolvedProblemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    SolvedProblemAdapter adapter;
    StudyAdapter studyAdapter;
    List<ScoreModal> scoreModal = new ArrayList<>();
    List<StudyModal> studyModal = new ArrayList<>();
    String categoryId;
    DialogMaker progressdialog;
    Toolbar toolbar;
    Boolean isSolved = true;
    Boolean isAptitude=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solved_problem);

        categoryId = getIntent().getStringExtra("title");
        isSolved = getIntent().getBooleanExtra(Constants.isSolvedProblems,true);
        isAptitude = getIntent().getBooleanExtra(Constants.isAptitude,true);
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(categoryId);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissLoader();
                finish();
            }
        });

        progressdialog = new DialogMaker(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SolvedProblemAdapter(scoreModal,this);
        recyclerView.setAdapter(adapter);

        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressdialog.dismiss();
    }

    private void showDialog(){
        progressdialog.getDialog().show();
    }

    private void dismissLoader(){
        if(progressdialog.getDialog().isShowing())
            progressdialog.getDialog().dismiss();
    }

    private void getData(){
        showDialog();
        String key;
        if (isAptitude)
            key = DatabaseEnum.aptitude.toString();
        else
            key = DatabaseEnum.logical.toString();
        DocumentReference collectionReference = FirebaseFirestore.getInstance().collection(key)
                .document(categoryId);

        if(isSolved) {
            collectionReference.collection("questions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                                if (!documentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot documentSnapshot: documentSnapshots) {
                                        Map<String, Object> object = documentSnapshot.getData();
                                        if (object!=null) {
                                            scoreModal.add(new ScoreModal(object.get("question").toString(),
                                                    object.get("correctAns").toString(),
                                                    object.get("explanation").toString()));
                                        }
                                        adapter = new SolvedProblemAdapter(scoreModal, SolvedProblemActivity.this);
                                        recyclerView.setAdapter(adapter);
                                    }
                                } else {
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
        }else{
            collectionReference
                    .collection("formulas")
                    .orderBy("no", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                if (documentSnapshots.size()==0){
                                    Toast.makeText(SolvedProblemActivity.this, "No Formulas. Come Back after some time !!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    for (DocumentSnapshot d :
                                            documentSnapshots) {
                                        studyModal.add(new StudyModal(d.getString("image").toString(),
                                                d.getString("title").toString(),
                                                d.getString("description").toString()));
                                        studyAdapter = new StudyAdapter(studyModal, SolvedProblemActivity.this);
                                        recyclerView.setAdapter(studyAdapter);

                                    }
                                    Log.d("", studyModal.get(1).getDescription());
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
}