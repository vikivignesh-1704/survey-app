package com.survey.repository;

import com.survey.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);

    @Query("SELECT DISTINCT a.responseId FROM Answer a WHERE a.question.survey.id = :surveyId")
    List<String> findDistinctResponseIdsBySurveyId(Long surveyId);

    @Query("SELECT a.value, COUNT(a) FROM Answer a WHERE a.question.id = :questionId GROUP BY a.value")
    List<Object[]> countAnswersByQuestionId(Long questionId);
}
