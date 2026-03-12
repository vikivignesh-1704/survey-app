package com.survey.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question text is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "is_required")
    private boolean required = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    // Stored as pipe-separated string e.g. "Option A|Option B|Option C"
    @Column(name = "options_data", columnDefinition = "TEXT")
    private String optionsData;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();

    public Question() {}

    public enum QuestionType {
        MULTIPLE_CHOICE,
        CHECKBOX,
        TEXT,
        RATING,
        YES_NO
    }

    // ── Getters ──────────────────────────────────────────

    public Long getId() { return id; }

    public String getText() { return text; }

    public QuestionType getType() { return type; }

    public int getOrderIndex() { return orderIndex; }

    public boolean isRequired() { return required; }

    public Survey getSurvey() { return survey; }

    public String getOptionsData() { return optionsData; }

    public List<Answer> getAnswers() { return answers; }

    public List<String> getOptions() {
        if (optionsData == null || optionsData.isBlank()) return List.of();
        return List.of(optionsData.split("\\|"));
    }

    // ── Setters ──────────────────────────────────────────

    public void setId(Long id) { this.id = id; }

    public void setText(String text) { this.text = text; }

    public void setType(QuestionType type) { this.type = type; }

    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public void setRequired(boolean required) { this.required = required; }

    public void setSurvey(Survey survey) { this.survey = survey; }

    public void setOptionsData(String optionsData) { this.optionsData = optionsData; }

    public void setAnswers(List<Answer> answers) { this.answers = answers; }

    public void setOptions(List<String> options) {
        this.optionsData = options == null ? null : String.join("|", options);
    }
}
