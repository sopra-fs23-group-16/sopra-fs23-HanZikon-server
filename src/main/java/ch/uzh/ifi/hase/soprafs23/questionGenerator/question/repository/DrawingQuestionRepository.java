package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrawingQuestionRepository extends JpaRepository<HanziDrawing, Integer>{
    List<Question> findAllByLevel(String Level);
}