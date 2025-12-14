package edu.llapp.strategies.matchers;

import edu.llapp.domain.GapReport;
import edu.llapp.domain.SkillSet;

/**
 * Skill Matching Strategy Interface
 * Defines how existing skills are compared with target skills
 */
public interface ISkillMatcher {
    /**
     * Strategy Name
     */
    String getName();

    /**
     * Strategy Description
     */
    String getDescription();

    /**
     * Perform skills matching and return a gap report.
     * @param current Existing skill set
     * @param target Target Skills Set
     * @return Gap Report
     */
    GapReport match(SkillSet current, SkillSet target);
}