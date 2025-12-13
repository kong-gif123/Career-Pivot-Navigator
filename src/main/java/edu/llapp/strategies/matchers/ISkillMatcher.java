package edu.llapp.strategies.matchers;

import edu.llapp.domain.GapReport;
import edu.llapp.domain.SkillSet;

/**
 * 技能匹配策略介面
 * 定義如何比對現有技能與目標技能
 */
public interface ISkillMatcher {
    /**
     * 策略名稱
     */
    String getName();

    /**
     * 策略描述
     */
    String getDescription();

    /**
     * 執行技能匹配，返回差距報告
     * @param current 現有技能集
     * @param target 目標技能集
     * @return 差距報告
     */
    GapReport match(SkillSet current, SkillSet target);
}