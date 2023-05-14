package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class PlayerVoteDTO {
    private Long fromUserID;
    private Long userID;
    private String userName;
    private int round;
    private int votedTimes; // the player is being voted times
    private int votedScore;
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getVotedTimes() {
        return votedTimes;
    }

    public void setVotedTimes(int votedTimes) {
        this.votedTimes = votedTimes;
    }

    public int getVotedScore() {
        return votedScore;
    }

    public void setVotedScore(int votedScore) {
        this.votedScore = votedScore;
    }

    public Long getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(Long fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
