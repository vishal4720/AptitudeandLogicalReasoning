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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PracticeBookmarkFragment extends Fragment {

    private RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Map<String,List<QuestionModal>> bookmarkMap = new HashMap<>();
    Gson gson;
    PracticeBookmarkAdapter practiceBookmarkAdapter;

    List<QuestionModal> bookmarklist = new ArrayList<>();
    LinearLayoutCompat noBookmark;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice_bookmark, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noBookmark = view.findViewById(R.id.noBookmark);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        sharedPreferences = getContext().getSharedPreferences("MyPref",MODE_PRIVATE);

        editor = sharedPreferences.edit();
        gson = new Gson();

        getBookmark();
        return view;
    }

    private void getBookmark(){
        String json = sharedPreferences.getString("bookmark"+((BookmarkActivity)getContext()).isAptitude,"");
//        Log.d("Bookmark Json Activity",sharedPreferences.getString("bookmarkCompetitive"+isAptitude,""));
        Log.d("Bookmark Json",json);
        Type type = new TypeToken<Map<String, List<QuestionModal>>>(){}.getType();
        bookmarkMap = new HashMap<>();
        bookmarkMap = gson.fromJson(json,type);

        if (bookmarkMap!=null && !bookmarkMap.isEmpty()) {
            Iterator<Map.Entry<String, List<QuestionModal>>> iterator = bookmarkMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<QuestionModal>> x = iterator.next();
                Log.d("Bookmark Json Iterator", String.valueOf(x.getValue()));
                bookmarklist.addAll(x.getValue());
            }
        }

        if (bookmarklist == null || bookmarklist.isEmpty()){
            bookmarklist = new ArrayList<>();
            recyclerView.setVisibility(View.GONE);
            noBookmark.setVisibility(View.VISIBLE);
        }

        AdManager adManager = (AdManager) getContext().getApplicationContext();
        Log.d("Bookmark",bookmarklist.toString());
        practiceBookmarkAdapter = new PracticeBookmarkAdapter(getContext(),bookmarklist,sharedPreferences,adManager,((BookmarkActivity) getContext()).getSupportFragmentManager());
        recyclerView.setAdapter(practiceBookmarkAdapter);

    }

    public void deleteBookmark(int position){
        if (position!=RecyclerView.NO_POSITION) {
            practiceBookmarkAdapter.notifyItemRemoved(position);
            practiceBookmarkAdapter.notifyItemRangeChanged(position, bookmarklist.size());
            bookmarklist.remove(position);
            if (bookmarklist.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noBookmark.setVisibility(View.VISIBLE);
            }
        }
    }

    public void storeBookmark(){
        Map<String,List<QuestionModal>> map = new HashMap<>();
        List<QuestionModal> questionModals;
        Log.d("Bookmark Json Practice Size", String.valueOf(bookmarklist.size()));
        for (QuestionModal bookmark: bookmarklist) {
            questionModals = new ArrayList<>();
            String categoryId = bookmark.getCategoryId();
            Log.d("Bookmark Json Category Id", categoryId);
            if(map.containsKey(categoryId)){
                questionModals.addAll(map.get(categoryId));
                Log.d("Bookmark Json Getting Map", String.valueOf(questionModals.size()));
            }
            questionModals.add(bookmark);
            map.put(categoryId,questionModals);

            Log.d("Bookmark Json Practice Map",map.toString());
        }
        String json = gson.toJson(map);
        Log.d("Bookmark Json Practice", json);
        editor.putString("bookmark" + ((BookmarkActivity)getContext()).isAptitude, json);
        editor.apply();
    }
}
