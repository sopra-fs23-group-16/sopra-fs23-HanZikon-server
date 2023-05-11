package ch.uzh.ifi.hase.soprafs23.websocket.dto;

import java.io.Serializable;
public class PlayerImitationDTO implements Serializable {
    //playerID will be generated when convert it to player
    private Long userID;

    private int round;
    private String imitationBytes;

    private String username;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }


    public String getImitationBytes() {
        return imitationBytes;
    }

    public void setImitationBytes(String imitationBytes) {
        this.imitationBytes = imitationBytes;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
