package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.MultipleChoice;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoiceQuestionRepository extends JpaRepository<MultipleChoice, Integer>{
    List<Question> findAllByLevel(String Level);

}