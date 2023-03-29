package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;

public class Player {
    // assign
    private static int instanceID = 1;

    private int playerID;
    private String playerName;

    public Player(PlayerDTO playerDTO){
        this.playerID = instanceID++;
        this.playerName = playerDTO.getUserName();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerID() {
        return playerID;
    }
}
