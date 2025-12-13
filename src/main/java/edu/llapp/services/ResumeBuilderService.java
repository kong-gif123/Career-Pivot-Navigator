package edu.llapp.services;

import edu.llapp.domain.*;

/**
 * Resume Generation Service
 * Convert learning outcomes into resume bullet points
 */
public class ResumeBuilderService {

    public ResumeBullets build(UserProfile profile, LearningPath path) {
        ResumeBullets bullets = new ResumeBullets();

        // Generate bullet points for each completed course.
        for (PlanStep step : path.getSteps()) {
            String bullet = String.format("Completed %s (%d hours of training in %s)",
                    step.getCourse().getTitle(),
                    step.getEstHours(),
                    String.join(", ", step.getCourse().getSkills()));
            bullets.addItem(bullet);
        }

        return bullets;
    }
}