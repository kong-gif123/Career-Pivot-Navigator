package edu.llapp;

import edu.llapp.domain.*;
import edu.llapp.services.*;
import edu.llapp.strategies.matchers.*;
import edu.llapp.strategies.recommenders.*;

import java.util.*;

/**
 * Sample Program
 * Demonstrates the complete workflow
 */
public class DemoMain {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Career Pivot Navigator - Demo");
        System.out.println("=================================================\n");

        // Prepare test data
        UserProfile user = createSampleUser();
        SkillSet targetSkills = createTargetSkills();
        List<Course> courses = createSampleCourses();

        // Establish services
        SkillMatcherService matcherService = new SkillMatcherService(new KeywordBasedMatcher());
        CourseRecommenderService recommenderService = new CourseRecommenderService(new PopularityBasedStrategy());
        PathGeneratorService pathGenerator = new PathGeneratorService();
        ResumeBuilderService resumeBuilder = new ResumeBuilderService();

        AppController controller = new AppController(
                matcherService, recommenderService, pathGenerator, resumeBuilder);

        // Execute the complete process
        LearningPath path = controller.analyzeAndGeneratePath(user, targetSkills, courses);
        ResumeBullets resume = controller.buildResume(user, path);

        // Output
        System.out.println("\n=================================================");
        System.out.println("  FINAL RESULTS");
        System.out.println("=================================================\n");

        System.out.println("Learning Path Steps:");
        for (PlanStep step : path.getSteps()) {
            System.out.println("  " + step);
        }

        System.out.println("\nResume Bullets:");
        for (String bullet : resume.getItems()) {
            System.out.println("  • " + bullet);
        }

        System.out.println("\nTotal Learning Time: " + path.getTotalHours() + " hours");

        // Demonstration strategy switching
        System.out.println("\n=================================================");
        System.out.println("  Demonstrating Strategy Switch");
        System.out.println("=================================================\n");

        matcherService.setStrategy(new HybridMatcher());
        GapReport newGap = matcherService.match(user.getSkills(), targetSkills);
        System.out.println("New Gap Report with Hybrid Matcher: " + newGap);
    }

    private static UserProfile createSampleUser() {
        UserProfile user = new UserProfile("user001", "Data Analyst");
        user.getSkills().addSkill(new Skill("Java", SkillLevel.INTERMEDIATE));
        user.getSkills().addSkill(new Skill("Excel", SkillLevel.ADVANCED));
        System.out.println("User Profile: " + user);
        return user;
    }

    private static SkillSet createTargetSkills() {
        SkillSet target = new SkillSet();
        target.addSkill(new Skill("Java", SkillLevel.INTERMEDIATE));
        target.addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));
        target.addSkill(new Skill("SQL", SkillLevel.INTERMEDIATE));
        target.addSkill(new Skill("Excel", SkillLevel.ADVANCED));
        System.out.println("Target Skills: " + target);
        return target;
    }

    private static List<Course> createSampleCourses() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("C001", "Python for Beginners", "YouTube", "http://example.com/python", 10, Set.of("Python")));
        courses.add(new Course("C002", "SQL Fundamentals", "Coursera", "http://example.com/sql", 8, Set.of("SQL", "Database")));
        courses.add(new Course("C003", "Advanced Java", "Udemy", "http://example.com/java", 15, Set.of("Java", "OOP")));
        courses.add(new Course("C004", "Data Analysis with Python", "edX", "http://example.com/data", 12, Set.of("Python", "Data Science")));
        courses.add(new Course("C005", "Excel Power Query", "LinkedIn", "http://example.com/excel", 6, Set.of("Excel")));
        System.out.println("Available Courses: " + courses.size() + " courses\n");
        return courses;
    }
}