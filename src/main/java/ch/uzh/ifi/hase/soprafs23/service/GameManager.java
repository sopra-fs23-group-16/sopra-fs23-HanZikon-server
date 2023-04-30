package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> roomIDs;

    private Map<Long, ByteBuffer> playerImitation = new HashMap<>();
    private ConcurrentHashMap<Integer, Map<Long, ByteBuffer>> gameImitations = new ConcurrentHashMap<>();

    public GameManager() {
        this.roomIDs = new ConcurrentHashMap<>();
    }

    public void addGame(Game game) {
        roomIDs.put(game.getRoomID(), game);
    }
    public void removeGame(Game game) {
        roomIDs.remove(game.getRoomID());
    }

    public Game findByRoomID(int roomID) {
        Game game = roomIDs.get(roomID);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not exist!");
        }
        return game;
    }

    public void addPlayerImitation(int roomID, Long userId, ByteBuffer imitationBytes) {
        playerImitation.put(userId, imitationBytes);
        gameImitations.put(roomID, playerImitation);
    }

    public void removePlayerImitation(int roomID, Long userId) {
        Map<Long, ByteBuffer> playersImitations = getPlayerImitations(roomID);
        playersImitations.remove(playersImitations.get(userId));
    }


    public Map<Long, ByteBuffer>  getPlayerImitations(int roomID) {
        Map<Long, ByteBuffer> playersImitations = gameImitations.get(roomID);
        if (playersImitations == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player's imitations in this room does not exist!");
        }
        return playersImitations;
    }
}