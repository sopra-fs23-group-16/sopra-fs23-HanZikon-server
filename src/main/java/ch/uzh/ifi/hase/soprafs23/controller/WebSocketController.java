package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

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

    @SubscribeMapping({ "/greeting"})
    //"/topic/greeting",
    public String greet() throws Exception {
        log.info("greeting subscribed");
        return "Hello, client!";
    }

    @SubscribeMapping("/multi/create/{userId}")
    //"/topic/greeting",
    public String create() throws Exception {
        log.info("creating subscribed");
        return "Hello, owner!";
    }

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

    @MessageMapping("/multi/rooms/{roomID}/join")
    public void joinRoom(@DestinationVariable int roomID, PlayerDTO playerDTO) throws Exception {
        log.info("request to join Room: " + roomID);
        // check if client is registered
        // check if userID exists!!
        User gamer;
        if (1 == 1) {
            gamer = userService.getUserById(playerDTO.getUserID());
        }
        else {
            gamer = new User();
            gamer.setId(playerDTO.getUserID());
            gamer.setUsername(playerDTO.getUserName());
        }
        Player player = gameService.createPlayer(gamer);
        log.info("joined to the room: " + roomID);
    }

    @MessageMapping("multi/rooms/{roomID}/drop")
    public void dropRoom(@DestinationVariable int roomID, PlayerDTO playerDTO) throws Exception {
        log.info("request to drop Room: " + roomID);
        // find the player by userID

    }

    /**
     * Game start in two scenarios:
     * 1. Owner start the game, even not all players are ready;
     * 2. The server broadcast to clients when all players isReady=true;
     *
     * @param gameDTO
     * @return true/ false
     * @throws Exception
     */
    @MessageMapping("multi/create/game")
    @SendTo("topic/multi/game/start")
    public Game createGame(GameDTO gameDTO) throws Exception {
        Game game = gameService.createGame(gameDTO.getPlayers(), gameDTO.getRoomID());

        return game;
    }

}
