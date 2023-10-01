package com.developingmind.aptitudeandlogicalreasoning.test.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DatabaseEnum;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
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
import java.util.Map;

public class TopicSelectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static List<TopicSelectionModal> list;

    Button submit;

    DialogMaker progressdialog;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_topic);

        recyclerView = findViewById(R.id.recycler_view);
        progressdialog = new DialogMaker(this);
        toolbar = findViewById(R.id.toolbar);
        submit = findViewById(R.id.submit_btn);

        setSupportActionBar(toolbar);

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

        final TopicSelectionAdapter topicSelectionAdapter = new TopicSelectionAdapter(list,this);
        recyclerView.setAdapter(topicSelectionAdapter);

        showDialog();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("List", topicSelectionAdapter.getSelectedList().toString());
            }
        });


        firebaseFirestore.collection(DatabaseEnum.aptitude.toString())
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

    private void showDialog(){
        progressdialog.getDialog().show();
    }

    private void dismissDialog(){
        progressdialog.getDialog().dismiss();
    }
}