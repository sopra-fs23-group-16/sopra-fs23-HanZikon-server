package ch.uzh.ifi.hase.soprafs23.websocket;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*")
@ServerEndpoint("/websocket")
@Component
public class WsocketController {

    // an ID:Room map shared by all instances
    private static ConcurrentHashMap<Integer, Room> roomPool = new ConcurrentHashMap<>();

    // as unique player or shared map?
    // map: can get the player and vote for her quickly
    private static ConcurrentHashMap<Integer, Player> playerPool = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        session.getBasicRemote().sendText("Connected to Websocket");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
    }

    @OnClose
    public void onClose(Session session) {
    }

}
