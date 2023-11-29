package com.developingmind.aptitudeandlogicalreasoning.bookmark;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developingmind.aptitudeandlogicalreasoning.AdManager;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionModal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CompetitiveBookmarkFragment extends Fragment {

    private RecyclerView competitiveRecyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;
    CompetitiveBookmarkAdapter bookmarkAdapter;

    List<QuestionModal> bookmarklist = new ArrayList<>();
    LinearLayoutCompat noBookmark;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_competitive_bookmark, container, false);

        competitiveRecyclerView = view.findViewById(R.id.recycler_view_competitive);
        noBookmark = view.findViewById(R.id.noBookmark);

        LinearLayoutManager competitiveLinearLayoutManager = new LinearLayoutManager(getContext());
        competitiveLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        competitiveRecyclerView.setLayoutManager(competitiveLinearLayoutManager);

        sharedPreferences = getContext().getSharedPreferences("MyPref",MODE_PRIVATE);

        editor = sharedPreferences.edit();
        gson = new Gson();

        getCompetitiveBookmark();
        return view;
    }

    private void getCompetitiveBookmark(){
        String json = sharedPreferences.getString("bookmarkCompetitive"+((BookmarkActivity)getContext()).isAptitude,"");
//        Log.d("Bookmark Json Activity",sharedPreferences.getString("bookmarkCompetitive"+isAptitude,""));
        Log.d("Bookmark Json",json);
        Type type = new TypeToken<List<QuestionModal>>(){}.getType();
        bookmarklist = gson.fromJson(json,type);

        if (bookmarklist == null || bookmarklist.isEmpty()){
            bookmarklist = new ArrayList<>();
            noBookmark.setVisibility(View.VISIBLE);
            competitiveRecyclerView.setVisibility(View.GONE);
        }

        AdManager adManager = (AdManager) getContext().getApplicationContext();
        Log.d("Bookmark",bookmarklist.toString());
        bookmarkAdapter = new CompetitiveBookmarkAdapter(getContext(),bookmarklist,sharedPreferences,adManager,((BookmarkActivity) getContext()).getSupportFragmentManager());
        competitiveRecyclerView.setAdapter(bookmarkAdapter);

    }

    public void storeCompititiveBookmarks(){
        String json = gson.toJson(bookmarklist);
        editor.putString("bookmarkCompetitive"+((BookmarkActivity)getContext()).isAptitude,json);
        editor.apply();
    }


    public void deleteCompetitiveBookmark(int position){
        if (position!=RecyclerView.NO_POSITION) {
            bookmarklist.remove(position);
            bookmarkAdapter.notifyItemRemoved(position);
            if (bookmarklist.isEmpty()) {
                competitiveRecyclerView.setVisibility(View.GONE);
                noBookmark.setVisibility(View.VISIBLE);
            }
        }
    }
}
