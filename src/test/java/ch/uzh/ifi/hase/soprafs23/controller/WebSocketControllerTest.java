package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.websocket.PrincipalHandshake;
import ch.uzh.ifi.hase.soprafs23.websocket.WebSocketConfig;
import ch.uzh.ifi.hase.soprafs23.websocket.WebSocketHandshakeInterceptor;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WebSocketControllerTest {
    @Autowired
    private WebSocketController webSocketController;
    @MockBean
    private GameService gameService;
    @MockBean
    private UserService userService;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @LocalServerPort
    private int port;

    public WebSocketStompClient createStompClient() {
        // client with SockJS
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private CompletableFuture<String> resultKeeper;

    @BeforeEach
    public void initialize() throws Exception {
        // connect to websocket
        String wsUrl = "http://localhost:" + port + "/websocket";
        this.stompClient = createStompClient();
        this.stompSession = stompClient.connect(wsUrl, new WSTestHandlers.MyStompSessionHandler()).get();
    }

    @Test
    public void connectToSocket_Test() throws Exception {
        assertEquals(true,stompSession.isConnected());
    }

    @Test
    public void createRoom_Test() throws Exception {
        long userID = 1;
//1.        stompSession.subscribe(
//                "/topic/multi/create/" + userID,
//                new WSTestHandlers.FrameHandlerCreateRoom((payload) -> resultKeeper.complete(payload)));
//        Thread.sleep(180000);
        // test roomCode
        String roomCode = "TEST01";
        // test owner
        User gamer = new User();
        gamer.setId(userID);
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice");
        // test room
        Room room = new Room(roomCode,owner,gameParam);

        // given
        given(userService.getUserById(Mockito.anyLong())).willReturn(gamer);
        given(gameService.createPlayer(Mockito.any())).willReturn(owner);
        given(gameService.createRoom(Mockito.any(),Mockito.any())).willReturn(room);
        // when
        webSocketController.createRoom((int) userID, gameParam);
//2.        stompSession.send("/app/multi/create/" + userID, gameParam);
//3.        String destination = "/app/multi/create/"+userID;
//        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
//        headers.setDestination(destination);
//        headers.setSessionId(stompSession.getSessionId());
//        String payload = new ObjectMapper().writeValueAsString(gameParam);
//        simpMessagingTemplate.convertAndSend(String.valueOf(headers.getMessageHeaders()), payload);

        // then
        // Verify that convertAndSend is called with the correct destination
        // Verify that convertAndSend is called with the expected room instance
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        assertEquals("/topic/multi/create/" + userID, destinationCaptor.getValue());
        Room sentRoom = roomCaptor.getValue();
        assertEquals("TEST01", sentRoom.getRoomCode());
        assertEquals(1, sentRoom.getOwner().getUserID());
        assertEquals("testUser", sentRoom.getOwner().getPlayerName());
        assertEquals(1, sentRoom.getGameParam().getLevel());
        assertEquals(2, sentRoom.getGameParam().getNumPlayers());
        assertEquals("MultipleChoice", sentRoom.getGameParam().getQuestionType());
//4.        assertEquals(room.toString(),resultKeeper.get(60, SECONDS));
    }

    public void getRoomByID_Test() throws Exception {

    }

    @AfterEach
    public void disconnect() throws Exception {
        stompSession.disconnect();
        stompClient.stop();
    }

}
