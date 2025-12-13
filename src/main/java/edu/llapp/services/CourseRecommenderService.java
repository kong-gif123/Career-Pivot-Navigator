package edu.llapp.services;

import edu.llapp.domain.*;
import edu.llapp.strategies.recommenders.ICourseStrategy;
import java.util.List;

/**
 * Course recommendation service
 */
public class CourseRecommenderService {
    private ICourseStrategy strategy;

    public CourseRecommenderService(ICourseStrategy strategy) {
        this.strategy = strategy;
    }

    public CourseListWithReasons recommend(GapReport gap, UserProfile profile, List<Course> availableCourses) {
        System.out.println("Using strategy: " + strategy.getAlgorithmType());
        return strategy.recommend(gap, profile, availableCourses);
    }

    public void setStrategy(ICourseStrategy strategy) {
        this.strategy = strategy;
    }
}