package com.survey.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "surveys")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Survey title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private boolean active = true;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderIndex ASC")
    private List<Question> questions = new ArrayList<>();

    @Column(name = "response_count")
    private int responseCount = 0;

    public Survey() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters ──────────────────────────────────────────

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isActive() { return active; }

    public List<Question> getQuestions() { return questions; }

    public int getResponseCount() { return responseCount; }

    // ── Setters ──────────────────────────────────────────

    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setActive(boolean active) { this.active = active; }

    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public void setResponseCount(int responseCount) { this.responseCount = responseCount; }
}
