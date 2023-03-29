package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;

public class GameService {
    public Player createPlayer(PlayerDTO playerDTO){
        return new Player(playerDTO);
    }
    public Room createRoom(Player owner,GameParamDTO gameParam){
        return new Room(owner,gameParam);
    }

}
