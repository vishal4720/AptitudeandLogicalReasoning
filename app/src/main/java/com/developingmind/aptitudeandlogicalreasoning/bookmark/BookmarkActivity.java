package com.developingmind.aptitudeandlogicalreasoning.bookmark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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