package ch.uzh.ifi.hase.soprafs23.constant;

public enum UserStatus {
    ONLINE("online"), OFFLINE("offline");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
