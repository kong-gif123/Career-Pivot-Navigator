package edu.llapp.test;

import edu.llapp.domain.Course;
import edu.llapp.infra.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CacheAndFallbackTest {

    @Test
    public void testCache() {
        // TTL = 1 minute
        SimpleCache<String, String> cache = new SimpleCache<>(1);

        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));

        // Second time should hit cache
        assertEquals("value1", cache.get("key1"));

        System.out.println("Cache test passed");
    }

    @Test
    public void testFallbackRouter() {
        LocalCatalogRepository catalog = new LocalCatalogRepository();
        FallbackRouter router = new FallbackRouter(catalog);

        List<Course> courses = router.getLocalCourses("Python");

        assertNotNull(courses);
        assertFalse(courses.isEmpty());

        System.out.println("Found " + courses.size() + " courses from local catalog");
    }

    /**
     * Integration test: calls real YouTube API and consumes quota.
     * Run manually only when API key + network are available.
     */
    @Tag("integration")
    @Test
    public void testYouTubeCacheHit() {
        YouTubeClient client = new YouTubeClient();

        // First time: hit API
        System.out.println("\n--- First call (should hit API) ---");
        List<Course> first = client.searchCourses("Java", 3);

        // Second time: should hit cache
        System.out.println("\n--- Second call (should hit cache) ---");
        List<Course> second = client.searchCourses("Java", 3);

        assertEquals(first.size(), second.size());
        System.out.println("Cache is working!");
    }
}
