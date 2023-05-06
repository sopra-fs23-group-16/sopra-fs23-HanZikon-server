package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Game;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.PlayerVoteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private ConcurrentHashMap<Integer, Game> roomIDs;
    private Map<Long, String> playerImitation = new HashMap<>();
    private ConcurrentHashMap<Integer, Map<Long, String>> gameImitations = new ConcurrentHashMap<>();
    List<PlayerVoteDTO> playerVotes = new ArrayList<>();

    Logger log = LoggerFactory.getLogger(GameManager.class);

    public GameManager() {
        this.roomIDs = new ConcurrentHashMap<>();
    }

    public void addGame(Game game) {
        roomIDs.put(game.getRoomID(), game);
    }
    public void removeGame(Game game) {
        roomIDs.remove(game.getRoomID());
    }

    public Game findByRoomID(int roomID) {
        Game game = roomIDs.get(roomID);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This room does not exist!");
        }
        return game;
    }

    public void addPlayerImitation(int roomID, Long userId, String imitationBytes) {
        playerImitation.put(userId, imitationBytes);
        gameImitations.put(roomID, playerImitation);
        log.info("Add the player imitation in game manager {}", playerImitation.get(userId));
    }

    public void removePlayerImitation(int roomID, Long userId) {
        Map<Long, String> playersImitations = getPlayerImitations(roomID);
        if((playersImitations != null) && (playersImitations.get(userId) != null)){
            playersImitations.remove(playersImitations.get(userId));
        }
    }

    public Map<Long, String>  getPlayerImitations(int roomID) {
        Map<Long, String> playersImitations = gameImitations.get(roomID);
        if (playersImitations == null) {
            log.info("There is no player's imitations yet!");
        }
        return playersImitations;
    }

    public List<PlayerVoteDTO> calculatePlayerVotes(PlayerVoteDTO playerVoteDTO) {
        PlayerVoteDTO playerVoteNew;
        if(playerVoteDTO.getVotedScore() >=0 && playerVoteDTO.getVotedTimes()>=0 ){
            if(playerVotes == null && playerVoteDTO.getUserID()!= null){
                playerVoteNew = addPlayerVote(playerVoteDTO);
                playerVotes.add(playerVoteNew);
                log.info("Create a new player vote for player {} with voted time {} and voted score {}!", playerVoteDTO.getUserID(), playerVoteDTO.getVotedTimes(), playerVoteDTO.getVotedScore());
            } else if(playerVotes != null && playerVoteDTO.getUserID()!= null) {
                PlayerVoteDTO playerVoteFound;
                for(int i=0; i< playerVotes.size(); i++){
                    playerVoteFound = playerVotes.get(i);
                    if(playerVoteDTO.getUserID() == playerVoteFound.getUserID() ) {
                        if(playerVoteDTO.getRound() == playerVoteFound.getRound()){
                            playerVoteFound.setVotedTimes(playerVoteFound.getVotedTimes() + playerVoteDTO.getVotedTimes());
                            playerVoteFound.setVotedScore(playerVoteFound.getVotedScore() + playerVoteDTO.getVotedScore());
                            log.info("Accumulate the player {} votes with voted time {} and voted score {}!", playerVoteDTO.getUserID(), playerVoteDTO.getVotedTimes(), playerVoteDTO.getVotedScore());
                            return playerVotes;
                        } else {
                            // new round, reset new votes
                            playerVotes = new ArrayList<>();
                            playerVoteNew = addPlayerVote(playerVoteDTO);
                            playerVotes.add(playerVoteNew);
                        }

                    }
                }

                playerVoteNew = addPlayerVote(playerVoteDTO);
                playerVotes.add(playerVoteNew);
                log.info("Add a new player vote for player {} with voted time {} and voted score {}!", playerVoteDTO.getUserID(), playerVoteDTO.getVotedTimes(), playerVoteDTO.getVotedScore());

            }

        } else {
            log.info("There is no player's votes yet!");
        }
        return playerVotes;

    }

    public PlayerVoteDTO addPlayerVote(PlayerVoteDTO playerVoteDTO) {
        PlayerVoteDTO playerVote = new PlayerVoteDTO();
        if(playerVoteDTO != null){
            playerVote.setVotedScore(playerVoteDTO.getVotedScore());
            playerVote.setVotedTimes(playerVoteDTO.getVotedTimes());
            playerVote.setRound(playerVoteDTO.getRound());
            playerVote.setUserID(playerVoteDTO.getUserID());
        }

        return playerVote;

    }

}