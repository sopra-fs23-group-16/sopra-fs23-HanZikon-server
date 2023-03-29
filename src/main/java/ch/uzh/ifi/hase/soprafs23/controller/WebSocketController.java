package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebSocketController {
    private final GameService gameService;

    public WebSocketController(){
        this.gameService = new GameService();
    }

    @MessageMapping("/game/create")
    @ResponseBody
    public int createRoom(PlayerDTO ownerDTO, GameParamDTO gameParam) throws Exception {
        // client should retrieve username first
        Player owner = gameService.createPlayer(ownerDTO);
        int playerID = owner.getPlayerID();
        int roomID = gameService.createRoom(owner,gameParam).getRoomID();
        return roomID;
    }

}
