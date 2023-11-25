package com.example.game_puzzle_pursuit;

public class Account {
    public String userName;
    public String email;
    public String password;
    public int totalScore;

    public Account(){

    }

    public Account(String userName, String email, String password, int totalScore) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.totalScore = totalScore;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
