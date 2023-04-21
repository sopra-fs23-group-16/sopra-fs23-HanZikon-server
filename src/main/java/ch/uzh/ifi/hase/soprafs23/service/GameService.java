package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.QuestionPacker;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerScoreBoardDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class GameService {

    private RoomManager roomManager;
    private GameManager gameManager;

    GameRecordService gameRecordService;

    Logger log = LoggerFactory.getLogger(GameService.class);

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
    public List<QuestionDTO> createGame(int roomID,QuestionPacker questionPacker){
        Room foundRoom = findRoomByID(roomID);
        Game newGame = new Game(foundRoom);
        this.gameManager.addGame(newGame);

        List<QuestionDTO> questionList = questionPacker.getQuestionList(foundRoom.getGameParam());
        //List<QuestionDTO> questionList = new ArrayList<>();
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


    /**
     * Check whether all players finish writing, reset all players isWriting= true
     * @param roomID
     * @return
     */
    public void nextQuestion(int roomID){
        Room findRoom = this.roomManager.findByRoomID(roomID);

        List<Player> gamePlayers = findGamePlayersByRoomID(roomID);
        for(int i=0; i<gamePlayers.size(); i++){
            Player roomPlayer = gamePlayers.get(i);
            roomPlayer.setWriting(true);
            findRoom.updatePlayer(roomPlayer);
        }
    }

    /**
     * 1. Update the player status isReady= true get ready for game (each round)
     * @param roomID
     * @param playerDTO
     * @return
     */
    public Player isPlayerReady(int roomID, PlayerStatusDTO playerDTO){
        Room findRoom = this.roomManager.findByRoomID(roomID);

        Player updatePlayer = findRoom.findPlayerByUserID(playerDTO.getUserID());

        if(playerDTO.isReady() != null){
            updatePlayer.setReady(playerDTO.isReady());
        }

        // Used to update the corresponded room
        findRoom.updatePlayer(updatePlayer);

        return updatePlayer;
    }

    /**
     * 1. Update the player status isWriting in the room
     * @param roomID
     * @param playerDTO
     * @return
     */
    public Player isPlayerWriting(int roomID, PlayerStatusDTO playerDTO){
        Room findRoom = this.roomManager.findByRoomID(roomID);

        Player updatePlayer = findRoom.findPlayerByUserID(playerDTO.getUserID());

        if(playerDTO.isWriting() != null){
            updatePlayer.setWriting(playerDTO.isWriting());
        }

        // Used to update the corresponded room
        findRoom.updatePlayer(updatePlayer);

        return updatePlayer;
    }

    /**
     * 1. Accumulate the player score according to scoreBoard passed
     * @param roomID
     * @param playerScoreBoardDTO
     * @return
     */
    public void updatePlayerScore(int roomID, PlayerScoreBoardDTO playerScoreBoardDTO){
        Room findRoom = this.roomManager.findByRoomID(roomID);
        Game findGame = this.gameManager.findByRoomID(roomID);

        Player updatePlayer = findRoom.findPlayerByUserID(playerScoreBoardDTO.getUserID());

        if(playerScoreBoardDTO.getScoreBoard() != null && updatePlayer.isWriting() == false){
            int existingAccumulatedSystemScores = updatePlayer.getScoreBoard().getSystemScore();
            int existingAccumulatedVotedScores = updatePlayer.getScoreBoard().getVotedScore();
            updatePlayer.getScoreBoard().setSystemScore(existingAccumulatedSystemScores + playerScoreBoardDTO.getScoreBoard().getSystemScore());
            updatePlayer.getScoreBoard().setVotedScore(existingAccumulatedVotedScores + playerScoreBoardDTO.getScoreBoard().getVotedScore());
        } else {
            log.info("Room {}: Player {} has accumulated the score board this round.", roomID, playerScoreBoardDTO.getUserID());
        }

        // update the corresponded room
        findRoom.updatePlayer(updatePlayer);
        // refresh the game instance players, ranking score based on game players
        findGame.refreshPlayers(findRoom);

    }

    /**
     * Calculate the ranking returned to client, and save record to DB
     * @param roomID
     * @return
     */
    public LinkedHashMap<Integer, Player> calculateRanking(int roomID){
        LinkedHashMap<Integer, Player> playerRanking = new LinkedHashMap<>();
        List<Player> gamePlayers = findGamePlayersByRoomID(roomID);
        for(int i=0; i<gamePlayers.size(); i++){
            Player player = gamePlayers.get(i);
            int score = player.getScoreBoard().getWeightedScore();
            playerRanking.put(score, player);
        }

        gameRecordService.saveGameRecords(playerRanking);

        return playerRanking;
    }

    /**
     * When start the next game round, reset all players isReady= false, and reset player's score board
     *
     * @param roomID
     * @return
     */
    public void nextRound(int roomID){
        Room findRoom = this.roomManager.findByRoomID(roomID);
        Game findGame = this.gameManager.findByRoomID(roomID);

        List<Player> roomPlayers = findRoom.getPlayers();
        for(int i=0; i<roomPlayers.size(); i++){
            roomPlayers.get(i).setReady(false);
            roomPlayers.get(i).getScoreBoard().setVotedScore(0);
            roomPlayers.get(i).getScoreBoard().setSystemScore(0);
            findRoom.updatePlayer(roomPlayers.get(i));
        }
        log.info("Reset Room {} for next round: {}  ", roomID, findRoom);

        findGame.refreshPlayers(findRoom);
        log.info("Reset Game {} for next round: {}  ", roomID, findGame);
    }

    private void endGame(int roomID) {
        Room findRoom = this.roomManager.findByRoomID(roomID);
        Game findGame = this.gameManager.findByRoomID(roomID);
        this.roomManager.removeRoom(findRoom);
        this.gameManager.removeGame(findGame);
    }


}
