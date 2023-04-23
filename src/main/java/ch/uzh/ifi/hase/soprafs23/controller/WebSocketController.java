package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.QuestionPacker;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.CSVService;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class WebSocketController {
    Logger log = LoggerFactory.getLogger(WebSocketController.class);
    @Autowired
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private CSVService csvService;

    public WebSocketController(UserService userService, SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameService = new GameService();
    }

//    @SubscribeMapping({ "/greeting"})
//    //"/topic/greeting",
//    public String greet() throws Exception {
//        log.info("greeting subscribed");
//        return "Hello, client!";
//    }
//
//    @SubscribeMapping("/multi/create/{userId}")
//    //"/topic/greeting",
//    public String create() throws Exception {
//        log.info("creating subscribed");
//        return "Hello, owner!";
//    }

//    @SubscribeMapping("multi/rooms/{roomCode}/join")
//    //"/topic/greeting",
//    public String join() throws Exception {
//        log.info("joining subscribed");
//        return "Hello, player!";
//    }


    @MessageMapping("/multi/create/{userID}") //client.send("/app/muilti/create/userID",{}，JSON)
    //@SendTo("topic/multi/create/{userID}") //client.subscribe("topic/multi/create/{userID}")
    public void createRoom(@DestinationVariable int userID, GameParamDTO gameParam) throws Exception {
        log.info("request to create new Room: " + userID);
        // client is a registered user
        User gamer = userService.getUserById(userID);
        Player owner = gameService.createPlayer(gamer);
        Room newRoom = gameService.createRoom(owner, gameParam);
        log.info("new room created: " + newRoom.getRoomCode());
        this.simpMessagingTemplate.convertAndSend("/topic/multi/create/"+userID,newRoom);
        log.info("msg sent");
    }

    @MessageMapping("/multi/rooms/{roomID}/info") //client.send("/multi/rooms/"+roomID+"/info",{}，JSON)
    //@SendTo("topic/multi/rooms/{roomID}/info") //client.subscribe("topic/multi/rooms/"+roomID+"/info")
    public void getRoomByID(@DestinationVariable int roomID) throws Exception {
        log.info("request to get room info: " + roomID);
        Room foundRoom = this.gameService.findRoomByID(roomID);
        log.info("room found: " + foundRoom.getRoomID());
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+roomID+"/info",foundRoom);
        /**need to be corrected as user private channel*/
        log.info("msg sent");
    }

    @MessageMapping("/multi/rooms/{roomCode}/join")
    public void joinRoom(@DestinationVariable String roomCode, PlayerDTO playerDTO) throws Exception {
        log.info("request to join Room: " + roomCode);
        Room foundRoom = this.gameService.findRoomByCode(roomCode);
        // check if client is registered
        User gamer;
        if (this.userService.checkIfUserIDExists(playerDTO.getUserID())) {
            gamer = userService.getUserById(playerDTO.getUserID());
        } else {
            gamer = new User();
            gamer.setId(playerDTO.getUserID());
            gamer.setUsername(playerDTO.getUserName());
        }
        Player player = gameService.createPlayer(gamer);
        if(!foundRoom.checkIsFull()) {
            foundRoom.addPlayer(player);
        }
        log.info(player.getPlayerName()+" joined the room: " + foundRoom.getRoomID());
        log.info("room contains " + foundRoom.getPlayers().size() + " players.");
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+roomCode+"/join",foundRoom);
        /**need to be corrected as user private channel*/
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+foundRoom.getRoomID()+"/info",foundRoom);
    }

    @MessageMapping("/multi/rooms/{roomID}/drop")
    public void dropRoom(@DestinationVariable int roomID, PlayerDTO playerDTO) throws Exception {
        log.info("request to drop Room: " + roomID);
        Room foundRoom = this.gameService.findRoomByID(roomID);
        Player player = foundRoom.findPlayerByUserID(playerDTO.getUserID());
        foundRoom.removePlayer(player);
        log.info("dropped from the room: " + roomID);
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+foundRoom.getRoomID()+"/drop",foundRoom);
    }

    /**
     * Game start in two scenarios:
     * 1. Owner start the game, even not all players are ready;
     * 2. The server broadcast to clients when all players isReady=true;
     *
     * @throws Exception
     */
    @MessageMapping("/multi/games/{roomID}/start")
