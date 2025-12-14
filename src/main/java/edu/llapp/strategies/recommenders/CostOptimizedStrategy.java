package edu.llapp.strategies.recommenders;

import edu.llapp.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cost Optimization Recommendation Strategy
 * Prioritize recommending free/low-cost courses
 */
public class CostOptimizedStrategy implements ICourseStrategy {

    @Override
    public String getAlgorithmType() {
        return "CostOptimized";
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

        // Free courses are highly recommended.（YouTube, Khan Academy, freeCodeCamp）
        List<String> freePlatforms = Arrays.asList("YouTube", "Khan Academy", "freeCodeCamp",
                "GitHub Learning Lab", "AWS Training", "Microsoft Learn");

        List<Course> freeCourses = new ArrayList<>();
        List<Course> paidCourses = new ArrayList<>();

        for (Course course : availableCourses) {
            boolean fillsGap = course.getSkills().stream()
                    .anyMatch(s -> missingSkillNames.contains(s) || weakSkillNames.contains(s));

            if (fillsGap) {
                if (freePlatforms.stream().anyMatch(p -> course.getProvider().contains(p))) {
                    freeCourses.add(course);
                } else {
                    paidCourses.add(course);
                }
            }
        }

        // First, promote the free ones.
        for (Course course : freeCourses) {
            if (result.size() >= 5) break;
            result.addCourse(course, new Reason("FREE", "免費課程"));
        }

        // If that's not enough, we'll push paid options.
        for (Course course : paidCourses) {
            if (result.size() >= 5) break;
            result.addCourse(course, new Reason("VALUE", "高性價比"));
        }

        return result;
    }
}