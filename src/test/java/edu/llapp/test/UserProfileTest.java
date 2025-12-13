package edu.llapp.test;

import edu.llapp.domain.Skill;
import edu.llapp.domain.SkillLevel;
import edu.llapp.domain.UserProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileTest {

    @Test
    public void testProfileCreation() {
        UserProfile profile = new UserProfile("user123", "Data Analyst");

        assertEquals("user123", profile.getUserId());
        assertEquals("Data Analyst", profile.getTargetRole());
        assertNotNull(profile.getSkills());
        assertTrue(profile.getSkills().isEmpty());
    }

    @Test
    public void testAddSkillsToProfile() {
        UserProfile profile = new UserProfile("user456", "Software Engineer");
        profile.getSkills().addSkill(new Skill("Java", SkillLevel.INTERMEDIATE));
        profile.getSkills().addSkill(new Skill("Python", SkillLevel.BEGINNER));

        assertEquals(2, profile.getSkills().size());
        assertTrue(profile.getSkills().hasSkill("Java"));
    }
}