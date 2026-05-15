package edu.llapp.services;

import edu.llapp.domain.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Application controller that orchestrates the full career pivot workflow:
 * Skill matching → Course recommendation → Learning path generation → Resume building
 */
public class AppController {
    private static final Logger logger = Logger.getLogger(AppController.class.getName());

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
     * Run the full analysis pipeline: gap analysis → recommendations → learning path.
     *
     * @param profile          the user's current profile and skills
     * @param targetSkills     the skill set required for the target role
     * @param availableCourses courses to choose from
     * @return a generated learning path
     */
    public LearningPath analyzeAndGeneratePath(
            UserProfile profile,
            SkillSet targetSkills,
            List<Course> availableCourses) {

        logger.info("Step 1: Analyzing skill gaps");
        GapReport gap = matcherService.match(profile.getSkills(), targetSkills);
        logger.info("Gap report: " + gap);

        logger.info("Step 2: Recommending courses");
        CourseListWithReasons recommendations = recommenderService.recommend(gap, profile, availableCourses);
        logger.info("Recommended " + recommendations.size() + " courses");

        logger.info("Step 3: Generating learning path");
        LearningPath path = pathGenerator.generate(recommendations);
        logger.info("Learning path generated: " + path);

        return path;
    }

    /**
     * Build resume bullet points based on the user's profile and completed learning path.
     */
    public ResumeBullets buildResume(UserProfile profile, LearningPath path) {
        logger.info("Step 4: Building resume bullets");
        return resumeBuilder.build(profile, path);
    }

    // Getters for GUI access
    public SkillMatcherService getMatcherService() { return matcherService; }
    public CourseRecommenderService getRecommenderService() { return recommenderService; }
    public PathGeneratorService getPathGenerator() { return pathGenerator; }
    public ResumeBuilderService getResumeBuilder() { return resumeBuilder; }
}
