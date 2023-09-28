package com.developingmind.aptitudeandlogicalreasoning.leaderboard;

import android.net.Uri;

public class LeaderboardModal {
    private String name,score,gender;
    Uri profile;

    public LeaderboardModal(String name, String score,Uri profile,String gender) {
        this.name = name;
        this.score = score;
        this.profile = profile;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Uri getProfile() {
        return profile;
    }

    public void setProfile(Uri profile) {
        this.profile = profile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
