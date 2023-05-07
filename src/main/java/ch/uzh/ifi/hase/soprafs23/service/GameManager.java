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
        log.info("Into player votes!");
        boolean isUserPresent = false;
        if(playerVoteDTO.getVotedScore() >=0 ){
            log.info("Into player votes condition 1, playerVotes size is {} !", playerVotes.size());
            if(playerVotes.size() == 0 && playerVoteDTO.getUserID()!= null){
                log.info("player votes is null!");
                this.playerVotes.add(playerVoteDTO);
                log.info("Create a new player votes for player {} with voted time {} and voted score {}!", playerVoteDTO.getUserID(), playerVoteDTO.getVotedTimes(), playerVoteDTO.getVotedScore());
            } else if(this.playerVotes.size()>0 && playerVoteDTO.getUserID()!= null) {
                log.info("player votes is not null!");
                PlayerVoteDTO playerVoteFound;
                for(int i=0; i< this.playerVotes.size(); i++){
                    playerVoteFound = this.playerVotes.get(i);
                    log.info("player votes playerVotes.get({}) is userID {} voted scores {} voted times {} !", i, playerVoteFound.getUserID(), playerVoteFound.getVotedScore(),playerVoteFound.getRound());
                    if(playerVoteFound.getUserID().toString().equals(playerVoteDTO.getUserID().toString()) && Integer.toString(playerVoteDTO.getRound()).equals(Integer.toString(playerVoteFound.getRound())) ) {
                        log.info("player votes and round are matched!");
                        isUserPresent = true;
                        playerVoteFound.setVotedTimes(playerVoteFound.getVotedTimes() + playerVoteDTO.getVotedTimes());
                        playerVoteFound.setVotedScore(playerVoteFound.getVotedScore() + playerVoteDTO.getVotedScore());
                        log.info("Accumulate the player votes {} with voted time {} and voted score {}!", playerVoteDTO.getUserID(), playerVoteDTO.getVotedTimes(), playerVoteDTO.getVotedScore());
                    } else if (playerVoteFound.getUserID().toString().equals(playerVoteDTO.getUserID().toString()) &&  !Integer.toString(playerVoteDTO.getRound()).equals(Integer.toString(playerVoteFound.getRound()))){
                        log.info("player votes exist, player round are not matched!");
                        isUserPresent = true;
                        // next round, reset new votes
                        this.playerVotes = new ArrayList<>();
                        this.playerVotes.add(playerVoteDTO);
                        log.info("New round {} of player votes {} with voted time {} and voted score {}!", this.playerVotes.get(0).getRound(), this.playerVotes.get(0).getUserID(), this.playerVotes.get(0).getVotedTimes(), this.playerVotes.get(0).getVotedScore());
                    }
                }

                if(isUserPresent == false){
                    log.info("player is not matched!");
                    this.playerVotes.add(playerVoteDTO);
                    log.info("Add a new player votes for player {} with voted time {} and voted score {}!", playerVoteDTO.getUserID(), playerVoteDTO.getVotedTimes(), playerVoteDTO.getVotedScore());
                }

            }

        } else {
            log.info("There is no player votes yet!");
        }
        return this.playerVotes;

    }



}