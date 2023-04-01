package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;

public class Player {
    // assign
    private static int instanceID = 1;

    private int playerID;
    private String playerName;

    private int userID;

    private boolean isReady;

    private boolean isWriting;

    private ScoreBoard scoreBoard;

    public Player(PlayerDTO playerDTO){
        this.playerID = instanceID++;
        this.playerName = playerDTO.getUserName();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
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
