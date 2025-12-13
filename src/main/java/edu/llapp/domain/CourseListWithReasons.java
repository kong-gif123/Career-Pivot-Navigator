package edu.llapp.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * a list of courses with reasons
 * each course has a corresponding reason
 */
public class CourseListWithReasons {
    private List<Course> courses;
    private List<Reason> reasons;

    public CourseListWithReasons() {
        this.courses = new ArrayList<>();
        this.reasons = new ArrayList<>();
    }

    public CourseListWithReasons(List<Course> courses, List<Reason> reasons) {
        this.courses = courses;
        this.reasons = reasons;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Reason> getReasons() {
        return reasons;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void setReasons(List<Reason> reasons) {
        this.reasons = reasons;
    }

    public void addCourse(Course course, Reason reason) {
        courses.add(course);
        reasons.add(reason);
    }

    public int size() {
        return courses.size();
    }

    @Override
    public String toString() {
        return "CourseListWithReasons{" + courses.size() + " courses}";
    }
}