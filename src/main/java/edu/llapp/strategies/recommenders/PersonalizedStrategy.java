package edu.llapp.strategies.recommenders;

import edu.llapp.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Personalized recommendation strategy
 * Prioritize recommending short, highly relevant courses
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

        // First, collect relevant courses.
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

        // Sort by duration (shorter courses preferred)
        relevantCourses.sort(Comparator.comparingInt(Course::getDurationHours));

        // Take the first 5 doors
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