package edu.llapp.infra;

import edu.llapp.domain.Course;
import java.util.List;

/**
 * Backup Router
 * Switch to local data when API fails
 */
public class FallbackRouter {
    private LocalCatalogRepository localCatalog;
    private boolean fallbackEnabled;

    public FallbackRouter(LocalCatalogRepository localCatalog) {
        this.localCatalog = localCatalog;
        this.fallbackEnabled = ConfigLoader.isFallbackEnabled();
    }

    /**
     * Get courses from the local catalog (search by skill)
     */
    public List<Course> getLocalCourses(String skillName) {
        if (!fallbackEnabled) {
            System.out.println("Fallback is disabled");
            return List.of();
        }

        System.out.println("Falling back to local catalog for: " + skillName);
        List<Course> courses = localCatalog.searchBySkill(skillName);
        System.out.println("Found " + courses.size() + " courses in local catalog");

        return courses;
    }

    /**
     * Retrieve all courses from the local directory
     */
    public List<Course> getAllLocalCourses() {
        if (!fallbackEnabled) {
            System.out.println("Fallback is disabled");
            return List.of();
        }

        System.out.println("Falling back to local catalog (all courses)");
        return localCatalog.listAll();
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    public void setFallbackEnabled(boolean enabled) {
        this.fallbackEnabled = enabled;
        System.out.println("Fallback " + (enabled ? "enabled" : "disabled"));
    }
}