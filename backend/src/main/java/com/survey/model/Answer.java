package com.survey.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "response_id", nullable = false)
    private String responseId;

    @Column(columnDefinition = "TEXT")
    private String value;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    public Answer() {}

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }

    // ── Getters ──────────────────────────────────────────

    public Long getId() { return id; }

    public Question getQuestion() { return question; }

    public String getResponseId() { return responseId; }

    public String getValue() { return value; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }

    // ── Setters ──────────────────────────────────────────

    public void setId(Long id) { this.id = id; }

    public void setQuestion(Question question) { this.question = question; }

    public void setResponseId(String responseId) { this.responseId = responseId; }

    public void setValue(String value) { this.value = value; }

    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
