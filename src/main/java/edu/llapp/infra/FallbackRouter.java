package edu.llapp.infra;

import edu.llapp.domain.Course;
import java.util.List;
import java.util.logging.Logger;

/**
 * Fallback router that serves courses from the local catalog
 * when the external API is unavailable or disabled.
 */
public class FallbackRouter {
    private static final Logger logger = Logger.getLogger(FallbackRouter.class.getName());

    private LocalCatalogRepository localCatalog;
    private boolean fallbackEnabled;

    public FallbackRouter(LocalCatalogRepository localCatalog) {
        this.localCatalog = localCatalog;
        this.fallbackEnabled = ConfigLoader.isFallbackEnabled();
    }

    /**
     * Retrieve courses from the local catalog matching the given skill.
     */
    public List<Course> getLocalCourses(String skillName) {
        if (!fallbackEnabled) {
            logger.info("Fallback is disabled");
            return List.of();
        }

        logger.info("Falling back to local catalog for: " + skillName);
        List<Course> courses = localCatalog.searchBySkill(skillName);
        logger.info("Found " + courses.size() + " courses in local catalog");
        return courses;
    }

    /**
     * Retrieve all courses from the local catalog.
     */
    public List<Course> getAllLocalCourses() {
        if (!fallbackEnabled) {
            logger.info("Fallback is disabled");
            return List.of();
        }

        logger.info("Loading all courses from local catalog");
        return localCatalog.listAll();
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    public void setFallbackEnabled(boolean enabled) {
        this.fallbackEnabled = enabled;
        logger.info("Fallback " + (enabled ? "enabled" : "disabled"));
    }
}
