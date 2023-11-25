package com.example.game_puzzle_pursuit;

import java.util.List;
import java.util.Map;

public class Question {
    private String content;
    private Map<String, Answer> answers;

    public Question() {
        // Default constructor required for calls to DataSnapshot.getValue(Question.class)
    }

    public Question(String content, Map<String, Answer> answers) {
        this.content = content;
        this.answers = answers;
    }

    public String getContent() {
        return content;
    }

    public Map<String, Answer> getAnswers() {
        return answers;
    }
}
