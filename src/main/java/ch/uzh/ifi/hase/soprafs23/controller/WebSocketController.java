package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class WebSocketController {
    @Autowired
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketController(UserService userService){
        this.userService = userService;
        this.gameService = new GameService();
    }

    @MessageMapping("multi/create/{userID}") //client.send("/app/muilti/create/userID",{}，JSON)
    @SendTo("topic/multi/create/{userID}") //client.subscribe("topic/multi/create/{userID}")
    public String createRoom(@PathVariable int userID, @RequestBody GameParamDTO gameParam) throws Exception {
        // client is a registered user
        User gamer = userService.getUserById(userID);
        Player owner = gameService.createPlayer(gamer);
        Room newRoom = gameService.createRoom(owner,gameParam);
        return newRoom.getRoomCode();
    }

    /**
     * Game start in two scenarios:
     * 1. Owner start the game, even not all players are ready;
     * 2. The server broadcast to clients when all players isReady=true;
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
