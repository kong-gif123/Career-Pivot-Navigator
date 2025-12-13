package edu.llapp.strategies.matchers;

import edu.llapp.domain.GapReport;
import edu.llapp.domain.Skill;
import edu.llapp.domain.SkillSet;

/**
 * strategy based on keyword matching
 * only checks skill names and levels
 */
public class KeywordBasedMatcher implements ISkillMatcher {

    @Override
    public String getName() {
        return "KeywordBased";
    }

    @Override
    public String getDescription() {
        return "Simple keyword matching - fast but basic";
    }

    @Override
    public GapReport match(SkillSet current, SkillSet target) {
        GapReport report = new GapReport();

        // finds missing and weak skills
        for (Skill targetSkill : target.getItems()) {
            if (!current.hasSkill(targetSkill.getName())) {
                report.getMissingSkills().add(targetSkill);
            } else {
                // skill exists, check level
                Skill currentSkill = current.getSkill(targetSkill.getName());
                if (currentSkill != null &&
                        currentSkill.getLevel().ordinal() < targetSkill.getLevel().ordinal()) {
                    report.getWeakSkills().add(targetSkill);
                }
            }
        }

        // calculate score
        int targetSize = target.getItems().size();
        int gapSize = report.getMissingSkills().size() + report.getWeakSkills().size();
        int matchedSize = targetSize - gapSize;

        report.setScore(targetSize > 0 ? (matchedSize * 100.0 / targetSize) : 0);

        return report;
    }
}