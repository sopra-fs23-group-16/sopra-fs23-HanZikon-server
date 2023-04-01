package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class WebSocketController {
    private final GameService gameService;

    public WebSocketController(){
        this.gameService = new GameService();
    }

    @MessageMapping("multi/create") //client.send("/app/muilti/create",{}，JSON)
    @SendTo("topic/multi/player/{playerID}") //client.subscribe("topic/multi/player/{playerID}")
    public int createRoom(PlayerDTO ownerDTO, GameParamDTO gameParam) throws Exception {
        // client should retrieve username first
        Player owner = gameService.createPlayer(ownerDTO);
        int playerID = owner.getPlayerID();
        int roomID = gameService.createRoom(owner,gameParam).getRoomID();
        return roomID;
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
