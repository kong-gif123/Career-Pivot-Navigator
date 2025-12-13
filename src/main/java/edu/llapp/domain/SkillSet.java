package edu.llapp.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * skill set class
 * Manage a set of skills, providing operations such as adding and querying.
 */
public class SkillSet {
    private Set<Skill> items;

    public SkillSet() {
        this.items = new HashSet<>();
    }

    /**
     * add a skill
     */
    public void addSkill(Skill skill) {
        items.add(skill);
    }

    /**
     * Check if you possess a certain skill (compare only the name, not case sensitive).
     */
    public boolean hasSkill(String skillName) {
        return items.stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(skillName));
    }

    /**
     * Get skills by name
     */
    public Skill getSkill(String skillName) {
        return items.stream()
                .filter(s -> s.getName().equalsIgnoreCase(skillName))
                .findFirst()
                .orElse(null);
    }

    /**
     * get all skills
     */
    public Set<Skill> getItems() {
        return items;
    }

    /**
     * get number of skills
     */
    public int size() {
        return items.size();
    }

    /**
     * check if empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * get skill names
     */
    public Set<String> getSkillNames() {
        return items.stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "SkillSet{" + items.size() + " skills: " + getSkillNames() + "}";
    }
}