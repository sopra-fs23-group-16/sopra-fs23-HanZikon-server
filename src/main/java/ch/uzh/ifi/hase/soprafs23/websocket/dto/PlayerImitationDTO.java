package ch.uzh.ifi.hase.soprafs23.websocket.dto;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.ScoreBoard;

import java.nio.ByteBuffer;

public class PlayerImitationDTO {
    //playerID will be generated when convert it to player
    private Long userID;

    private String characterId;
    private ByteBuffer imitationBytes;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }


    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public ByteBuffer getImitationBytes() {
        return imitationBytes;
    }

    public void setImitationBytes(ByteBuffer imitationBytes) {
        this.imitationBytes = imitationBytes;
    }

}