//  @SendTo("topic/multi/game/start")
    public void createGame(@DestinationVariable int roomID) throws Exception {
        log.info("request to create game: " + roomID);
        List<QuestionDTO> questionList = gameService.createGame(roomID,new QuestionPacker(csvService));
        log.info("new game created");
        this.simpMessagingTemplate.convertAndSend("/topic/multi/games/"+roomID+"/questions",questionList);
    }

    /**
     * It is used for update status when the player get ready: Set player isReady=true;
     *
     * @throws Exception
     */
    @MessageMapping("/multi/rooms/{roomID}/players/ready")
    public void updatePlayerReady(@DestinationVariable int roomID, PlayerStatusDTO playerStatusDTO) {
        log.info("Room {}: Player {} is changing the status.", roomID, playerStatusDTO.getUserID());
        Player updatedPlayer = this.gameService.updatePlayerStatusReady(roomID, playerStatusDTO);

        Room updatedRoom = this.gameService.findRoomByID(roomID);
        log.info("Updated room after player is ready: " + updatedRoom.getRoomID());

        boolean allReadyIndicator = this.gameService.checkALlReady(roomID);
        if(allReadyIndicator == true){
            log.info("Room {}: All players are ready!", roomID);
        }
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+roomID+"/info",updatedRoom);

    }

    /**
     * It is used for update status when players play the game
     * start the game: Set isWriting=true;
     * submit a question: set isWriting=false;
     * When all players finished, reset isWriting=true, next question
     *
     * @throws Exception
     */
    @MessageMapping("/multi/rooms/{roomID}/players/gaming")
    public void updatePlayerWriting(@DestinationVariable int roomID, PlayerStatusDTO playerStatusDTO) {
        log.info("Room {}: Player {} is changing the status.", roomID, playerStatusDTO.getUserID());
        Player updatedPlayer = this.gameService.updatePlayerStatusWriting(roomID, playerStatusDTO);

        Room updatedRoom = this.gameService.findRoomByID(roomID);
        log.info("Updated room after player is ready: " + updatedRoom.getRoomID());
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+roomID+"/info",updatedRoom);

        // When this question ends, reset the isWriting = ture
        boolean finishIndicator = this.gameService.checkALlFinish(roomID);
        if(finishIndicator == true){
            this.gameService.nextQuestion(roomID);

            log.info("Room {}: All players finish! " + roomID);
            this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+roomID+"/info",updatedRoom);
        }

    }

    /**
     * Accumulate player scoreBoard (includes system score and votedScore) after each question and share ranking
     * After 1 pack (10 questions) finished, save the game record of each player(user) into DB
     *
     *  playerDTO.userId are required for searching
     *
     * @return true/ false
     * @throws Exception
     */
    @MessageMapping("/multi/rooms/{roomID}/players/scoreBoard")
    public void updatePlayerScoreBoard(@DestinationVariable int roomID, PlayerScoreBoardDTO playerScoreBoardDTO) {
        log.info("Room {}: Player score {} is changing.", roomID, playerScoreBoardDTO.getScoreBoard().getSystemScore());
        this.gameService.updatePlayerScore(roomID, playerScoreBoardDTO);

        Room updatedRoom = this.gameService.findRoomByID(roomID);
        log.info("Updated room with player score {} : " + updatedRoom);
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+roomID+"/info",updatedRoom);

    }

    /**
     * Retrieve players score after each question and share ranking
     * playerDTO.userId is required for searching
     *
     * @return true/ false
     * @throws Exception
     */
    @MessageMapping("/multi/rooms/{roomID}/players/scores")
    public void getPlayerScoreBoard(@DestinationVariable int roomID) {
        log.info("Room {} is retrieving players score.", roomID);
        LinkedHashMap<Integer, Player> playerRank =  this.gameService.calculateRanking(roomID);
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+roomID+"/scores", playerRank);
    }

}
