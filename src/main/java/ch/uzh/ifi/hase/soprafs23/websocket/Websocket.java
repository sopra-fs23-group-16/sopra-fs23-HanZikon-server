package ch.uzh.ifi.hase.soprafs23.websocket;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/websocket")
public class Websocket {

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
