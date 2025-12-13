package edu.llapp.domain;

import java.util.*;

/**
 * Skills Gap Report
 * includes missing and weak skills, overall score, etc.
 *
 * version 2.0 - supports new Semantic Matcher
 */
public class GapReport {
    private SkillSet currentSkills;     // user current skill
    private SkillSet targetSkills;      // target skill set
    private double score;               // overall score (0-100)
    private List<Skill> missingSkills;  // Skills that are completely absent (use a List to maintain order)
    private List<Skill> weakSkills;     // Skills that exist but are not at the required level

    /**
     * empty constructor
     */
    public GapReport() {
        this.missingSkills = new ArrayList<>();
        this.weakSkills = new ArrayList<>();
        this.score = 0.0;
    }

    /**
     * Full constructor (supports the new Semantic Matcher)
     */
    public GapReport(SkillSet current, SkillSet target, double score,
                     List<Skill> missingSkills, List<Skill> weakSkills) {
        this.currentSkills = current;
        this.targetSkills = target;
        this.score = score;
        this.missingSkills = new ArrayList<>(missingSkills);
        this.weakSkills = new ArrayList<>(weakSkills);
    }

    // ==================== Getters ====================

    public SkillSet getCurrentSkills() {
        return currentSkills;
    }

    public SkillSet getTargetSkills() {
        return targetSkills;
    }

    public List<Skill> getMissingSkills() {
        return missingSkills;
    }

    public List<Skill> getWeakSkills() {
        return weakSkills;
    }

    public double getScore() {
        return score;
    }

    // ==================== Setters ====================

    public void setCurrentSkills(SkillSet currentSkills) {
        this.currentSkills = currentSkills;
    }

    public void setTargetSkills(SkillSet targetSkills) {
        this.targetSkills = targetSkills;
    }

    public void setMissingSkills(List<Skill> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public void setWeakSkills(List<Skill> weakSkills) {
        this.weakSkills = weakSkills;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // ==================== Convenience methods ====================

    /**
     * check if there are any gaps
     */
    public boolean hasGaps() {
        return !missingSkills.isEmpty() || !weakSkills.isEmpty();
    }

    /**
     * get total number of gaps
     */
    public int getTotalGaps() {
        return missingSkills.size() + weakSkills.size();
    }

    /**
     * add missing skill
     */
    public void addMissingSkill(Skill skill) {
        if (!missingSkills.contains(skill)) {
            missingSkills.add(skill);
        }
    }

    /**
     * add weak skill
     */
    public void addWeakSkill(Skill skill) {
        if (!weakSkills.contains(skill)) {
            weakSkills.add(skill);
        }
    }

    /**
     * check if it's a perfect match
     */
    public boolean isPerfectMatch() {
        return score >= 100.0 && !hasGaps();
    }

    /**
     * get match level as string
     */
    public String getMatchLevel() {
        if (score >= 90) return "Excellent";
        if (score >= 75) return "Good";
        if (score >= 60) return "Fair";
        if (score >= 40) return "Poor";
        return "Very Poor";
    }

    // ==================== output method ====================

    @Override
    public String toString() {
        return "GapReport{" +
                "score=" + String.format("%.1f", score) + "%" +
                ", matchLevel=" + getMatchLevel() +
                ", missingSkills=" + missingSkills.size() +
                ", weakSkills=" + weakSkills.size() +
                '}';
    }

    /**
     * detail report（for debug use）
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Gap Report ===\n");
        sb.append("Match Score: ").append(String.format("%.1f%%", score))
                .append(" (").append(getMatchLevel()).append(")\n\n");

        if (!missingSkills.isEmpty()) {
            sb.append("Missing Skills (").append(missingSkills.size()).append("):\n");
            for (Skill skill : missingSkills) {
                sb.append("  - ").append(skill.getName())
                        .append(" (").append(skill.getLevel()).append(")\n");
            }
            sb.append("\n");
        }

        if (!weakSkills.isEmpty()) {
            sb.append("Weak Skills (").append(weakSkills.size()).append("):\n");
            for (Skill skill : weakSkills) {
                sb.append("  - ").append(skill.getName())
                        .append(" (need ").append(skill.getLevel()).append(")\n");
            }
            sb.append("\n");
        }

        if (!hasGaps()) {
            sb.append("✓ Perfect match! No gaps found.\n");
        }

        return sb.toString();
    }
}