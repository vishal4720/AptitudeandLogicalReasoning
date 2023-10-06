package com.developingmind.aptitudeandlogicalreasoning.study;

public class StudyModal {

    private String img,title,description;

    public StudyModal(String img, String title, String description) {
        this.img = img;
        this.title = title;
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
