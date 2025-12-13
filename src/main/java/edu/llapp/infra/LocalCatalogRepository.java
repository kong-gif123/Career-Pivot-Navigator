package edu.llapp.infra;

import edu.llapp.domain.Course;
import java.util.*;

public class LocalCatalogRepository {
    private List<Course> courses;

    public LocalCatalogRepository() {
        this.courses = initializeCourses();
    }

    public List<Course> listAll() {
        return new ArrayList<>(courses);
    }

    public List<Course> searchBySkill(String skillName) {
        List<Course> results = new ArrayList<>();
        for (Course course : courses) {
            if (course.getSkills().stream()
                    .anyMatch(s -> s.equalsIgnoreCase(skillName))) {
                results.add(course);
            }
        }
        return results;
    }

    private List<Course> initializeCourses() {
        List<Course> list = new ArrayList<>();

        // For Quick: Only create 10 courses.
        list.add(new Course("C001", "Python for Beginners", "YouTube", "http://youtube.com/python", 10, Set.of("Python")));
        list.add(new Course("C002", "SQL Fundamentals", "Coursera", "http://coursera.org/sql", 8, Set.of("SQL")));
        list.add(new Course("C003", "Java Basics", "Udemy", "http://udemy.com/java", 12, Set.of("Java")));
        list.add(new Course("C004", "Excel Advanced", "LinkedIn", "http://linkedin.com/excel", 6, Set.of("Excel")));
        list.add(new Course("C005", "Tableau for Data Viz", "YouTube", "http://youtube.com/tableau", 10, Set.of("Tableau")));
        list.add(new Course("C006", "Docker Basics", "YouTube", "http://youtube.com/docker", 8, Set.of("Docker")));
        list.add(new Course("C007", "AWS Fundamentals", "AWS Training", "http://aws.amazon.com", 15, Set.of("AWS")));
        list.add(new Course("C008", "Git & GitHub", "GitHub Lab", "http://lab.github.com", 5, Set.of("Git")));
        list.add(new Course("C009", "Agile & Scrum", "Udemy", "http://udemy.com/agile", 8, Set.of("Agile")));
        list.add(new Course("C010", "UI/UX Design", "Coursera", "http://coursera.org/uiux", 10, Set.of("UI/UX")));

        return list;
    }
}