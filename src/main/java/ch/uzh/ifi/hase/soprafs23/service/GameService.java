package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.GameParam;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GameService {

    public GameService() {
    }

    public Player createPlayer(PlayerDTO playerDTO){
        return new Player(playerDTO);
    }
    public Room createRoom(Player owner,GameParamDTO gameParam){
        Room newRoom = new Room(owner,gameParam);
        RoomRepository.addRoom(newRoom.getRoomID(), newRoom);
        return newRoom;
    }

    /**
     * When all room players are ready, start the game
     * Or accept the game owner control to start the game
     * @param players
     * @param roomID
     * @return
     */
    public Game createGame(List<Player> players, int roomID){

        if(checkALlReady(roomID) == true){
            return new Game(findRoomPlayersByRoomID(roomID),roomID);
        }

        return new Game(players,roomID);
    }

    private List<Player> findRoomPlayersByRoomID(int roomID){
        Room findRoom = RoomRepository.findByRoomId(roomID);
        List<Player> roomPlayers = findRoom.getPlayers();
        if (roomPlayers == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not have players!");
        }
        return roomPlayers;
    }

    /**
     * When all players join the room
     * and all players isReady status is ture, to broadcast to clients end to start the game
     * @param roomID
     * @return
     */
    public boolean checkALlReady(int roomID){
        Room findRoom = RoomRepository.findByRoomId(roomID);
        int setPlayersNum = findRoom.getGameParam().getNumPlayers();
        List<Player> roomPlayers = findRoom.getPlayers();
        if(roomPlayers.size() != setPlayersNum){
            return false;
        } else {
            for(int i=0; i<roomPlayers.size(); i++){
                if (roomPlayers.get(i).isReady() != true) {
                    return false;
                }
            }
            return true;
        }
    }

    private List<Player> findGamePlayersByRoomID(int roomID){
        Game findGame = GameRepository.findByRoomId(roomID);
        List<Player> gamePlayers = findGame.getPlayers();
        if (gamePlayers == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No players in this game!");
        }
        return gamePlayers;
    }

    /**
     * Check whether all players finish writing
     * @param roomID
     * @return
     */
    public boolean checkALlFinish(int roomID){
        List<Player> roomPlayers = findGamePlayersByRoomID(roomID);
        for(int i=0; i<roomPlayers.size(); i++){
            if (roomPlayers.get(i).isWriting() == true) {
                return false;
            }
        }

        return true;
    }

    public LinkedHashMap<Integer, Player> calculateRanking(int roomID){
        LinkedHashMap<Integer, Player> playerRanking = new LinkedHashMap<>();
        List<Player> roomPlayers = findGamePlayersByRoomID(roomID);
        for(int i=0; i<roomPlayers.size(); i++){
            Player player = roomPlayers.get(i);
            int score = player.getScoreBoard().getWeightedScore();
            playerRanking.put(score, player);
        }

        return playerRanking;
    }


}
