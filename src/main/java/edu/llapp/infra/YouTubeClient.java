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

/**
 * YouTube Data API v3 client
 * Search for instructional videos and convert them into course objects.
 */
public class YouTubeClient {
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
     * search courses
     * @param skillName skill name（eg "Python"）
     * @param maxResults requests can be returned at most（eg 5-10）
     * @return course list
     */

    public List<Course> searchCourses(String skillName, int maxResults) {
        String cacheKey = skillName + "_" + maxResults;

        // check Cache first
        List<Course> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;  // cache hit, return directly
        }
        // ===== Cache check done=====

        // Constructing Enhanced Queries
        String query = skillName + " tutorial beginner";

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                List<Course> results = searchWithRetry(query, maxResults);  // ← original

                // ===== add：Deposit into Cache =====
                if (results != null && !results.isEmpty()) {
                    cache.put(cacheKey, results);
                }
                // ===== caches storage complete =====

                return results;  // ← original

            } catch (Exception e) {
                System.err.println("Attempt " + (attempt + 1) + " failed: " + e.getMessage());

                if (attempt == maxRetries) {
                    System.err.println("All retries failed for query: " + query);
                    throw new RuntimeException("YouTube API failed after " + maxRetries + " retries", e);
                }

                // exponential backoff
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
        // Constructing URL
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String urlString = String.format(
                "%s/search?part=snippet&q=%s&type=video&videoDuration=medium&maxResults=%d&key=%s",
                baseUrl, encodedQuery, maxResults, apiKey
        );

        System.out.println("Searching YouTube: " + query);

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
            throw new RuntimeException("HTTP error code: " + responseCode);
        }

        // Read response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Parsing JSON
        return parseYouTubeResponse(response.toString(), query);
    }

    private List<Course> parseYouTubeResponse(String jsonResponse, String originalQuery) {
        List<Course> courses = new ArrayList<>();

        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");

            if (items == null || items.size() == 0) {
                System.out.println("No results found for: " + originalQuery);
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

                // Estimated duration (YouTube API requires an additional request, so this is simplified to a fixed value).
                int estimatedHours = estimateDuration(title);

                // Extracting skills from queries
                Set<String> skills = extractSkills(originalQuery);

                Course course = new Course(
                        "YT-" + videoId,
                        cleanTitle(title),
                        "YouTube - " + channelTitle,
                        videoUrl,
                        estimatedHours,
                        skills
                );

                courses.add(course);
            }

            System.out.println("Found " + courses.size() + " courses from YouTube");

        } catch (Exception e) {
            System.err.println("Error parsing YouTube response: " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Clean up the title (remove unnecessary symbols)
     */
    private String cleanTitle(String title) {
        return title.replaceAll("[\\[\\](){}|]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Estimating duration from the title
     */
    private int estimateDuration(String title) {
        String lower = title.toLowerCase();

        if (lower.contains("crash course") || lower.contains("complete")) {
            return 15;
        } else if (lower.contains("full") || lower.contains("tutorial")) {
            return 12;
        } else if (lower.contains("quick") || lower.contains("intro")) {
            return 8;
        }

        return 10; // Preset 10 hours
    }

    /**
     * Skills for extracting query strings
     */
    private Set<String> extractSkills(String query) {
        Set<String> skills = new HashSet<>();

        // Remove common tutorial keywords
        String cleaned = query.toLowerCase()
                .replaceAll("tutorial|beginner|course|complete|full", "")
                .trim();

        if (!cleaned.isEmpty()) {
            skills.add(capitalize(cleaned));
        }

        return skills;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Test API connection
     */
    public boolean testConnection() {
        try {
            List<Course> results = searchCourses("Java", 1);
            return !results.isEmpty();
        } catch (Exception e) {
            System.err.println("API connection test failed: " + e.getMessage());
            return false;
        }
    }
}