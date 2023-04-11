package ch.uzh.ifi.hase.soprafs23.MultipleMode;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Room {

    private static int instanceID = 1;

    private int roomID;

    private final String roomCode;
    private Player owner;

    private int roomNumber;

    private String password;

    private String invitationLink;

    private GameParam gameParam;

    /**
     * client can subscribe '/room/{roomID}'
     * then room needs not manage players
     */
    private ConcurrentHashMap<Long,Player> players = new ConcurrentHashMap<>();

    public Room(String roomCode, Player owner, GameParamDTO gameParam){
        // do something with the gameParam
        this.roomID = instanceID++;
        this.roomCode = roomCode;
        this.owner = owner;
        this.addPlayer(owner);
    }

    public int getRoomID() {
        return roomID;
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

    public void addPlayer(Player player) {
        this.players.put(player.getUserID(),player);
    }

    public void removePlayer(Player player){
        this.players.remove(player.getUserID(),player);
    }

    public GameParam getGameParam() {
        return gameParam;
    }

    public void setGameParam(GameParam gameParam) {
        this.gameParam = gameParam;
    }

    public String getRoomCode(){
        return this.roomCode;
    }

}
