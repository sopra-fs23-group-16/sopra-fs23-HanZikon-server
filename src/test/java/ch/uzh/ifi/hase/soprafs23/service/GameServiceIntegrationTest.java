package ch.uzh.ifi.hase.soprafs23.service;

import antlr.collections.List;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.Room;
import ch.uzh.ifi.hase.soprafs23.MultipleMode.ScoreBoard;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.QuestionPacker;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.CSVService;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.ChoiceQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.DrawingQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerScoreBoardDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerStatusDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameServiceIntegrationTest {
    Player testPlayer;
    Player testPlayer2;
    Player testPlayer3;
    GameService gameService;

    Room testRoom;

    @Qualifier("choiceQuestionRepository")
    @Autowired
    ChoiceQuestionRepository choiceQuestionRepository;

    @Autowired
    @Qualifier("drawingQuestionRepository")
    DrawingQuestionRepository drawingQuestionRepository;

    @Autowired
    CSVService service;

    QuestionPacker questionPacker;

    @BeforeEach
    void initialize(){
        this.gameService = new GameService();
        this.testPlayer = new Player(new User());
        this.testRoom = new Room(null,this.testPlayer,null);
    }

    Player mockTestPlayer(String id,String name){
        User user = new User();
        user.setId(Long.parseLong(id));
        user.setUsername(name);

        return new Player(user);
    }

    @Test
    void createPlayer() {
        User user = new User();
        user.setId(Long.parseLong("1"));
        user.setUsername("testUser1");

        this.testPlayer = this.gameService.createPlayer(user);

        assertEquals(Long.parseLong("1"),this.testPlayer.getUserID());
        assertEquals("testUser1",this.testPlayer.getPlayerName());
        assertEquals(1,testPlayer.getUserID());
    }

    @Test
    void createRoom_and_successroomcode() {
        this.testPlayer = mockTestPlayer("1","testUser1");
        this.testRoom = this.gameService.createRoom(this.testPlayer, new GameParamDTO(1,2,"Mixed"));

        assertEquals(6,this.testRoom.getRoomCode().length());
        assertTrue(this.testRoom.getRoomCode() instanceof String);
        assertTrue(this.testRoom instanceof Room);
        assertEquals(Long.parseLong("1"),this.testRoom.getOwner().getUserID());
    }

    @Test
    void createGame() {
        this.testRoom = this.gameService.createRoom(this.testPlayer, new GameParamDTO(1,2,"Mixed"));
        assertTrue(this.gameService.createGame(this.testRoom.getRoomID(),new QuestionPacker(service)) instanceof java.util.List);
    }

    @Test
    void update_PlayerReadyStatus_withSuccess(){
        this.testPlayer = mockTestPlayer("1","testOwner");
        this.testRoom = this.gameService.createRoom(this.testPlayer,
                new GameParamDTO(1,3,"Mixed"));
        this.testRoom.addPlayer(mockTestPlayer("2","player1"));
        this.testRoom.addPlayer(mockTestPlayer("3","player2"));

        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setUserID(Long.parseLong("2"));
        playerStatusDTO.setReady(true);
        playerStatusDTO.setWriting(false);

        //there are players not ready
        assertEquals(false,this.gameService.checkALlReady(this.testRoom.getRoomID()));

        assertEquals(false,this.testRoom.findPlayerByUserID(Long.parseLong("2")).isReady());
        this.gameService.updatePlayerStatusReady(this.testRoom.getRoomID(),playerStatusDTO);
        assertEquals(true,this.testRoom.findPlayerByUserID(Long.parseLong("2")).isReady());

        playerStatusDTO.setUserID(Long.parseLong("3"));
        this.gameService.updatePlayerStatusReady(this.testRoom.getRoomID(),playerStatusDTO);

        assertEquals(true,this.gameService.checkALlReady(this.testRoom.getRoomID()));
    }

    @Test
    void update_PlayerWritingStatus_withSuccess() {
        this.testPlayer = mockTestPlayer("1","testOwner");
        this.testRoom = this.gameService.createRoom(this.testPlayer,
                new GameParamDTO(1,3,"Mixed"));

        this.testRoom.addPlayer(mockTestPlayer("2","player1"));
        this.testRoom.addPlayer(mockTestPlayer("3","player2"));

        this.gameService.createGame(this.testRoom.getRoomID(),new QuestionPacker(service));

        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setUserID(Long.parseLong("2"));
        playerStatusDTO.setReady(true);
        playerStatusDTO.setWriting(false);

        assertEquals(false,this.gameService.checkALlFinish(this.testRoom.getRoomID()));

        this.gameService.updatePlayerStatusWriting(this.testRoom.getRoomID(),playerStatusDTO);
        assertEquals(false,this.testRoom.findPlayerByUserID(Long.parseLong("2")).isWriting());

        playerStatusDTO.setWriting(false);
        this.gameService.updatePlayerStatusWriting(this.testRoom.getRoomID(),playerStatusDTO);
        assertEquals(false,this.testRoom.findPlayerByUserID(Long.parseLong("2")).isWriting());

        playerStatusDTO.setUserID(Long.parseLong("1"));
        this.gameService.updatePlayerStatusWriting(this.testRoom.getRoomID(),playerStatusDTO);
        playerStatusDTO.setUserID(Long.parseLong("3"));
        this.gameService.updatePlayerStatusWriting(this.testRoom.getRoomID(),playerStatusDTO);

        assertEquals(true,this.gameService.checkALlFinish(this.testRoom.getRoomID()));

        this.gameService.nextQuestion(this.testRoom.getRoomID());
        assertEquals(false,this.gameService.checkALlFinish(this.testRoom.getRoomID()));
    }

    @Test
    void update_PlayerScore_withSuccess() {
        this.testPlayer = mockTestPlayer("1","testOwner");
        this.testRoom = this.gameService.createRoom(this.testPlayer,
                new GameParamDTO(1,3,"Mixed"));

        this.testRoom.addPlayer(mockTestPlayer("2","player1"));
        this.testRoom.addPlayer(mockTestPlayer("3","player2"));

        this.gameService.createGame(this.testRoom.getRoomID(),new QuestionPacker(service));

        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setUserID(Long.parseLong("2"));
        playerStatusDTO.setReady(true);
        playerStatusDTO.setWriting(false);

        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setSystemScore(10);
        scoreBoard.setVotedScore(20);
        PlayerScoreBoardDTO playerScoreBoardDTO = new PlayerScoreBoardDTO();
        playerScoreBoardDTO.setScoreBoard(scoreBoard);
        playerScoreBoardDTO.setUserID(this.testPlayer.getUserID());

        this.gameService.updatePlayerScore(this.testRoom.getRoomID(), playerScoreBoardDTO);

        assertEquals(10,this.gameService.findRoomByID(this.testRoom.getRoomID()).findPlayerByUserID(this.testPlayer.getUserID()).getScoreBoard().getSystemScore());
        assertEquals(20,this.gameService.findRoomByID(this.testRoom.getRoomID()).findPlayerByUserID(this.testPlayer.getUserID()).getScoreBoard().getVotedScore());
        assertEquals(15,this.gameService.findRoomByID(this.testRoom.getRoomID()).findPlayerByUserID(this.testPlayer.getUserID()).getScoreBoard().getWeightedScore());
    }

    @Test
    void calculateRanking_withSuccess() {
        this.testPlayer = mockTestPlayer("1","testOwner");
        this.testRoom = this.gameService.createRoom(this.testPlayer,
                new GameParamDTO(1,3,"Mixed"));

        this.testPlayer2 = mockTestPlayer("2","player2");
        this.testPlayer3 = mockTestPlayer("3","player3");

        this.testRoom.addPlayer(testPlayer2);
        this.testRoom.addPlayer(testPlayer3);

        this.gameService.createGame(this.testRoom.getRoomID(),new QuestionPacker(service));

        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setUserID(Long.parseLong("2"));
        playerStatusDTO.setReady(true);
        playerStatusDTO.setWriting(false);

        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setSystemScore(20);
        scoreBoard.setVotedScore(10);
        PlayerScoreBoardDTO playerScoreBoardDTO = new PlayerScoreBoardDTO();
        playerScoreBoardDTO.setScoreBoard(scoreBoard);
        playerScoreBoardDTO.setUserID(this.testPlayer.getUserID());

        this.gameService.updatePlayerScore(this.testRoom.getRoomID(), playerScoreBoardDTO);

        playerScoreBoardDTO.setUserID(this.testPlayer2.getUserID());
        this.gameService.updatePlayerScore(this.testRoom.getRoomID(), playerScoreBoardDTO);

        scoreBoard.setSystemScore(10);
        scoreBoard.setVotedScore(30);
        playerScoreBoardDTO.setScoreBoard(scoreBoard);
        playerScoreBoardDTO.setUserID(this.testPlayer3.getUserID());
        this.gameService.updatePlayerScore(this.testRoom.getRoomID(), playerScoreBoardDTO);

        assertEquals(testPlayer3.getPlayerName(),this.gameService.calculateRanking(this.testRoom.getRoomID()).entrySet().iterator().next().getKey());
        assertEquals(20,this.gameService.calculateRanking(this.testRoom.getRoomID()).entrySet().iterator().next().getValue());
    }

    @Test
    void endRounds_withSuccess() {
        this.testPlayer = mockTestPlayer("1","testOwner");
        this.testRoom = this.gameService.createRoom(this.testPlayer,
                new GameParamDTO(1,3,"Mixed"));

        this.testRoom.addPlayer(mockTestPlayer("2","player1"));
        this.testRoom.addPlayer(mockTestPlayer("3","player2"));

        this.gameService.createGame(this.testRoom.getRoomID(),new QuestionPacker(service));

        PlayerStatusDTO playerStatusDTO = new PlayerStatusDTO();
        playerStatusDTO.setUserID(Long.parseLong("2"));
        playerStatusDTO.setReady(true);
        playerStatusDTO.setWriting(false);

        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoard.setSystemScore(10);
        PlayerScoreBoardDTO playerScoreBoardDTO = new PlayerScoreBoardDTO();
        playerScoreBoardDTO.setScoreBoard(scoreBoard);
        playerScoreBoardDTO.setUserID(this.testPlayer.getUserID());

        this.gameService.updatePlayerScore(this.testRoom.getRoomID(), playerScoreBoardDTO);
        this.gameService.endRounds(this.testRoom.getRoomID());

        assertEquals(0,this.gameService.findRoomByID(this.testRoom.getRoomID()).findPlayerByUserID(this.testPlayer.getUserID()).getScoreBoard().getSystemScore());
    }

}