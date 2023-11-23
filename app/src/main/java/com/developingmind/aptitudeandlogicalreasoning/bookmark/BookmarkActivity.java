package com.developingmind.aptitudeandlogicalreasoning.bookmark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.developingmind.aptitudeandlogicalreasoning.AdManager;
import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.quiz.QuestionModal;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BookmarkActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    BookmarkPagerAdapter bookmarkPagerAdapter;
    public Boolean isAptitude;
    DialogMaker loadingDialog;
    private String[] labels = new String[]{"Practice", "Competitive"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        toolbar = findViewById(R.id.toolbar);
        loadingDialog = new DialogMaker(this);
        isAptitude = getIntent().getBooleanExtra(Constants.isAptitude,true);

        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        viewPager=(ViewPager2)findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText(labels[0]),true);
        tabLayout.addTab(tabLayout.newTab().setText(labels[1]));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        bookmarkPagerAdapter = new BookmarkPagerAdapter(this,this,tabLayout.getTabCount());
        viewPager.setAdapter(bookmarkPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(labels[position])
        ).attach();

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        ((PracticeBookmarkFragment)getSupportFragmentManager().getFragments().get(0)).storeBookmark();
        ((CompetitiveBookmarkFragment)getSupportFragmentManager().getFragments().get(1)).storeCompititiveBookmarks();
        super.onPause();
    }
    
}