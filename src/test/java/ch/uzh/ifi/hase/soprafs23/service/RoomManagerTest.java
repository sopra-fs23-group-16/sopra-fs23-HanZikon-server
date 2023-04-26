package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RoomManagerTest {
    @InjectMocks
    private RoomManager roomManager;

    @InjectMocks
    private ConcurrentHashMap<Integer, Game> roomIDs;

    @InjectMocks
    private ConcurrentHashMap<String, Game> roomCodes;
    @MockBean
    private Room room;


    @BeforeEach
    void setup() {
        // given
        User user = new User();
        user.setId(2L);
        Player testPlayer1 = new Player(user);
        testPlayer1.setUserID(user.getId());
        testPlayer1.setWriting(true);
        List<Player> players = new ArrayList<>();
        players.add(testPlayer1);

        GameParamDTO gameParam = new GameParamDTO(1, 3,"Mixed");

        int roomIdTest = 2;
        String roomCodeTest = "TEST01";

        room = new Room(roomCodeTest, testPlayer1, gameParam);
        roomIDs = new ConcurrentHashMap<>();
        roomCodes = new ConcurrentHashMap<>();
        roomManager = new RoomManager();
    }

    @Test
    public void findByRoomIDTest() {
        // given -> a first game has already been created
        roomManager.addRoom(room);
        assertEquals(room, roomManager.findByRoomID(room.getRoomID()));
    }

    @Test
    public void removeGameTest() {
        roomManager.removeRoom(room);
        assertThrows(ResponseStatusException.class, () -> roomManager.findByRoomID(2));
    }

}
