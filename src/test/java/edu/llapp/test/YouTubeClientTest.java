package edu.llapp.test;

import edu.llapp.domain.Course;
import edu.llapp.infra.YouTubeClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
// import org.junit.jupiter.api.Disabled;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * YouTube API Testing
 * Note: These tests call the real API and consume quota.
 */
@Tag("integration")
public class YouTubeClientTest {

    @Test
    // @Disabled("Run manually only (consumes quota)")
    public void testSearchCourses() {
        YouTubeClient client = new YouTubeClient();
        List<Course> courses = client.searchCourses("Python", 5);

        assertNotNull(courses);
        assertFalse(courses.isEmpty());
    }

    @Test
    // @Disabled("Run manually only (network dependent)")
    public void testConnectionCheck() {
        YouTubeClient client = new YouTubeClient();
        boolean connected = client.testConnection();

        assertTrue(connected, "YouTube API should be reachable");
    }
}
