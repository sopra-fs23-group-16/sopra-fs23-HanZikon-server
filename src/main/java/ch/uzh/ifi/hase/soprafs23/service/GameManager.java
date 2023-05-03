package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> roomIDs;

    private Map<Long, String> playerImitation = new HashMap<>();
    private ConcurrentHashMap<Integer, Map<Long, String>> gameImitations = new ConcurrentHashMap<>();

    Logger log = LoggerFactory.getLogger(GameManager.class);

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

    public void addPlayerImitation(int roomID, Long userId, String imitationBytes) {
        playerImitation.put(userId, imitationBytes);
        gameImitations.put(roomID, playerImitation);
        log.info("Add the player imitation in game manager {}", playerImitation.get(userId));
    }

    public void removePlayerImitation(int roomID, Long userId) {
        Map<Long, String> playersImitations = getPlayerImitations(roomID);
        if((playersImitations != null) && (playersImitations.get(userId) != null)){
            playersImitations.remove(playersImitations.get(userId));
        }
    }


    public Map<Long, String>  getPlayerImitations(int roomID) {
        Map<Long, String> playersImitations = gameImitations.get(roomID);
        if (playersImitations == null) {
            log.info("There is no player's imitations yet!");
        }
        return playersImitations;
    }
}