package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class PlayerStatusDTO {
    //playerID will be generated when convert it to player
    private Long userID;
    private Boolean isReady;
    private Boolean isWriting;  //upon submission: false

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public Boolean isWriting() {
        return isWriting;
    }

    public void setWriting(boolean writing) {
        isWriting = writing;
    }

}
