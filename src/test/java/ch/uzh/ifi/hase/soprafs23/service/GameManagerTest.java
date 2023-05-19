package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerImitationDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerVoteDTO;
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
    @InjectMocks
    private ConcurrentHashMap<String, PlayerImitationDTO> gameImitationsMap;
    @InjectMocks
    private ConcurrentHashMap<String, PlayerVoteDTO> gameVotesMap;
    @MockBean
    private Room room;
    @MockBean
    private Game game;
    @MockBean
    private PlayerImitationDTO playerImitationDTO;
    @MockBean
    private PlayerVoteDTO playerVoteDTO;


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

        GameParamDTO gameParam = new GameParamDTO(1, 3,"Mixed",10);

        int roomIdTest = 2;
        String roomCodeTest = "TEST01";

        room = new Room(roomCodeTest, testPlayer1, gameParam);
        game = new Game(room);
        gameManager = new GameManager();
        roomIDs = new ConcurrentHashMap<>();
        playerImitationDTO = new PlayerImitationDTO();
        playerImitationDTO.setRound(1);
        playerImitationDTO.setUserID(204L);
        playerImitationDTO.setImitationBytes("XXXBBC");
        gameImitationsMap = new ConcurrentHashMap<>();

        playerVoteDTO = new PlayerVoteDTO();
        playerVoteDTO.setVotedTimes(1);
        playerVoteDTO.setFromUserID(203L);
        playerVoteDTO.setUserID(203L);
        gameVotesMap = new ConcurrentHashMap<>();

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

    @Test
    public void addPlayerImitationTest() {
        // given -> a first game has already been created
        gameManager.addPlayerImitation(1,playerImitationDTO);
        assertEquals(playerImitationDTO, gameManager.findImgByUserID("1RO1R204"));
    }

    @Test
    public void removePlayerImitationTest() {
        gameManager.removePlayerImitation("1R204");
        assertThrows(ResponseStatusException.class, () -> gameManager.findImgByUserID("1R204"));

    }

    @Test
    public void addPlayerVotesTest() {
        // given -> a first game has already been created
        gameManager.addPlayerVotes(1,playerVoteDTO);
        assertEquals(true, gameManager.checkPlayerVotes("1RO0R203T203F"));
    }

    @Test
    public void checkPlayerVotes() {
        gameManager.checkPlayerVotes("1R204");
        assertEquals(false, gameManager.checkPlayerVotes("1R204"));

    }

}
