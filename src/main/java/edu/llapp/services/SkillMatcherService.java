package edu.llapp.services;

import edu.llapp.domain.GapReport;
import edu.llapp.domain.SkillSet;
import edu.llapp.strategies.matchers.ISkillMatcher;

/**
 * skill matcher service
 * relies on different skill matching strategies
 */
public class SkillMatcherService {
    private ISkillMatcher strategy;

    public SkillMatcherService(ISkillMatcher strategy) {
        this.strategy = strategy;
    }

    /**
     * do skill matching using the current strategy
     */
    public GapReport match(SkillSet current, SkillSet target) {
        System.out.println("Using strategy: " + strategy.getName());
        return strategy.match(current, target);
    }

    /**
     * dynamically set the skill matching strategy
     */
    public void setStrategy(ISkillMatcher strategy) {
        this.strategy = strategy;
    }

    public ISkillMatcher getStrategy() {
        return strategy;
    }
}