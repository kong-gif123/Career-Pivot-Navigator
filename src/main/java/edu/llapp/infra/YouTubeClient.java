package edu.llapp.infra;

import edu.llapp.domain.Course;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * YouTube Data API v3 client.
 * Searches for instructional videos and maps them to Course objects.
 * Includes in-memory caching and exponential backoff retry logic.
 */
public class YouTubeClient {
    private static final Logger logger = Logger.getLogger(YouTubeClient.class.getName());

    private String apiKey;
    private String baseUrl;
    private int timeoutMs;
    private int maxRetries;
    private SimpleCache<String, List<Course>> cache;

    public YouTubeClient() {
        this.apiKey = ConfigLoader.getYouTubeApiKey();
        this.baseUrl = ConfigLoader.getYouTubeBaseUrl();
        this.timeoutMs = ConfigLoader.getApiTimeout();
        this.maxRetries = ConfigLoader.getMaxRetries();
        this.cache = new SimpleCache<>(ConfigLoader.getCacheTTL());
    }

    /**
     * Search for courses matching the given skill.
     * Results are cached by skill name + maxResults.
     *
     * @param skillName  the skill to search for (e.g. "Python")
     * @param maxResults maximum number of results
     * @return list of matching courses
     */
    public List<Course> searchCourses(String skillName, int maxResults) {
        String cacheKey = skillName + "_" + maxResults;

        List<Course> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        String query = skillName + " tutorial beginner";

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                List<Course> results = searchWithRetry(query, maxResults);

                if (results != null && !results.isEmpty()) {
                    cache.put(cacheKey, results);
                }

                return results;

            } catch (Exception e) {
                logger.warning("Attempt " + (attempt + 1) + " failed: " + e.getMessage());

                if (attempt == maxRetries) {
                    logger.severe("All retries exhausted for query: " + query);
                    throw new RuntimeException("YouTube API failed after " + maxRetries + " retries", e);
                }

                // Exponential backoff
                try {
                    Thread.sleep((long) Math.pow(2, attempt) * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Course> searchWithRetry(String query, int maxResults) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String urlString = String.format(
                "%s/search?part=snippet&q=%s&type=video&videoDuration=medium&maxResults=%d&key=%s",
                baseUrl, encodedQuery, maxResults, apiKey
        );

        logger.info("Searching YouTube: " + query);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(timeoutMs);
        conn.setReadTimeout(timeoutMs);

        int responseCode = conn.getResponseCode();

        if (responseCode == 403) {
            throw new RuntimeException("API quota exceeded or key invalid");
        }
        if (responseCode != 200) {
            throw new RuntimeException("HTTP error: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return parseYouTubeResponse(response.toString(), query);
    }

    private List<Course> parseYouTubeResponse(String jsonResponse, String originalQuery) {
        List<Course> courses = new ArrayList<>();

        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");

            if (items == null || items.size() == 0) {
                logger.info("No results found for: " + originalQuery);
                return courses;
            }

            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();
                JsonObject snippet = item.getAsJsonObject("snippet");
                JsonObject id = item.getAsJsonObject("id");

                String videoId = id.get("videoId").getAsString();
                String title = snippet.get("title").getAsString();
                String channelTitle = snippet.get("channelTitle").getAsString();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                // Duration is estimated from title keywords (full lookup requires a separate API call)
                int estimatedHours = estimateDuration(title);
                Set<String> skills = extractSkills(originalQuery);

                courses.add(new Course(
                        "YT-" + videoId,
                        cleanTitle(title),
                        "YouTube - " + channelTitle,
                        videoUrl,
                        estimatedHours,
                        skills
                ));
            }

            logger.info("Found " + courses.size() + " courses from YouTube");

        } catch (Exception e) {
            logger.severe("Error parsing YouTube response: " + e.getMessage());
        }

        return courses;
    }

    /** Remove brackets and extra whitespace from video titles. */
    private String cleanTitle(String title) {
        return title.replaceAll("[\\[\\](){}|]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /** Estimate course duration in hours based on title keywords. */
    private int estimateDuration(String title) {
        String lower = title.toLowerCase();
        if (lower.contains("crash course") || lower.contains("complete")) return 15;
        if (lower.contains("full") || lower.contains("tutorial")) return 12;
        if (lower.contains("quick") || lower.contains("intro")) return 8;
        return 10;
    }

    /** Extract skill name from the search query string. */
    private Set<String> extractSkills(String query) {
        Set<String> skills = new HashSet<>();
        String cleaned = query.toLowerCase()
                .replaceAll("tutorial|beginner|course|complete|full", "")
                .trim();
        if (!cleaned.isEmpty()) {
            skills.add(capitalize(cleaned));
        }
        return skills;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /** Test API connectivity by running a minimal search. */
    public boolean testConnection() {
        try {
            List<Course> results = searchCourses("Java", 1);
            return !results.isEmpty();
        } catch (Exception e) {
            logger.warning("API connection test failed: " + e.getMessage());
            return false;
        }
    }
}
