package com.developingmind.aptitudeandlogicalreasoning.test.test;

public class TopicSelectionModal {

    private Integer questionCount;
    private String topic;

    public TopicSelectionModal(int questionCount, String topic) {
        this.questionCount = questionCount;
        this.topic = topic;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
