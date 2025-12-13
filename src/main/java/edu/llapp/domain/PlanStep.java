package edu.llapp.domain;

/**
 * a step in a learning plan
 * includes course, order, estimated hours
 */
public class PlanStep {
    private Course course;
    private int order;         // which step in the plan(start from `1`)
    private int estHours;      // expected hours to complete this step

    public PlanStep(Course course, int order, int estHours) {
        this.course = course;
        this.order = order;
        this.estHours = estHours;
    }

    public Course getCourse() {
        return course;
    }

    public int getOrder() {
        return order;
    }

    public int getEstHours() {
        return estHours;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setEstHours(int estHours) {
        this.estHours = estHours;
    }

    @Override
    public String toString() {
        return "Step " + order + ": " + course.getTitle() + " (" + estHours + "h)";
    }
}