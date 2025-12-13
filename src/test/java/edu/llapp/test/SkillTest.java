package edu.llapp.test;

import edu.llapp.domain.Skill;
import edu.llapp.domain.SkillLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SkillTest {

    @Test
    public void testSkillCreation() {
        Skill skill = new Skill("Java", SkillLevel.INTERMEDIATE);

        assertEquals("Java", skill.getName());
        assertEquals(SkillLevel.INTERMEDIATE, skill.getLevel());
        assertNull(skill.getEscoUri());
    }

    @Test
    public void testSkillWithEscoUri() {
        Skill skill = new Skill("Python", SkillLevel.BEGINNER, "http://esco.uri/python");

        assertEquals("Python", skill.getName());
        assertEquals("http://esco.uri/python", skill.getEscoUri());
    }

    @Test
    public void testSkillEquality() {
        Skill skill1 = new Skill("SQL", SkillLevel.ADVANCED);
        Skill skill2 = new Skill("SQL", SkillLevel.ADVANCED);
        Skill skill3 = new Skill("SQL", SkillLevel.BEGINNER);

        assertEquals(skill1, skill2);  // name and level are the same
        assertNotEquals(skill1, skill3);  // level is different
    }
}