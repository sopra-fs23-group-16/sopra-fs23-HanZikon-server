package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class PlayerDTO {
    //playerID will be generated when convert it to player
    private Long userID;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
}
