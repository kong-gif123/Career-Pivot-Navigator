package edu.llapp.test;

import edu.llapp.domain.*;
import edu.llapp.services.*;
import edu.llapp.strategies.matchers.*;
import edu.llapp.strategies.recommenders.*;
import edu.llapp.infra.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * System integration testing
 * Testing the complete end-to-end process
 */
public class SystemTest {

    private AppController controller;
    private ApiClient apiClient;
    private UserProfile testUser;
    private SkillSet targetSkills;

    @BeforeEach
    public void setUp() {
        // Initialize service
        StrategyRegistry registry = new StrategyRegistry();
        apiClient = new ApiClient(false);  // Use local during testing to save quota.

        SkillMatcherService matcherService = new SkillMatcherService(registry.getMatcher("keyword"));
        CourseRecommenderService recommenderService = new CourseRecommenderService(registry.getRecommender("popularity"));
        PathGeneratorService pathGenerator = new PathGeneratorService();
        ResumeBuilderService resumeBuilder = new ResumeBuilderService();

        controller = new AppController(matcherService, recommenderService, pathGenerator, resumeBuilder);

        // Create test data
        testUser = new UserProfile("test_user", "Data Analyst");
        testUser.getSkills().addSkill(new Skill("Java", SkillLevel.INTERMEDIATE));
        testUser.getSkills().addSkill(new Skill("Excel", SkillLevel.ADVANCED));

        targetSkills = new SkillSet();
        targetSkills.addSkill(new Skill("Java", SkillLevel.INTERMEDIATE));
        targetSkills.addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));
        targetSkills.addSkill(new Skill("SQL", SkillLevel.INTERMEDIATE));
        targetSkills.addSkill(new Skill("Excel", SkillLevel.ADVANCED));
    }

    /**
     * TC01: Complete Process Testing - Success Path
     * Tests the complete process from skills analysis to resume generation.
     */
    @Test
    public void testTC01_CompleteWorkflowSuccess() {
        System.out.println("\n=== TC01: Complete Workflow Success ===");

        // Step 1: Analyze the skills gap
        GapReport gap = controller.getMatcherService().match(testUser.getSkills(), targetSkills);

        assertNotNull(gap);
        assertTrue(gap.hasGaps());
        assertEquals(2, gap.getMissingSkills().size());  // Python, SQL
        System.out.println("Gap analysis completed: " + gap.getMissingSkills().size() + " gaps found");

        // Step 2: search courses for missing skills
        List<String> missingSkills = gap.getMissingSkills().stream()
                .map(Skill::getName)
                .collect(java.util.stream.Collectors.toList());

        List<Course> courses = apiClient.searchMultipleSkills(missingSkills, 3);

        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        System.out.println("Found " + courses.size() + " courses");

        // Step 3: recommend courses
        CourseListWithReasons recommendations = controller.getRecommenderService()
                .recommend(gap, testUser, courses);

        assertNotNull(recommendations);
        assertFalse(recommendations.getCourses().isEmpty());
        System.out.println("Recommendations generated: " + recommendations.size() + " courses");

        // Step 4: generate learning path
        PathGeneratorService pathGen = new PathGeneratorService();
        LearningPath path = pathGen.generate(recommendations);

        assertNotNull(path);
        assertTrue(path.getTotalHours() > 0);
        System.out.println("Learning path created: " + path.size() + " steps, " + path.getTotalHours() + " hours");

        // Step 5: generate resume bullets
        ResumeBullets resume = controller.getResumeBuilder().build(testUser, path);

        assertNotNull(resume);
        assertFalse(resume.getItems().isEmpty());
        System.out.println("Resume bullets generated: " + resume.size() + " items");

        System.out.println("TC01 PASSED");
    }

    /**
     * TC02: Policy Switching Test
     * Tests whether different policies can be switched and executed normally.
     */
    @Test
    public void testTC02_StrategySwitching() {
        System.out.println("\n=== TC02: Strategy Switching ===");

        StrategyRegistry registry = new StrategyRegistry();

        // Testing Matcher strategy switching
        ISkillMatcher keyword = registry.getMatcher("keyword");
        ISkillMatcher hybrid = registry.getMatcher("hybrid");

        controller.getMatcherService().setStrategy(keyword);
        GapReport gap1 = controller.getMatcherService().match(testUser.getSkills(), targetSkills);

        controller.getMatcherService().setStrategy(hybrid);
        GapReport gap2 = controller.getMatcherService().match(testUser.getSkills(), targetSkills);

        assertNotNull(gap1);
        assertNotNull(gap2);
        System.out.println("Keyword strategy: " + gap1.getScore() + "%");
        System.out.println("Hybrid strategy: " + gap2.getScore() + "%");

        // Testing Recommender policy switching
        List<Course> courses = apiClient.searchCourses("Python", 5);
        GapReport gap = controller.getMatcherService().match(testUser.getSkills(), targetSkills);

        ICourseStrategy popularity = registry.getRecommender("popularity");
        ICourseStrategy personalized = registry.getRecommender("personalized");

        controller.getRecommenderService().setStrategy(popularity);
        CourseListWithReasons rec1 = controller.getRecommenderService().recommend(gap, testUser, courses);

        controller.getRecommenderService().setStrategy(personalized);
        CourseListWithReasons rec2 = controller.getRecommenderService().recommend(gap, testUser, courses);

        assertNotNull(rec1);
        assertNotNull(rec2);
        System.out.println("Popularity strategy: " + rec1.size() + " courses");
        System.out.println("Personalized strategy: " + rec2.size() + " courses");

        System.out.println("TC02 PASSED");
    }

    /**
     * TC03: API Fallback Test
     * Tests whether the API correctly falls back to the local directory when it fails.
     */
    @Test
    public void testTC03_ApiFallback() {
        System.out.println("\n=== TC03: API Fallback ===");

        // Simulate API shutdown
        apiClient.setUseApi(false);

        List<Course> courses = apiClient.searchCourses("Python", 5);

        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        System.out.println("Fallback to local catalog: " + courses.size() + " courses found");

        // Confirmed to be a local course
        boolean allLocal = courses.stream()
                .allMatch(c -> c.getId().startsWith("C0"));  // Local courses 的 ID 格式

        assertTrue(allLocal);
        System.out.println("All courses from local catalog");

        // Switch back to API mode
        apiClient.setUseApi(true);
        System.out.println("API mode restored");

        System.out.println("TC03 PASSED");
    }

    /**
     * TC04: Empty Skill Set Test
     * Test Boundary Condition: The user has no skills.
     */
    @Test
    public void testTC04_EmptySkillSet() {
        System.out.println("\n=== TC04: Empty Skill Set ===");

        UserProfile emptyUser = new UserProfile("empty_user", "Software Engineer");
        // Without adding any skills

        SkillSet target = new SkillSet();
        target.addSkill(new Skill("Java", SkillLevel.ADVANCED));
        target.addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));

        GapReport gap = controller.getMatcherService().match(emptyUser.getSkills(), target);

        assertNotNull(gap);
        assertEquals(0.0, gap.getScore(), 0.01);  // The score should be 0
        assertEquals(2, gap.getMissingSkills().size());  // All skills are lacking
        System.out.println("Gap score for empty skills: " + gap.getScore() + "%");
        System.out.println("Missing skills: " + gap.getMissingSkills().size());

        System.out.println("TC04 PASSED");
    }

    /**
     * TC05: Perfect Match Test
     * Tests the situation when the user already possesses all skills.
     */
    @Test
    public void testTC05_PerfectMatch() {
        System.out.println("\n=== TC05: Perfect Match ===");

        UserProfile perfectUser = new UserProfile("perfect_user", "Data Analyst");
        perfectUser.getSkills().addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));
        perfectUser.getSkills().addSkill(new Skill("SQL", SkillLevel.INTERMEDIATE));
        perfectUser.getSkills().addSkill(new Skill("Excel", SkillLevel.ADVANCED));

        SkillSet perfectTarget = new SkillSet();
        perfectTarget.addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));
        perfectTarget.addSkill(new Skill("SQL", SkillLevel.INTERMEDIATE));
        perfectTarget.addSkill(new Skill("Excel", SkillLevel.ADVANCED));

        GapReport gap = controller.getMatcherService().match(perfectUser.getSkills(), perfectTarget);

        assertNotNull(gap);
        assertEquals(100.0, gap.getScore(), 0.01);  // prefect score
        assertTrue(gap.getMissingSkills().isEmpty());  // not missing
        assertTrue(gap.getWeakSkills().isEmpty());  // not weak
        System.out.println("Perfect match score: " + gap.getScore() + "%");
        System.out.println("No gaps found");

        System.out.println("TC05 PASSED");
    }
}