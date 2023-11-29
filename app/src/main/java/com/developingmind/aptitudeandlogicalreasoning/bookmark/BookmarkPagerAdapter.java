package com.developingmind.aptitudeandlogicalreasoning.bookmark;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BookmarkPagerAdapter extends FragmentStateAdapter {

    private Context myContext;
    int totalTabs;

    public BookmarkPagerAdapter(@NonNull Context context,@NonNull FragmentActivity fragmentActivity,int totalTabs) {
        super(fragmentActivity);
        myContext = context;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("Fragment", String.valueOf(position));
        switch (position) {
            case 0:
                return new PracticeBookmarkFragment();
            case 1:
                return new CompetitiveBookmarkFragment();
        }
        return new PracticeBookmarkFragment();
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
