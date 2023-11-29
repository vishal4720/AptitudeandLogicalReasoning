package com.developingmind.aptitudeandlogicalreasoning.tips;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.solvedProblems.ScoreModal;
import com.developingmind.aptitudeandlogicalreasoning.solvedProblems.SolvedProblemAdapter;
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

public class TipsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    TipsAdapter adapter;
    TipsAdapter tipsAdapter;
    List<TipsModal> tipsModals = new ArrayList<>();
    String categoryId;
    DialogMaker progressDialog;
    Toolbar toolbar;
    Boolean isAptitude=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solved_problem);

        categoryId = getIntent().getStringExtra("title");
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

        progressDialog = new DialogMaker(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TipsAdapter(tipsModals,this);
        recyclerView.setAdapter(adapter);

        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    private void showDialog(){
        progressDialog.getDialog().show();
    }

    private void dismissLoader(){
        if(progressDialog.getDialog().isShowing())
            progressDialog.getDialog().dismiss();
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


        collectionReference
                .collection("tips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            if (documentSnapshots.size()==0){
                                Toast.makeText(TipsActivity.this, "No Tips. Come Back after some time !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                for (DocumentSnapshot d :
                                        documentSnapshots) {
                                    tipsModals.add(new TipsModal(d.getString("image"),
                                            d.getString("title"),
                                            d.getString("desc")));
                                    tipsAdapter = new TipsAdapter(tipsModals, TipsActivity.this);
                                    recyclerView.setAdapter(tipsAdapter);

                                }
                            }
                        }
                        dismissLoader();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissLoader();
                        Toast.makeText(TipsActivity.this, "Something went wrong. Try after some time !!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }
}