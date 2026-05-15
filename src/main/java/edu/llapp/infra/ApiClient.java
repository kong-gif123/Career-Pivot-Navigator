package edu.llapp.infra;

import edu.llapp.domain.Course;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Unified API entry point.
 * Coordinates YouTube API calls, caching, and fallback to local catalog.
 */
public class ApiClient {
    private static final Logger logger = Logger.getLogger(ApiClient.class.getName());

    private YouTubeClient youtubeClient;
    private FallbackRouter fallbackRouter;
    private boolean useApi;

    // Metadata for GUI: tracks fallback status of the last call
    private boolean lastCallUsedFallback = false;
    private String lastCallNote = "";

    // Metadata for GUI: tracks fallback status of the last batch call
    private boolean lastBatchUsedFallback = false;
    private List<String> lastBatchFallbackSkills = new ArrayList<>();
    private String lastBatchNote = "";

    public ApiClient(boolean useApi) {
        this.useApi = useApi;
        this.youtubeClient = new YouTubeClient();
        this.fallbackRouter = new FallbackRouter(new LocalCatalogRepository());
    }

    /**
     * Search for courses for a given skill.
     * Automatically falls back to the local catalog if the API is unavailable or returns no results.
     *
     * @param skillName  the skill to search for
     * @param maxResults maximum number of results to return
     * @return list of matching courses
     */
    public List<Course> searchCourses(String skillName, int maxResults) {
        lastCallUsedFallback = false;
        lastCallNote = "YouTube API";

        if (!useApi) {
            logger.info("API disabled — using local catalog for: " + skillName);
            lastCallNote = "Local catalog (API disabled)";
            return fallbackRouter.getLocalCourses(skillName);
        }

        try {
            logger.info("Searching YouTube API for: " + skillName);
            List<Course> results = youtubeClient.searchCourses(skillName, maxResults);

            if (results.isEmpty()) {
                logger.warning("No results from API — falling back to local catalog");
                lastCallUsedFallback = true;
                lastCallNote = "API returned 0 results, using local catalog";
                return fallbackRouter.getLocalCourses(skillName);
            }

            return results;

        } catch (Exception e) {
            logger.severe("API call failed: " + e.getMessage() + " — switching to fallback");
            lastCallUsedFallback = true;
            lastCallNote = "API unavailable, using local catalog";
            return fallbackRouter.getLocalCourses(skillName);
        }
    }

    /**
     * Search for courses across multiple skills in batch.
     *
     * @param skillNames        list of skills to search for
     * @param maxResultsPerSkill maximum results per skill
     * @return combined list of courses
     */
    public List<Course> searchMultipleSkills(List<String> skillNames, int maxResultsPerSkill) {
        List<Course> allCourses = new ArrayList<>();

        lastBatchUsedFallback = false;
        lastBatchFallbackSkills = new ArrayList<>();
        lastBatchNote = "YouTube API";

        for (String skill : skillNames) {
            List<Course> courses = searchCourses(skill, maxResultsPerSkill);
            allCourses.addAll(courses);

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

    /** @return true if the last batch search used fallback for at least one skill */
    public boolean wasLastBatchFallbackUsed() {
        return lastBatchUsedFallback;
    }

    /** @return skills that fell back to local catalog in the last batch search */
    public List<String> getLastBatchFallbackSkills() {
        return new ArrayList<>(lastBatchFallbackSkills);
    }

    /** @return human-readable source note for the last batch search */
    public String getLastBatchNote() {
        return lastBatchNote;
    }

    /** Toggle between live API and local catalog mode. */
    public void setUseApi(boolean useApi) {
        this.useApi = useApi;
        logger.info("API mode: " + (useApi ? "ENABLED" : "DISABLED"));
    }

    public boolean isUsingApi() {
        return useApi;
    }
}
