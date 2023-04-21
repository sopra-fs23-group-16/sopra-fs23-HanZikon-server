package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.QuestionPacker;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class GameService {

    private RoomManager roomManager;
    private GameManager gameManager;

    public GameService() {
        this.roomManager = new RoomManager();
        this.gameManager = new GameManager();
    }

    public Player createPlayer(User gamer){
        return new Player(gamer);
    }

    public Room createRoom(Player owner,GameParamDTO gameParam){
        String roomCode = generateRoomCode();
        Room newRoom = new Room(roomCode,owner,gameParam);
        this.roomManager.addRoom(newRoom);
        return newRoom;
    }

    /**
     * When all room players are ready, start the game
     * Or accept the game owner control to start the game
     * @param roomID
     * @return
     */
    public List<QuestionDTO> createGame(int roomID){
        Room foundRoom = findRoomByID(roomID);
        Game newGame = new Game(foundRoom);
        this.gameManager.addGame(newGame);
//        List<QuestionDTO> questionList = QuestionPacker.getQuestionList(foundRoom.getGameParam());
        List<QuestionDTO> questionList = new ArrayList<>();
        return questionList;

    }

    private String generateRoomCode(){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 6;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(characters.length());
            char randomChar = characters.charAt(idx);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public Room findRoomByID(int roomID){
        return this.roomManager.findByRoomID(roomID);
    }

    public Room findRoomByCode(String roomCode) { return this.roomManager.findByRoomCode(roomCode); };

    private List<Player> findRoomPlayersByRoomID(int roomID){
        Room findRoom = this.roomManager.findByRoomID(roomID);
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
        Room findRoom = this.roomManager.findByRoomID(roomID);
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
        Game findGame = this.gameManager.findByRoomID(roomID);
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
