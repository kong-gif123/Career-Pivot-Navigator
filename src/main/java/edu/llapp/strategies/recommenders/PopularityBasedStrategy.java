package edu.llapp.strategies.recommenders;

import edu.llapp.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基於熱門度的推薦策略
 * 優先推薦能補足技能差距的課程
 */
public class PopularityBasedStrategy implements ICourseStrategy {

    @Override
    public String getAlgorithmType() {
        return "PopularityBased";
    }

    @Override
    public CourseListWithReasons recommend(GapReport gap, UserProfile profile, List<Course> availableCourses) {
        CourseListWithReasons result = new CourseListWithReasons();

        // 取得缺失技能的名稱
        Set<String> missingSkillNames = gap.getMissingSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        Set<String> weakSkillNames = gap.getWeakSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        // 推薦能補足缺口的課程
        for (Course course : availableCourses) {
            boolean fillsMissing = course.getSkills().stream()
                    .anyMatch(missingSkillNames::contains);
            boolean improvesWeak = course.getSkills().stream()
                    .anyMatch(weakSkillNames::contains);

            if (fillsMissing) {
                result.addCourse(course, new Reason("FILLS_GAP", "補足缺失技能"));
            } else if (improvesWeak) {
                result.addCourse(course, new Reason("IMPROVES_SKILL", "提升薄弱技能"));
            }

            // 最多推薦 5 門
            if (result.size() >= 5) break;
        }

        return result;
    }
}