package edu.llapp.domain;

import java.util.Objects;

/**
 * skill class
 * Represents a specific skill, including name, level, and optional ESCO URI.
 */
public class Skill {
    private String name;
    private SkillLevel level;
    private String escoUri;  // optional，URI for ESCO Skill ontology

    // Constructor
    public Skill(String name, SkillLevel level) {
        this.name = name;
        this.level = level;
    }

    public Skill(String name, SkillLevel level, String escoUri) {
        this.name = name;
        this.level = level;
        this.escoUri = escoUri;
    }

    // Getters
    public String getName() {
        return name;
    }

    public SkillLevel getLevel() {
        return level;
    }

    public String getEscoUri() {
        return escoUri;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }

    public void setEscoUri(String escoUri) {
        this.escoUri = escoUri;
    }

    // equals 和 hashCode（use for Set and Map）
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(name, skill.name) && level == skill.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level);
    }

    // toString（convenient to debug）
    @Override
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                ", level=" + level +
                (escoUri != null ? ", escoUri='" + escoUri + '\'' : "") +
                '}';
    }
}