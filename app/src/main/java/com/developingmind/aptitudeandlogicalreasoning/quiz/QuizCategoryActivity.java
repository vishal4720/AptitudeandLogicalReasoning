package com.developingmind.aptitudeandlogicalreasoning.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuizCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static List<String> list;

    DialogMaker progressdialog;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private Toolbar toolbar;
    private int formulaCount = 0;
    private QuizzesCategoryAdapter quizzesCategoryAdapter;

    private boolean isAptitude,isPractice,isStudy,isSolved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_category);

        recyclerView = findViewById(R.id.quizzes_category_rv);
        progressdialog = new DialogMaker(this);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        isAptitude = getIntent().getBooleanExtra(Constants.isAptitude, true);
        isPractice = getIntent().getBooleanExtra(Constants.isPractice, true);
        isStudy = getIntent().getBooleanExtra(Constants.isStudy, false);
        isSolved = getIntent().getBooleanExtra(Constants.isSolvedProblems, false);

        if (isPractice){
            toolbar.setTitle("Practice");
        } else if (isStudy){
            toolbar.setTitle("Formulas");
        }else if (isSolved){
            toolbar.setTitle("Solved Problems");
        }else{
            toolbar.setTitle("Tips & Tricks");
        }

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

        quizzesCategoryAdapter = new QuizzesCategoryAdapter(list,this,isAptitude,isPractice,isStudy,isSolved);
        recyclerView.setAdapter(quizzesCategoryAdapter);

        showDialog();

        String key;
        if(isAptitude)
            key = DatabaseEnum.aptitude.toString();
        else
            key = DatabaseEnum.logical.toString();

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
                                list.add(doc.getId());
                            }
                            quizzesCategoryAdapter.notifyDataSetChanged();

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

    public void showDialog(){
        progressdialog.getDialog().show();
    }

    public void dismissDialog(){
        progressdialog.getDialog().dismiss();
    }
}