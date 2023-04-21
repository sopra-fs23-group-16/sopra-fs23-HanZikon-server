package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.MultipleMode.Player;
import ch.uzh.ifi.hase.soprafs23.entity.GameRecord;
import ch.uzh.ifi.hase.soprafs23.repository.GameRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Game Record Service
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameRecordService {

    private final Logger log = LoggerFactory.getLogger(GameRecordService.class);
    private final GameRecordRepository gameRecordRepository;

    @Autowired
    public GameRecordService(@Qualifier("gameRecordRepository") GameRecordRepository gameRecordRepository) {
        this.gameRecordRepository = gameRecordRepository;
    }

    public List<GameRecord> getGameRecords() {
        return this.gameRecordRepository.findAll();
    }

    public List<GameRecord> saveGameRecords(LinkedHashMap<Integer, Player> playerRanking) {
        List<GameRecord> gameRecordList = null;

        GameRecord gameRecord = new GameRecord();

        for (Map.Entry<Integer, Player> entry : playerRanking.entrySet()) {
            int score = entry.getKey();
            Player player = entry.getValue();
            log.info(player.getPlayerName() + ": " + score);

            gameRecord.setScore(score);
            gameRecord.setUserID(player.getUserID());
            gameRecord.setGameLevel(1);

            // saves the given entity but data is only persisted in the database once
            // flush() is called
            gameRecord = gameRecordRepository.save(gameRecord);
            gameRecordRepository.flush();
        }

        gameRecordList.add(gameRecord);

        log.debug("Created Information for Game record: {}", gameRecord);
        return gameRecordList;
    }
}
