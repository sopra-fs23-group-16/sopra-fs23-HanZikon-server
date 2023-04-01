package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;

import java.util.List;

public class Game {

    private int roomID;

    private List<Player> players;

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Game(List<Player> players, int roomID){
        this.roomID = roomID;
        this.players = players;
    }

}
