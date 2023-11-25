package com.example.game_puzzle_pursuit;

public class Answer {

    private String content;
    private boolean correct;

    public Answer() {
        // Default constructor required for calls to DataSnapshot.getValue(Answer.class)
    }

    public Answer(String content, boolean correct) {
        this.content = content;
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public boolean isCorrect() {
        return correct;
    }
}
