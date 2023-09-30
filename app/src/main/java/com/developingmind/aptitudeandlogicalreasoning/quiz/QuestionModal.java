package com.developingmind.aptitudeandlogicalreasoning.quiz;

import android.widget.Button;

public class QuestionModal {

    private String question,optionA,optionB,optionC,optionD,correctAns,id,explanation,categoryId;
    private Boolean isAnswered = false;
    private Boolean isVisited = false;
    private Button givenAns = null;
    public QuestionModal(){

    }

    public QuestionModal(String question, String optionA, String optionB, String optionC, String optionD, String correctAns,String explanation,String categoryId, String id) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAns = correctAns;
        this.explanation = explanation;
        this.categoryId = categoryId;
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getAnswered() {
        return isAnswered;
    }

    public void setAnswered(Boolean answered) {
        isAnswered = answered;
    }

    public Button getGivenAns() {
        return givenAns;
    }

    public void setGivenAns(Button givenAns) {
        this.givenAns = givenAns;
    }

    public Boolean getVisited() {
        return isVisited;
    }

    public void setVisited(Boolean visited) {
        isVisited = visited;
    }
}
