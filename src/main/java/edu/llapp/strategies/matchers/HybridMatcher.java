package edu.llapp.strategies.matchers;

import edu.llapp.domain.GapReport;
import edu.llapp.domain.SkillSet;

/**
 * Hybrid Skill Matching Strategy
 * Combining the advantages of keyword and semantic matching
 */
public class HybridMatcher implements ISkillMatcher {
    private KeywordBasedMatcher keywordMatcher;
    private SemanticMatcher semanticMatcher;

    public HybridMatcher() {
        this.keywordMatcher = new KeywordBasedMatcher();
        this.semanticMatcher = new SemanticMatcher();
    }

    @Override
    public String getName() {
        return "Hybrid";
    }

    @Override
    public String getDescription() {
        return "Combines keyword and semantic matching";
    }

    @Override
    public GapReport match(SkillSet current, SkillSet target) {
        // Calculate using two different strategies
        GapReport keywordReport = keywordMatcher.match(current, target);
        GapReport semanticReport = semanticMatcher.match(current, target);

        // Combined result: Take the gap between keywords (more stringent), and then take the weighted average of the scores from both.
        GapReport finalReport = new GapReport();
        finalReport.setMissingSkills(keywordReport.getMissingSkills());
        finalReport.setWeakSkills(keywordReport.getWeakSkills());

        // Score weighting: keyword 60%, semantic 40%
        double finalScore = keywordReport.getScore() * 0.6 + semanticReport.getScore() * 0.4;
        finalReport.setScore(finalScore);

        return finalReport;
    }
}