package ch.uzh.ifi.hase.soprafs23.websocket.dto;

import java.io.Serializable;
public class PlayerImitationDTO implements Serializable {
    //playerID will be generated when convert it to player
    private Long userID;

    // private String characterId;
    private String imitationBytes;

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

}
