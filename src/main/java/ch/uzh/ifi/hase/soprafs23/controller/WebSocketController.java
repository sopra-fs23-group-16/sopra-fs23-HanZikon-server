package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebSocketController {
    Logger log = LoggerFactory.getLogger(WebSocketController.class);
    @Autowired
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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
        foundRoom.addPlayer(player);
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
        this.simpMessagingTemplate.convertAndSend("/topic/multi/rooms/"+foundRoom.getRoomID()+"/info",foundRoom);
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
        List<QuestionDTO> questionList = gameService.createGame(roomID);
        log.info("new game created");
        this.simpMessagingTemplate.convertAndSend("/topic/multi/games/"+roomID+"/questions",questionList);
    }

}
