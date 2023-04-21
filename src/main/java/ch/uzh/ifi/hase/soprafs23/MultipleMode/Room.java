package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Room {

    private static int instanceID = 1;

    private int roomID;
    private final String roomCode;
    private Player owner;
    private LinkedHashMap<Long,Player> players = new LinkedHashMap<>();
    private GameParamDTO gameParam;
    /**
     * client can subscribe '/room/{roomID}'
     * then room needs not manage players
     */

    public Room(String roomCode, Player owner, GameParamDTO gameParam){
        // do something with the gameParam
        this.roomID = instanceID++;
        this.roomCode = roomCode;
        this.owner = owner;
        this.addPlayer(owner);
        this.gameParam = gameParam;
    }

    public int getRoomID() {
        return roomID;
    }

    public LinkedHashMap<Long,Player> getPlayersHashmap(){
        return this.players;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public Player findPlayerByUserID(Long userID){
        Player player = this.players.get(userID);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This player does not exist!");
        }
        return player;
    }

    public boolean checkIsFull(){
        return this.players.size() == this.gameParam.getNumPlayers();
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUserID(),player);
    }

    public void removePlayer(Player player){
        this.players.remove(player.getUserID(),player);
    }

    public void updatePlayer(Player player){
        this.players.remove(player.getUserID(),player);
        this.players.put(player.getUserID(),player);
    }

    public GameParamDTO getGameParam() {
        return gameParam;
    }

    public void setGameParam(GameParamDTO gameParam) {
        this.gameParam = gameParam;
    }

    public String getRoomCode(){
        return this.roomCode;
    }

    public Player getOwner() {
        return owner;
    }

}
