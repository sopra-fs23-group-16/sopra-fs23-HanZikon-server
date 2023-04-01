package ch.uzh.ifi.hase.soprafs23.websocket.dto;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;

import java.util.ArrayList;

public class GameDTO {
    // assign
    private static int instanceID = 1;

    private int roomID;

    private ArrayList<Player> players;

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public GameDTO(ArrayList<Player> players, int roomID){
        this.roomID = instanceID;
        this.players = players;
    }


}
