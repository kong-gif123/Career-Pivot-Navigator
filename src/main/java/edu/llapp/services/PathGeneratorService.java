package edu.llapp.services;

import edu.llapp.domain.*;

/**
 * Learning Path Generation Service
 * Converts recommended courses into a sequential learning plan
 */
public class PathGeneratorService {

    public LearningPath generate(CourseListWithReasons courseList) {
        LearningPath path = new LearningPath();

        // Generate steps in sequence
        for (int i = 0; i < courseList.getCourses().size(); i++) {
            Course course = courseList.getCourses().get(i);
            PlanStep step = new PlanStep(course, i + 1, course.getDurationHours());
            path.addStep(step);
        }

        return path;
    }
}