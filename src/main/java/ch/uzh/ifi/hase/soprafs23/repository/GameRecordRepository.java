package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.GameRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** LC: Spring Data JPA repositories are interfaces with methods supporting creating, reading, updating, and deleting records against a back end data store
 *  Spring Data synthesizes implementations based on conventions found in the naming of the methods in the interface
 *  Spring Data 根据接口中方法命名中的约定合成实现
 */
@Repository("gameRecordRepository")
public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {
  GameRecord findByUserID(Long userID);

}
