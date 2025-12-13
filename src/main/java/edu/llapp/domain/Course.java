package edu.llapp.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * course class
 * represents a learning course from various providers
 */
public class Course {
    private String id;
    private String title;
    private String provider;        // course provider（eg YouTube, Coursera）
    private String url;
    private int durationHours;      // expect duration hours
    private Set<String> skills;     // teach skills（ String）

    public Course(String id, String title, String provider, String url, int durationHours, Set<String> skills) {
        this.id = id;
        this.title = title;
        this.provider = provider;
        this.url = url;
        this.durationHours = durationHours;
        this.skills = skills != null ? skills : new HashSet<>();
    }

    // 簡化版 Constructor
    public Course(String id, String title, int durationHours, Set<String> skills) {
        this(id, title, "Unknown", "", durationHours, skills);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getProvider() {
        return provider;
    }

    public String getUrl() {
        return url;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public Set<String> getSkills() {
        return skills;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    /**
     * check if the course teaches a specific skill
     */
    public boolean teachesSkill(String skillName) {
        return skills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(skillName));
    }

    @Override
    public String toString() {
        return "Course{" +
                "title='" + title + '\'' +
                ", provider='" + provider + '\'' +
                ", duration=" + durationHours + "h" +
                ", skills=" + skills.size() +
                '}';
    }
}