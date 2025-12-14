package edu.llapp.strategies.recommenders;

import edu.llapp.domain.CourseListWithReasons;
import edu.llapp.domain.GapReport;
import edu.llapp.domain.UserProfile;
import edu.llapp.domain.Course;
import java.util.List;

/**
 * Course Recommendation Strategy Interface
 */
public interface ICourseStrategy {
    /**
     * Strategy type
     */
    String getAlgorithmType();

    /**
     * Recommended courses
     * @param gap Skills Gap Report
     * @param profile User Profile
     * @param availableCourses Available Course List
     * @return Recommended courses and reasons
     */
    CourseListWithReasons recommend(GapReport gap, UserProfile profile, List<Course> availableCourses);
}