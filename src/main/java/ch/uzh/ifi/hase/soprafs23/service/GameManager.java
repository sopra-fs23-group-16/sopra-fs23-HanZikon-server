package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> roomIDs;

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
}