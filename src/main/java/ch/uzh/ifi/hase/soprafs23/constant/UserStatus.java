package ch.uzh.ifi.hase.soprafs23.constant;


// I don't delete this, as it might be useful for distinguish "tourist" and "logged in users".
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
