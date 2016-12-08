package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 18/11/16.
 *
 "email_id": "test@adof360.com",
 "fb_id": "test@adof360.com"
 "age": "25"
 "gender": "m" (OR f  in case of female)
 */

public class User {
    private String name = "";
    private String emailId = "";
    private String fbId = "";
    private int age = 0;
    private String gender = "";
    private int uuid = 0;
    private boolean playedTournament = false;
    private boolean concludedTournament = false;

    private boolean isWinner = false;

    private int gameScore = 0;
    private int gameLevel = 0;

    public User() {}

    public User(int uuid, String name, String emailId, String fbId, int age, String gender) {
        this.name = name;
        this.emailId = emailId;
        this.fbId = fbId;
        this.age = age;
        this.gender = gender;
        this.uuid = uuid;
    }

    public int getUuid() {  return this.uuid; }
    public String getUserName() {  return this.name; }
    public String getEmailId() { return emailId; }
    public String getFbId() { return fbId; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public boolean getPlayedTourFlag() { return this.playedTournament; }
    public boolean getConcludedTourFlag() { return this.concludedTournament; }

    public void setPlayedTournament() { this.playedTournament = true; }
    public void setConcludedTournament() { this.concludedTournament = true; }

    public void setGameScore(int gScore) { this.gameScore = gScore;}
    public void setGameLevel(int gLevel) { this.gameLevel = gLevel; }

    public int getGameScore() { return this.gameScore; }
    public int getGameLevel() { return this.gameLevel; }

    public void setWinnerFlag(boolean isWinnerFlag) { this.isWinner = isWinnerFlag; }
    public boolean getIsWinner() { return this.isWinner; }

    public String toString() {
        StringBuilder objDetails = new StringBuilder("User=(");
        objDetails.append("Uuid=" + String.valueOf(this.uuid) + ", ");
        objDetails.append("Name=" + this.name + ", ");
        objDetails.append("EmailId=" + this.emailId + ", ");
        objDetails.append("FacebookId=" + this.fbId + ", ");
        objDetails.append("Age=" + String.valueOf(this.age) + ", ");
        objDetails.append("Gender=" + this.gender + ", ");
        objDetails.append("PlayedTournament=" + this.playedTournament + ", ");
        objDetails.append("ConcludedTournament=" + this.concludedTournament + ", ");
        objDetails.append("HasWon=" + String.valueOf(this.isWinner));
        objDetails.append(")");

        return objDetails.toString();
    }
}
