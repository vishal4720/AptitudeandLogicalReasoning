package com.developingmind.aptitudeandlogicalreasoning.leaderboard;

public class LeaderboardModal {
    private String name,score;

    public LeaderboardModal(String name, String score) {
        this.name = name;
        this.score = score;
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
}
