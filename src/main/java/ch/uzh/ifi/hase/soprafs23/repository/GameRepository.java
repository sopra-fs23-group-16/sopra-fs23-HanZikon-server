package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

public class GameRepository {
    private static final HashMap<Integer, Game> gameRepository = new HashMap<>();

    private GameRepository() {
    }

    public static void addGame(int roomId, Game game) {
        gameRepository.put(roomId, game);
    }

    public static void removeGame(int roomId) {
        gameRepository.remove(roomId);
    }

    public static Game findByRoomId(int roomId) {
        Game game = gameRepository.get(roomId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not exist!");
        }
        return game;
    }
}