package edu.llapp.strategies.recommenders;

import edu.llapp.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 個人化推薦策略
 * 優先推薦短時間、高相關度的課程
 */
public class PersonalizedStrategy implements ICourseStrategy {

    @Override
    public String getAlgorithmType() {
        return "Personalized";
    }

    @Override
    public CourseListWithReasons recommend(GapReport gap, UserProfile profile, List<Course> availableCourses) {
        CourseListWithReasons result = new CourseListWithReasons();

        Set<String> missingSkillNames = gap.getMissingSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        Set<String> weakSkillNames = gap.getWeakSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        // 先收集相關課程
        List<Course> relevantCourses = new ArrayList<>();

        for (Course course : availableCourses) {
            boolean fillsMissing = course.getSkills().stream()
                    .anyMatch(missingSkillNames::contains);
            boolean improvesWeak = course.getSkills().stream()
                    .anyMatch(weakSkillNames::contains);

            if (fillsMissing || improvesWeak) {
                relevantCourses.add(course);
            }
        }

        // 按時長排序（優先推薦短課程）
        relevantCourses.sort(Comparator.comparingInt(Course::getDurationHours));

        // 取前 5 門
        for (int i = 0; i < Math.min(5, relevantCourses.size()); i++) {
            Course course = relevantCourses.get(i);

            if (course.getDurationHours() <= 10) {
                result.addCourse(course, new Reason("QUICK_WIN", "快速學習 (" + course.getDurationHours() + "h)"));
            } else {
                result.addCourse(course, new Reason("PERSONALIZED", "適合你的學習節奏"));
            }
        }

        return result;
    }
}