package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {

    private static int instanceID = 1;

    private int roomID;
    private Player owner;

    private int roomNumber;

    private String password;

    private String invitationLink;

    private GameParam gameParam;

    /**
     * client can subscribe '/room/{roomID}'
     * then room needs not manage players
     */
    private List<Player> players = new CopyOnWriteArrayList<>(); //concurrent

    public Room(Player owner, GameParamDTO gameParam){
        // do something with the gameParam
        this.roomID = instanceID++;
        this.owner = owner;
        this.players.add(owner);
    }

    public int getRoomID() {
        return roomID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public GameParam getGameParam() {
        return gameParam;
    }

    public void setGameParam(GameParam gameParam) {
        this.gameParam = gameParam;
    }

}
