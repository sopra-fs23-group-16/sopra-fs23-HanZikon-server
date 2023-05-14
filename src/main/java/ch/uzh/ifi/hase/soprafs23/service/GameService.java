package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.ScoreBoard;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.QuestionPacker;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
@Transactional
public class GameService {

    private RoomManager roomManager;
    private GameManager gameManager;

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

        updateRoomPlayersReady(newGame);

        return questionList;

    }

    private String generateRoomCode(){
        String characters = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz023456789";
        int length = 4;  // 4-digit roomcode
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
     * Update all room players gaming status isWriting= ture, when room owner start game
     * @param game
     * @return
     */
    public void updateRoomPlayersReady(Game game){
        List<Player> gamePlayers = game.getPlayers();
        log.info("Game {}: Players status {} before gaming.", game.getRoomID(), gamePlayers.get(0).isWriting());
        for(int i=0; i<gamePlayers.size(); i++){
            Player player = gamePlayers.get(i);
            player.setWriting(true);
        }
        log.info("Game {}: Players status {} after start game.", game.getRoomID(), gamePlayers.get(0).isWriting());
    }

    /**
     * 1. Update the player status isReady= true get ready for game (each round)
     * @param roomID
     * @param playerDTO
     * @return
     */
    public Player updatePlayerStatusReady(int roomID, PlayerStatusDTO playerDTO){
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
    public Player updatePlayerStatusWriting(int roomID, PlayerStatusDTO playerDTO){
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

        if(playerScoreBoardDTO.getScoreBoard() != null){
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
     * Calculate the ranking returned to client
     * @param roomID
     * @return
     */
    public Map<String, Integer> calculateRanking(int roomID){
        LinkedHashMap<String, Integer> playerScores = new LinkedHashMap<>();
        List<Player> gamePlayers = findGamePlayersByRoomID(roomID);
        log.info("Room {} has {} players.", roomID, gamePlayers.size());

        int score = 0;
        for(int i=0; i<gamePlayers.size(); i++){
            Player player = gamePlayers.get(i);
            if(player.getScoreBoard() == null){
                score = 0;
            } else {
                log.info("Player {} 's score is {} .", gamePlayers.get(i).getPlayerName(), player.getScoreBoard().getWeightedScore());
                score = player.getScoreBoard().getWeightedScore();
            }

            playerScores.put(player.getPlayerName(), score);
        }

        Map<String, Integer> playerRanking = rankPlayers(playerScores);
        return playerRanking;
    }

    public Map<String, Integer> rankPlayers(HashMap<String, Integer> playerScores) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(playerScores.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int cmp = o2.getValue().compareTo(o1.getValue());
                return cmp != 0 ? cmp : o1.getKey().compareTo(o2.getKey());
            }
        });
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Save the player's imitation img stream in a Map <userId, imgBytes>
     * @param roomID
     * @param playerImitationDTO
     * @return
     */
    public List<PlayerImitationDTO> updatePlayerImitation(int roomID, PlayerImitationDTO playerImitationDTO) throws UnsupportedEncodingException {
        Map<Long, String> playerImitation = new HashMap<>();
        List<PlayerImitationDTO> playerImitationList = new ArrayList<>();
        Long userId = playerImitationDTO.getUserID();
        int round = playerImitationDTO.getRound();

        if(userId != null && round >= 0) {
            String userRoundID = round + "R" + userId;
            log.info("Room {}: user round id is {}.", roomID, userRoundID);

            // before the player save imitation bytes, it will clear the player related map record firstly
            this.gameManager.removePlayerImitation(userRoundID);

            if(playerImitationDTO.getImitationBytes() != null){

                this.gameManager.addPlayerImitation(playerImitationDTO);
                playerImitationList =  getPlayersImitations(roomID, playerImitationDTO);
                return playerImitationList;
            } else {
                log.info("Room {}: Player {} has not submitted the imitation record.", roomID, playerImitationDTO.getUserID());
                return null;
            }
        } else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player's imitations in room {} does not exist!" + roomID);
        }

    }

    /**
     * 1. Accumulate the player score according to scoreBoard passed
     * @param roomID
     * @return
     */
    public List<PlayerImitationDTO> getPlayersImitations(int roomID, PlayerImitationDTO playerImitationDTO){
        Map<Long, String> playersImitationsMap = new HashMap<>();
        List<PlayerImitationDTO> playersImitationsList = new ArrayList<>();
        List<Player> Players = findGamePlayersByRoomID(roomID);
        log.info("getPlayersImitationsT1: Room {} has {} players.", roomID, Players.size());

        PlayerImitationDTO playerImitation = new PlayerImitationDTO();
        Long playerID;
        int round = playerImitationDTO.getRound();

        if (Players.size()>0) {
            for(int i= 0; i< Players.size(); i++){
                playerID = Players.get(i).getUserID();
                if(playerID >0){
                    String userRoundID = round+ "R"+playerID;
                    playerImitation = this.gameManager.findImgByUserID(userRoundID);
                    playerImitation.setUsername(Players.get(i).getPlayerName());
                    playersImitationsMap.put(playerID,playerImitation.getImitationBytes());
                    playersImitationsList.add(playerImitation);
                }

            }
        }else {
            log.info("There is no player's imitations yet!");
        }

        return playersImitationsList;

    }

    /**
     * Accumulate the player votes according to PlayerVote passed
     * @param roomID
     * @return
     */
    public List<PlayerVoteDTO> updatePlayerVotes (int roomID, PlayerVoteDTO playerVotesDTO) {
        List<PlayerVoteDTO> playerVotesList = new ArrayList<>();

        Long userId = playerVotesDTO.getUserID();
        Long fromUserId = playerVotesDTO.getFromUserID();
        int round = playerVotesDTO.getRound();
        if(userId != null && fromUserId !=null && round >= 0) {
            String userVoteRoundID = roomID + "RO" + round + "R" + userId + "T" +fromUserId + "F";
            log.info("Room {}: user voted round id is {}.", roomID, userVoteRoundID);

            boolean isVoted = this.gameManager.checkPlayerVotes(userVoteRoundID);
            if(isVoted == true){
                log.info("Room {}: The voted score from player {} to player {} is already accumulated this round {}.", roomID,fromUserId, userId);
            } else {
                this.gameManager.addPlayerVotes(roomID,playerVotesDTO);
                playerVotesList = calculatePlayersVotes(roomID, playerVotesDTO);

                addPlayerVotedScore(roomID,playerVotesDTO);

                return playerVotesList;
            }
        } else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player's imitations in room {} does not exist!" + roomID);
        }

        return playerVotesList;
    }

    public void addPlayerVotedScore(int roomID, PlayerVoteDTO playerVotesDTO){
        PlayerScoreBoardDTO playerScoreBoardDTO = new PlayerScoreBoardDTO();
        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setVotedScore(10);
        playerScoreBoardDTO.setUserID(playerVotesDTO.getUserID());
        playerScoreBoardDTO.setScoreBoard(scoreBoard);
        log.info("Player votes: Add Player votedScore {} !" + playerScoreBoardDTO.getScoreBoard().getVotedScore());
        updatePlayerScore(roomID, playerScoreBoardDTO);
    }

    /**
     * Calculate the player votedScore in this round this room
     * @param roomID
     * @param playerVotesDTO
     * @return
     */
    public List<PlayerVoteDTO> calculatePlayersVotes(int roomID, PlayerVoteDTO playerVotesDTO){
        List<PlayerVoteDTO> playerVotesList = new ArrayList<>();
        List<Player> Players = findGamePlayersByRoomID(roomID);

        int round = playerVotesDTO.getRound();
        if (Players.size()>0 && round>0) {
            int votedTimes = 0;
            for(int i= 0; i< Players.size(); i++){
                PlayerVoteDTO playerVotes = new PlayerVoteDTO();
                Player toPlayer = Players.get(i);
                if(toPlayer != null) {
                    for(int j= 0; j< Players.size(); j++){
                        Player fromPlayer = Players.get(j);
                        String userRoundID = roomID + "RO"+ round+ "R"+toPlayer.getUserID() +"T"+ fromPlayer.getUserID()+ "F";
                        boolean isVoted = this.gameManager.checkPlayerVotes(userRoundID);
                        if(isVoted == true){
                            votedTimes ++;
                        }
                    }

                    playerVotes.setUserID(toPlayer.getUserID());
                    playerVotes.setUserName(toPlayer.getPlayerName());
                    playerVotes.setRound(round);
                    playerVotes.setVotedTimes(votedTimes);

                    log.info("Player votes: Player ID {} name {} round {} votedTimes {} !" , playerVotes.getUserID(),playerVotes.getUserName(), playerVotes.getRound());

                }
                playerVotesList.add(playerVotes);
            }

        }else {
            log.info("Player votes: There is no player's in this room!");
        }

        return playerVotesList;
    }


    /**
     * When this rounds end, reseting all players isReady= false, and reset player's score board
     *
     * @param roomID
     * @return
     */
    public void endRounds(int roomID){
        Room findRoom = this.roomManager.findByRoomID(roomID);

        List<Player> roomPlayers = findRoom.getPlayers();
        for(int i=0; i<roomPlayers.size(); i++){
            // roomPlayers.get(i).setReady(false);
            roomPlayers.get(i).getScoreBoard().setVotedScore(0);
            roomPlayers.get(i).getScoreBoard().setSystemScore(0);
        }
        log.info("Reset room {} for next round: {}  ", roomID, findRoom);
    }

    public void endGame(int roomID) {
        // Room findRoom = this.roomManager.findByRoomID(roomID);
        Game findGame = this.gameManager.findByRoomID(roomID);
        // this.roomManager.removeRoom(findRoom);
        this.gameManager.removeGame(findGame);
    }


}
