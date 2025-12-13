package edu.llapp.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * study path class
 * includes multiple PlanStep
 */
public class LearningPath {
    private List<PlanStep> steps;

    public LearningPath() {
        this.steps = new ArrayList<>();
    }

    public List<PlanStep> getSteps() {
        return steps;
    }

    public void setSteps(List<PlanStep> steps) {
        this.steps = steps;
    }

    public void addStep(PlanStep step) {
        steps.add(step);
    }

    public int getTotalHours() {
        return steps.stream()
                .mapToInt(PlanStep::getEstHours)
                .sum();
    }

    public int size() {
        return steps.size();
    }

    @Override
    public String toString() {
        return "LearningPath{" + steps.size() + " steps, " + getTotalHours() + " hours total}";
    }
}