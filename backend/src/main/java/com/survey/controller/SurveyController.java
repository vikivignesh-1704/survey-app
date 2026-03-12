package com.survey.controller;

import com.survey.model.Survey;
import com.survey.service.SurveyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
@CrossOrigin(origins = "*") // Adjust for production
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    // GET /api/surveys  — list all surveys
    @GetMapping
    public ResponseEntity<List<Survey>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    // GET /api/surveys/{id}  — get a single survey with questions
    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurvey(@PathVariable Long id) {
        return surveyService.getSurveyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/surveys  — create a new survey
    @PostMapping
    public ResponseEntity<Survey> createSurvey(@Valid @RequestBody Survey survey) {
        Survey created = surveyService.createSurvey(survey);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/surveys/{id}  — update a survey
    @PutMapping("/{id}")
    public ResponseEntity<Survey> updateSurvey(@PathVariable Long id,
                                                @Valid @RequestBody Survey survey) {
        try {
            return ResponseEntity.ok(surveyService.updateSurvey(id, survey));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/surveys/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/surveys/{id}/submit  — submit responses
    // Body: { "answers": { "questionId": "answerValue", ... } }
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, String>> submitResponse(
            @PathVariable Long id,
            @RequestBody Map<String, Map<Long, String>> payload) {
        try {
            surveyService.submitResponse(id, payload.get("answers"));
            return ResponseEntity.ok(Map.of("message", "Response submitted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/surveys/{id}/results  — get survey results & analytics
    @GetMapping("/{id}/results")
    public ResponseEntity<Map<String, Object>> getResults(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(surveyService.getSurveyResults(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
