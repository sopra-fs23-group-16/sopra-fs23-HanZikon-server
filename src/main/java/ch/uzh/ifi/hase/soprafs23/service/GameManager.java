package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerImitationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> roomIDs;

    private ConcurrentHashMap<Long, PlayerImitationDTO> gameImitationsMap = new ConcurrentHashMap<>();

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

    public void addPlayerImitation(PlayerImitationDTO playerImitationDTO) {
        gameImitationsMap.put(playerImitationDTO.getUserID(), playerImitationDTO);

        // This is just for log testing
        if(gameImitationsMap.size() != 0) {
            for (Map.Entry<Long, PlayerImitationDTO> entry : gameImitationsMap.entrySet()) {
                Long key = entry.getKey();
                PlayerImitationDTO value = entry.getValue();
                log.info("Retrieve the map<roomID, List> of player bytes 1: " + key + " => " + value);
            }
        }

    }

    public void removePlayerImitation(Long userId) {
        gameImitationsMap.remove(userId);
    }

    public PlayerImitationDTO findImgByUserID(Long userID) {
        PlayerImitationDTO playerImitation = gameImitationsMap.get(userID);
        if (playerImitation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not exist!");
        }
        return playerImitation;
    }

}