package edu.llapp.infra;

import edu.llapp.domain.Course;
import java.util.ArrayList;
import java.util.List;

/**
 * API Client (Unified Entry Point)
 * Integrating YouTube API + Cache + Fallback
 */
public class ApiClient {
    private YouTubeClient youtubeClient;
    private FallbackRouter fallbackRouter;
    private boolean useApi;  // If Use the real API

    // --- meta for GUI (show evidence when fallback happens) ---
    // last single-call status
    private boolean lastCallUsedFallback = false;
    private String lastCallNote = "";

    // last batch-call status
    private boolean lastBatchUsedFallback = false;
    private List<String> lastBatchFallbackSkills = new ArrayList<>();
    private String lastBatchNote = "";

    public ApiClient(boolean useApi) {
        this.useApi = useApi;
        this.youtubeClient = new YouTubeClient();
        this.fallbackRouter = new FallbackRouter(new LocalCatalogRepository());
    }

    /**
     * Search courses (intelligently switches between API and Local)
     * @param skillName skill name
     * @param maxResults How many requests can be returned at most
     * @return course list
     */
    public List<Course> searchCourses(String skillName, int maxResults) {
        // reset last single-call meta
        lastCallUsedFallback = false;
        lastCallNote = "YouTube API";

        if (!useApi) {
            System.out.println("Using local catalog (API disabled)");
            // API is intentionally disabled, this is not treated as an error fallback
            lastCallUsedFallback = false;
            lastCallNote = "Local catalog (API disabled)";
            return fallbackRouter.getLocalCourses(skillName);
        }

        try {
            System.out.println("Attempting YouTube API search...");
            List<Course> results = youtubeClient.searchCourses(skillName, maxResults);

            if (results.isEmpty()) {
                System.out.println("No results from API, using fallback");
                lastCallUsedFallback = true;
                lastCallNote = "API returned 0 results, using local catalog";
                return fallbackRouter.getLocalCourses(skillName);
            }

            return results;

        } catch (Exception e) {
            System.err.println("API failed: " + e.getMessage());
            System.out.println("Switching to fallback...");
            lastCallUsedFallback = true;
            lastCallNote = "API unavailable, using local catalog";
            return fallbackRouter.getLocalCourses(skillName);
        }
    }

    /**
     * Batch search (for multiple skills)
     */
    public List<Course> searchMultipleSkills(List<String> skillNames, int maxResultsPerSkill) {
        List<Course> allCourses = new ArrayList<>();

        // reset last batch meta
        lastBatchUsedFallback = false;
        lastBatchFallbackSkills = new ArrayList<>();
        lastBatchNote = "YouTube API";

        for (String skill : skillNames) {
            List<Course> courses = searchCourses(skill, maxResultsPerSkill);
            allCourses.addAll(courses);

            // only mark fallback when API is enabled and we had to downgrade
            if (useApi && lastCallUsedFallback) {
                lastBatchUsedFallback = true;
                lastBatchFallbackSkills.add(skill);
            }
        }

        if (!useApi) {
            lastBatchNote = "Local catalog (API disabled)";
        } else if (lastBatchUsedFallback) {
            lastBatchNote = "YouTube API (partial) + local fallback";
        }

        return allCourses;
    }

    /**
     * For GUI display: whether the last batch search used fallback at least once.
     */
    public boolean wasLastBatchFallbackUsed() {
        return lastBatchUsedFallback;
    }

    /**
     * For GUI display: list of skills that fell back to local catalog in the last batch.
     */
    public List<String> getLastBatchFallbackSkills() {
        return new ArrayList<>(lastBatchFallbackSkills);
    }

    /**
     * For GUI display: human readable note of last batch source.
     */
    public String getLastBatchNote() {
        return lastBatchNote;
    }

    /**
     * Switch API mode
     */
    public void setUseApi(boolean useApi) {
        this.useApi = useApi;
        System.out.println("API mode: " + (useApi ? "ENABLED" : "DISABLED"));
    }

    public boolean isUsingApi() {
        return useApi;
    }
}