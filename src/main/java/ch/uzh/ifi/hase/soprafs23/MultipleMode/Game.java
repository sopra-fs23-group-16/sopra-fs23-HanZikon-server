package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Game {
    private int roomID;
    private LinkedHashMap<Long,Player> players;

    public Game(Room room){
        this.roomID = room.getRoomID();
        this.players = room.getPlayersHashmap();
    }
    public int getRoomID() {
        return roomID;
    }
    public List<Player> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

}