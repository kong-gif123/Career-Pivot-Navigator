package edu.llapp.strategies.recommenders;

import edu.llapp.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Popularity-based recommendation strategy
 * Prioritize recommending courses that can bridge skill gaps
 */
public class PopularityBasedStrategy implements ICourseStrategy {

    @Override
    public String getAlgorithmType() {
        return "PopularityBased";
    }

    @Override
    public CourseListWithReasons recommend(GapReport gap, UserProfile profile, List<Course> availableCourses) {
        CourseListWithReasons result = new CourseListWithReasons();

        // Obtain the name of the missing skill
        Set<String> missingSkillNames = gap.getMissingSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        Set<String> weakSkillNames = gap.getWeakSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        // Recommend courses that can fill the gaps in your knowledge.
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

            // A maximum of 5 courses can be recommended.
            if (result.size() >= 5) break;
        }

        return result;
    }
}