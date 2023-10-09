package com.developingmind.aptitudeandlogicalreasoning.bookmark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionModal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    List<QuestionModal> bookmarklist;
    Gson gson;

    BookmarkAdapter bookmarkAdapter;
    Boolean isAptitude;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);

        isAptitude = getIntent().getBooleanExtra(Constants.isAptitude,true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        gson = new Gson();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getBookmark();
    }

    private void getBookmark(){

        String json = sharedPreferences.getString("bookmark"+isAptitude,"");

        Type type = new TypeToken<List<QuestionModal>>(){}.getType();

        bookmarklist = gson.fromJson(json,type);
        if (bookmarklist == null || bookmarklist.isEmpty()){
            bookmarklist = new ArrayList<>();
            Toast.makeText(this, "No Questions Bookmarked", Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.d("Bookmark",bookmarklist.toString());
        bookmarkAdapter = new BookmarkAdapter(this,bookmarklist);
        recyclerView.setAdapter(bookmarkAdapter);
    }
}