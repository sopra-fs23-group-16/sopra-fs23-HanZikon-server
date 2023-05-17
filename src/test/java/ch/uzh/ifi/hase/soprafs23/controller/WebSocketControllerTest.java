package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.ScoreBoard;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.HanziDrawingDTO;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.*;
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
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
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
    @Test
    public void getRoomByID_Test() throws Exception {
        int roomID = 1;
        long userID = 1;
        String roomCode = "TEST01";
        // test owner
        User gamer = new User();
        gamer.setId(userID);
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        // test room
        Room room = new Room(roomCode,owner,gameParam);
        // given
        given(gameService.findRoomByID(Mockito.anyInt())).willReturn(room);
        // when
        webSocketController.getRoomByID(roomID);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        assertEquals("/topic/multi/rooms/"+roomID+"/info", destinationCaptor.getValue());
        Room sentRoom = roomCaptor.getValue();
        assertEquals("TEST01", sentRoom.getRoomCode());
        assertEquals(1, sentRoom.getOwner().getUserID());
        assertEquals("testUser", sentRoom.getOwner().getPlayerName());
        assertEquals(1, sentRoom.getGameParam().getLevel());
        assertEquals(2, sentRoom.getGameParam().getNumPlayers());
        assertEquals("MultipleChoice", sentRoom.getGameParam().getQuestionType());
    }

    @Test
    public void joinRoom_Test() throws Exception {
        int roomID = 1;
        long ownerID = 1;
        long playerID = 2;
        String roomCode = "TEST01";
        // test owner
        User gamer1 = new User();
        gamer1.setId(ownerID);
        gamer1.setUsername("testOwner");
        Player owner = new Player(gamer1);
        // test playerDTO
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setUserID(playerID);
        playerDTO.setUserName("testPlayer");
        // test player
        User gamer2 = new User();
        gamer2.setId(playerID);
        gamer2.setUsername("testPlayer");
        Player player = new Player(gamer2);
        // test players afterJoin
        LinkedHashMap<Long,Player> players = new LinkedHashMap<>();
        players.put(ownerID,owner);
        players.put(playerID,player);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        // test room
        Room room = new Room(roomCode,owner,gameParam);
        //given
        given(gameService.findRoomByCode(Mockito.anyString())).willReturn(room);
        given(userService.getUserById(Mockito.anyLong())).willReturn(gamer2);
        given(gameService.createPlayer(Mockito.any())).willReturn(player);
        // when
        webSocketController.joinRoom(roomCode,playerDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(2)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        assertEquals("/topic/multi/rooms/"+roomCode+"/join", destinationCaptor.getAllValues().get(0));
        Room sentRoom = roomCaptor.getAllValues().get(0);
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/info", destinationCaptor.getAllValues().get(1));
        assertEquals("TEST01", sentRoom.getRoomCode());
        assertEquals(players,sentRoom.getPlayersHashmap());
    }

    @Test
    void dropRoom_Test() throws Exception {
        int roomID = 1;
        long ownerID = 1;
        long playerID = 2;
        String roomCode = "TEST01";
        // test owner
        User gamer1 = new User();
        gamer1.setId(ownerID);
        gamer1.setUsername("testOwner");
        Player owner = new Player(gamer1);
        // test playerDTO
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setUserID(playerID);
        playerDTO.setUserName("testPlayer");
        // test player
        User gamer2 = new User();
        gamer2.setId(playerID);
        gamer2.setUsername("testPlayer");
        Player player = new Player(gamer2);
        // test players afterDrop
        LinkedHashMap<Long,Player> players = new LinkedHashMap<>();
        players.put(ownerID,owner);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        // test room
        Room room = new Room(roomCode,owner,gameParam);
        room.addPlayer(player);
        //given
        given(gameService.findRoomByID(Mockito.anyInt())).willReturn(room);
        given(userService.getUserById(Mockito.anyLong())).willReturn(gamer2);
        given(gameService.createPlayer(Mockito.any())).willReturn(player);
        // when
        webSocketController.dropRoom(roomID,playerDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        Room sentRoom = roomCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/drop", destinationCaptor.getValue());
        assertEquals("TEST01", sentRoom.getRoomCode());
        assertEquals(players,sentRoom.getPlayersHashmap());
    }

    @Test
    void createGame_Test () throws Exception {
        int roomID = 1;
        long userID = 1;
        String roomCode = "TEST01";
        // test owner
        User gamer = new User();
        gamer.setId(userID);
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        // test room
        Room room = new Room(roomCode,owner,gameParam);
        // test questionList
        List<QuestionDTO> questionList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            QuestionDTO question = new HanziDrawingDTO();
            questionList.add(question);
        }
        // given
        given(gameService.findRoomByID(Mockito.anyInt())).willReturn(room);
        given(gameService.createGame(Mockito.anyInt(),Mockito.any())).willReturn(questionList);
        // when
        webSocketController.createGame(roomID);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(),listCaptor.capture());
        assertEquals("/topic/multi/games/"+roomID+"/questions", destinationCaptor.getValue());
        assertEquals(10, listCaptor.getValue().size());
    }

    @Test
    void updatePlayerReady_Test() throws Exception {
        long ownerID = 1;
        long playerID = 2;
        String roomCode = "TEST01";
        // test owner
        User gamer1 = new User();
        gamer1.setId(ownerID);
        gamer1.setUsername("testOwner");
        Player owner = new Player(gamer1);
        // test player
        User gamer2 = new User();
        gamer2.setId(playerID);
        gamer2.setUsername("testPlayer");
        Player player = new Player(gamer2);
        player.setReady(true);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        // test room
        Room room = new Room(roomCode,owner,gameParam);
        room.addPlayer(player);
        // test PlayerStatusDTO
        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setReady(true);
        //given
        given(gameService.updatePlayerStatusReady(Mockito.anyInt(),Mockito.any())).willReturn(player);
        given(gameService.findRoomByID(Mockito.anyInt())).willReturn(room);
        // when
        webSocketController.updatePlayerReady(room.getRoomID(),playerStatusDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        Room sentRoom = roomCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/info", destinationCaptor.getValue());
        assertEquals("TEST01", sentRoom.getRoomCode());
        assertEquals(true,sentRoom.getPlayersHashmap().get(playerID).isReady());
    }

    @Test
    void updatePlayerWriting_not_finish() throws Exception {
        long ownerID = 1;
        long playerID = 2;
        String roomCode = "TEST01";
        // test owner
        User gamer1 = new User();
        gamer1.setId(ownerID);
        gamer1.setUsername("testOwner");
        Player owner = new Player(gamer1);
        // test player
        User gamer2 = new User();
        gamer2.setId(playerID);
        gamer2.setUsername("testPlayer");
        Player player = new Player(gamer2);
        player.setWriting(false);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        // test room
        Room room = new Room(roomCode,owner,gameParam);
        room.addPlayer(player);
        // test PlayerStatusDTO
        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setReady(false);
        //given
        given(gameService.updatePlayerStatusReady(Mockito.anyInt(),Mockito.any())).willReturn(player);
        given(gameService.findRoomByID(Mockito.anyInt())).willReturn(room);
        given(gameService.checkALlFinish(Mockito.anyInt())).willReturn(false);
        // when
        webSocketController.updatePlayerWriting(room.getRoomID(),playerStatusDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        Room sentRoom = roomCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/info", destinationCaptor.getValue());
        assertEquals("TEST01", sentRoom.getRoomCode());
        assertEquals(false,sentRoom.getPlayersHashmap().get(playerID).isWriting());
    }

    @Test
    void updatePlayerWriting_finish() throws Exception {
        long ownerID = 1;
        long playerID = 2;
        String roomCode = "TEST01";
        // test owner
        User gamer1 = new User();
        gamer1.setId(ownerID);
        gamer1.setUsername("testOwner");
        Player owner = new Player(gamer1);
        // test player
        User gamer2 = new User();
        gamer2.setId(playerID);
        gamer2.setUsername("testPlayer");
        Player player = new Player(gamer2);
        player.setWriting(false);
        // test gameParam
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        // test room
        Room room = new Room(roomCode,owner,gameParam);
        room.addPlayer(player);
        // test PlayerStatusDTO
        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setReady(false);
        //given
        given(gameService.updatePlayerStatusReady(Mockito.anyInt(),Mockito.any())).willReturn(player);
        given(gameService.findRoomByID(Mockito.anyInt())).willReturn(room);
        given(gameService.checkALlFinish(Mockito.anyInt())).willReturn(true);
        // when
        webSocketController.updatePlayerWriting(room.getRoomID(),playerStatusDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(2)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        Room sentRoom = roomCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/info", destinationCaptor.getValue());
        assertEquals("TEST01", sentRoom.getRoomCode());
        assertEquals(false,sentRoom.getPlayersHashmap().get(playerID).isWriting());
    }

    @Test
    void updatePlayerScoreBoard() {
        long userID = 1;
        // test room
        String roomCode = "TEST01";
        User gamer = new User();
        gamer.setId(userID);
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        Room room = new Room(roomCode,owner,gameParam);
        // test PlayerScoreBoardDTO
        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setSystemScore(10);
        PlayerScoreBoardDTO playerScoreBoardDTO = new PlayerScoreBoardDTO();
        playerScoreBoardDTO.setScoreBoard(scoreBoard);
        // given
        doNothing().when(gameService).updatePlayerScore(anyInt(), any(PlayerScoreBoardDTO.class));
        given(gameService.findRoomByID(Mockito.anyInt())).willReturn(room);
        // when
        webSocketController.updatePlayerScoreBoard(room.getRoomID(),playerScoreBoardDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), roomCaptor.capture());
        Room sentRoom = roomCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/info", destinationCaptor.getValue());
        assertEquals("TEST01", sentRoom.getRoomCode());
    }

    @Test
    void getPlayerScoreBoard() {
        // test room
        String roomCode = "TEST01";
        User gamer = new User();
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        GameParamDTO gameParam = new GameParamDTO(1,2,"MultipleChoice",10);
        Room room = new Room(roomCode,owner,gameParam);
        // playerScores
        LinkedHashMap<String, Integer> playerScores = new LinkedHashMap<>();
        playerScores.put("player1",10);
        playerScores.put("player2",20);

        // given
        given(gameService.calculateRanking(Mockito.anyInt())).willReturn(playerScores);
        // when
        webSocketController.getPlayerScoreBoard(room.getRoomID());
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), mapCaptor.capture());
        Map sentMap = mapCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/scores", destinationCaptor.getValue());
        assertEquals(2, sentMap.size());
    }

    @Test
    void updatePlayerImitation_withsuccess() {
        // test room
        String roomCode = "TEST01";
        User gamer = new User();
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        GameParamDTO gameParam = new GameParamDTO(1,2,"HanziDrawing",10);
        Room room = new Room(roomCode,owner,gameParam);

        Map<Long, String> playersImitations = new HashMap<>();
        playersImitations.put(gamer.getId(), "XXXBBBMM");

        PlayerImitationDTO playerImitationDTO = new PlayerImitationDTO();
        playerImitationDTO.setRound(1);
        playerImitationDTO.setImitationBytes("XXXBBBMM");
        playerImitationDTO.setUserID(gamer.getId());

        List<PlayerImitationDTO> playersImitationList = new ArrayList<>();
        playersImitationList.add(playerImitationDTO);

        // given
        given(gameService.getPlayersImitations(room.getRoomID(),playerImitationDTO)).willReturn(playersImitationList);
        // when
        webSocketController.updatePlayerImitation(room.getRoomID(), playerImitationDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> mapCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), mapCaptor.capture());
        List sentMap = mapCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/imitations", destinationCaptor.getValue());
        assertEquals(1, sentMap.size());
    }

    @Test
    void getPlayersImitations_withsuccess() {
        // test room
        String roomCode = "TEST01";
        User gamer = new User();
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        GameParamDTO gameParam = new GameParamDTO(1,2,"HanziDrawing",10);
        Room room = new Room(roomCode,owner,gameParam);

        Map<Long, String> playersImitations = new HashMap<>();
        playersImitations.put(gamer.getId(), "XXXBBBMM");
        playersImitations.put(2L, "XXXBBBWW");

        PlayerImitationDTO playerImitationDTO1 = new PlayerImitationDTO();
        playerImitationDTO1.setUserID(gamer.getId());
        playerImitationDTO1.setRound(1);
        playerImitationDTO1.setImitationBytes("XXXBBBMM");

        PlayerImitationDTO playerImitationDTO2 = new PlayerImitationDTO();
        playerImitationDTO2.setUserID(2L);
        playerImitationDTO2.setRound(1);
        playerImitationDTO2.setImitationBytes("XXXBBBWW");

        List<PlayerImitationDTO> playersImitationList = new ArrayList<>();
        playersImitationList.add(playerImitationDTO1);
        playersImitationList.add(playerImitationDTO2);

        // given
        given(gameService.getPlayersImitations(room.getRoomID(),playerImitationDTO1)).willReturn(playersImitationList);
        // when
        webSocketController.getPlayersImitations(room.getRoomID(),playerImitationDTO1);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> mapCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), mapCaptor.capture());
        List sentMap = mapCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/imitations", destinationCaptor.getValue());
        assertEquals(2, sentMap.size());
    }

    @Test
    void updatePlayerVotes_withsuccess() {
        // test room
        String roomCode = "TEST01";
        User gamer = new User();
        gamer.setId(1L);
        gamer.setUsername("testUser");
        Player owner = new Player(gamer);
        GameParamDTO gameParam = new GameParamDTO(1,2,"HanziDrawing",10);
        Room room = new Room(roomCode,owner,gameParam);

        User gamer2 = new User();
        gamer2.setId(2L);
        Player player2 = new Player(gamer2);
        gamer.setUsername("testUser2");
        room.addPlayer(player2);

        PlayerVoteDTO playerVoteDTO = new PlayerVoteDTO();
        playerVoteDTO.setRound(1);
        playerVoteDTO.setUserID(gamer.getId());
        playerVoteDTO.setFromUserID(gamer.getId());
        playerVoteDTO.setVotedTimes(1);

        PlayerVoteDTO playerVoteDTO2 = new PlayerVoteDTO();
        playerVoteDTO2.setRound(1);
        playerVoteDTO2.setUserID(gamer2.getId());
        playerVoteDTO2.setFromUserID(gamer2.getId());
        playerVoteDTO2.setVotedTimes(1);

        List<PlayerVoteDTO> playersVotesList = new ArrayList<>();
        playersVotesList.add(playerVoteDTO);
        playersVotesList.add(playerVoteDTO2);

        // given
        given(gameService.updatePlayerVotes(room.getRoomID(),playerVoteDTO)).willReturn(playersVotesList);
        // when
        webSocketController.updatePlayerVotes(room.getRoomID(), playerVoteDTO);
        // then
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> ListCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(simpMessagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), ListCaptor.capture());
        List sentList = ListCaptor.getValue();
        assertEquals("/topic/multi/rooms/"+room.getRoomID()+"/players/votes", destinationCaptor.getValue());
        assertEquals(2, sentList.size());
    }


    @AfterEach
    public void disconnect() throws Exception {
        stompSession.disconnect();
        stompClient.stop();
    }
}
