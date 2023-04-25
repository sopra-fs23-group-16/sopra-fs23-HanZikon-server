package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class GameManagerTest {
    @InjectMocks
    private GameManager gameManager;

    @InjectMocks
    private ConcurrentHashMap<Integer, Game> roomIDs;
    @MockBean
    private Room room;
    @MockBean
    private Game game;


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
        game = new Game(room);
        gameManager = new GameManager();
        roomIDs = new ConcurrentHashMap<>();
    }

    @Test
    public void findByRoomIDTest() {
        // given -> a first game has already been created
        gameManager.addGame(game);
        assertEquals(game, gameManager.findByRoomID(game.getRoomID()));
    }

    @Test
    public void removeGameTest() {
        gameManager.removeGame(game);
        assertThrows(ResponseStatusException.class, () -> gameManager.findByRoomID(2));

    }

}
