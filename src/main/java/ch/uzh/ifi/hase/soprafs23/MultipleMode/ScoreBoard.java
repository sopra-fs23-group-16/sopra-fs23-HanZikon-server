package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import java.math.BigDecimal;

public class ScoreBoard {
    // assign
    private static int instanceID = 1;

    private int votedScore;  // This votedScore should be already averaged by scores given by others in the front end
    private int systemScore;

    private double systemScorePercentage = 0.5;

    public int getVotedScore() {
        return votedScore;
    }

    public void setVotedScore(int votedScore) {
        this.votedScore = votedScore;
    }

    public int getSystemScore() {
        return systemScore;
    }

    public void setSystemScore(int systemScore) {
        this.systemScore = systemScore;
    }

    public int getWeightedScore() {
        int wightedScore = 0;
        wightedScore = (int) ((systemScore* systemScorePercentage) + votedScore*(1-systemScorePercentage));
        return wightedScore;
    }

}