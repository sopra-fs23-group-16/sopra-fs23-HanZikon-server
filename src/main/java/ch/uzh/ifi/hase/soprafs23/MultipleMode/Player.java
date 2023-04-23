package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.entity.User;

public class Player {
    // assign
    private static int instanceID = 1;

    private int playerID;

    private String playerName;

    private Long userID;

    private boolean isReady;

    private boolean isWriting;  //upon submission: false

    private ScoreBoard scoreBoard;

    public Player(User gamer){
        this.playerID = instanceID++;
        this.userID = gamer.getId();//registered user: userID(1,2,3,……); unregistered user: userID(0001,0002,0003,……)
        this.playerName = gamer.getUsername();
        /**
         * default isReady = false
         */
        this.isReady = false;
        this.isWriting = true;
        this.scoreBoard = new ScoreBoard();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerID() {
        return playerID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isWriting() {
        return isWriting;
    }

    public void setWriting(boolean writing) {
        isWriting = writing;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }
}
