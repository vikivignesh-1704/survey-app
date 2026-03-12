package com.survey.service;

import com.survey.model.Answer;
import com.survey.model.Question;
import com.survey.model.Survey;
import com.survey.repository.AnswerRepository;
import com.survey.repository.QuestionRepository;
import com.survey.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    // ---- Survey CRUD ----

    public List<Survey> getAllSurveys() {
        return surveyRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepository.findById(id);
    }

    public Survey createSurvey(Survey survey) {
        if (survey.getQuestions() != null) {
            for (int i = 0; i < survey.getQuestions().size(); i++) {
                Question q = survey.getQuestions().get(i);
                q.setSurvey(survey);
                q.setOrderIndex(i);
            }
        }
        return surveyRepository.save(survey);
    }

    public Survey updateSurvey(Long id, Survey updated) {
        Survey existing = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found: " + id));
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setActive(updated.isActive());
        return surveyRepository.save(existing);
    }

    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }

    // ---- Response Submission ----

    public void submitResponse(Long surveyId, Map<Long, String> answers) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found: " + surveyId));

        String responseId = UUID.randomUUID().toString();

        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Question question = questionRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + entry.getKey()));

            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setResponseId(responseId);
            answer.setValue(entry.getValue());
            answerRepository.save(answer);
        }

        survey.setResponseCount(survey.getResponseCount() + 1);
        surveyRepository.save(survey);
    }

    // ---- Results / Analytics ----

    public Map<String, Object> getSurveyResults(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found: " + surveyId));

        Map<String, Object> results = new LinkedHashMap<>();
        results.put("surveyId", survey.getId());
        results.put("title", survey.getTitle());
        results.put("description", survey.getDescription());
        results.put("totalResponses", survey.getResponseCount());
        results.put("createdAt", survey.getCreatedAt());

        List<Map<String, Object>> questionResults = new ArrayList<>();

        for (Question question : survey.getQuestions()) {
            Map<String, Object> qResult = new LinkedHashMap<>();
            qResult.put("questionId", question.getId());
            qResult.put("questionText", question.getText());
            qResult.put("questionType", question.getType());
            qResult.put("options", question.getOptions());

            List<Object[]> counts = answerRepository.countAnswersByQuestionId(question.getId());
            Map<String, Long> distribution = new LinkedHashMap<>();
            long totalAnswers = 0;
            for (Object[] row : counts) {
                String value = (String) row[0];
                Long count = (Long) row[1];
                distribution.put(value, count);
                totalAnswers += count;
            }
            qResult.put("distribution", distribution);
            qResult.put("totalAnswers", totalAnswers);

            // Calculate average for RATING type
            if (question.getType() == Question.QuestionType.RATING) {
                double avg = distribution.entrySet().stream()
                        .mapToDouble(e -> {
                            try { return Double.parseDouble(e.getKey()) * e.getValue(); }
                            catch (NumberFormatException ex) { return 0; }
                        })
                        .sum();
                if (totalAnswers > 0) avg = avg / totalAnswers;
                qResult.put("averageRating", Math.round(avg * 10.0) / 10.0);
            }

            questionResults.add(qResult);
        }

        results.put("questions", questionResults);
        return results;
    }
}
