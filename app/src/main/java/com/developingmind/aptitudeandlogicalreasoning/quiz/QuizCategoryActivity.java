package com.developingmind.aptitudeandlogicalreasoning.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    private int chooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_category);

        recyclerView = findViewById(R.id.quizzes_category_rv);
        progressdialog = new DialogMaker(this);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        chooser = getIntent().getIntExtra("index",0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();

        final QuizzesCategoryAdapter quizzesCategoryAdapter = new QuizzesCategoryAdapter(list,this);
        recyclerView.setAdapter(quizzesCategoryAdapter);

        showDialog();

        String databaseKey = (chooser==0)?DatabaseEnum.categories.toString():DatabaseEnum.categories.toString();

        firebaseFirestore.collection(databaseKey)
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

    private void showDialog(){
        progressdialog.getDialog().show();
    }

    private void dismissDialog(){
        progressdialog.getDialog().hide();
    }
}