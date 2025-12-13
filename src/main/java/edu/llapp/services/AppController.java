package edu.llapp.services;

import edu.llapp.domain.*;
import java.util.List;

/**
 * Application Controller
 * Coordinates the entire workflow: Matching → Recommendation → Path Generation → Resume Creation
 */
public class AppController {
    private SkillMatcherService matcherService;
    private CourseRecommenderService recommenderService;
    private PathGeneratorService pathGenerator;
    private ResumeBuilderService resumeBuilder;

    public AppController(
            SkillMatcherService matcherService,
            CourseRecommenderService recommenderService,
            PathGeneratorService pathGenerator,
            ResumeBuilderService resumeBuilder) {
        this.matcherService = matcherService;
        this.recommenderService = recommenderService;
        this.pathGenerator = pathGenerator;
        this.resumeBuilder = resumeBuilder;
    }

    /**
     * Complete process: Analyze skills gaps → Generate learning paths
     */
    public LearningPath analyzeAndGeneratePath(
            UserProfile profile,
            SkillSet targetSkills,
            List<Course> availableCourses) {

        // Step 1: analysis skill gap
        System.out.println("\n=== Step 1: Analyzing skill gaps ===");
        GapReport gap = matcherService.match(profile.getSkills(), targetSkills);
        System.out.println("Gap Report: " + gap);

        // Step 2: recommend courses
        System.out.println("\n=== Step 2: Recommending courses ===");
        CourseListWithReasons recommendations = recommenderService.recommend(gap, profile, availableCourses);
        System.out.println("Recommended " + recommendations.size() + " courses");

        // Step 3: generate learning path
        System.out.println("\n=== Step 3: Generating learning path ===");
        LearningPath path = pathGenerator.generate(recommendations);
        System.out.println("Learning Path: " + path);

        return path;
    }

    /**
     * build resume bullets based on learning path
     */
    public ResumeBullets buildResume(UserProfile profile, LearningPath path) {
        System.out.println("\n=== Step 4: Building resume bullets ===");
        return resumeBuilder.build(profile, path);
    }

    // Getters for GUI
    public SkillMatcherService getMatcherService() {
        return matcherService;
    }

    public CourseRecommenderService getRecommenderService() {
        return recommenderService;
    }

    public PathGeneratorService getPathGenerator() {
        return pathGenerator;
    }

    public ResumeBuilderService getResumeBuilder() {
        return resumeBuilder;
    }
}