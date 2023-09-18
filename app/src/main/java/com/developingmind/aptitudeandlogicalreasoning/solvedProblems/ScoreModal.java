package com.developingmind.aptitudeandlogicalreasoning.solvedProblems;

public class ScoreModal {
    private String question,answer,explanation;

    public ScoreModal(){}

    public ScoreModal(String question,String answer,String explanation){
        this.question = question;
        this.answer = answer;
        this.explanation = explanation;
    }

    public String getQuestion(){
        return this.question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
