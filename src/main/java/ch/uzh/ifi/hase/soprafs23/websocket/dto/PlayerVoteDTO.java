package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class PlayerVoteDTO {

    //playerID will be generated when convert it to player
    private Long fromPlayer;
    private Long userID; // being voted user
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

    public Long getFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(Long fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

}
