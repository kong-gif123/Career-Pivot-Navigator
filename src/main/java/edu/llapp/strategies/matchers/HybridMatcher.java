package edu.llapp.strategies.matchers;

import edu.llapp.domain.GapReport;
import edu.llapp.domain.SkillSet;

/**
 * 混合型技能匹配策略
 * 結合關鍵字和語意匹配的優點
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
        // 分別用兩種策略計算
        GapReport keywordReport = keywordMatcher.match(current, target);
        GapReport semanticReport = semanticMatcher.match(current, target);

        // 組合結果：取 keyword 的 gap（較嚴格），取兩者分數的加權平均
        GapReport finalReport = new GapReport();
        finalReport.setMissingSkills(keywordReport.getMissingSkills());
        finalReport.setWeakSkills(keywordReport.getWeakSkills());

        // 分數加權：keyword 60%, semantic 40%
        double finalScore = keywordReport.getScore() * 0.6 + semanticReport.getScore() * 0.4;
        finalReport.setScore(finalScore);

        return finalReport;
    }
}