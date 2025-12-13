package edu.llapp.test;

import edu.llapp.domain.Skill;
import edu.llapp.domain.SkillLevel;
import edu.llapp.domain.SkillSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SkillSetTest {

    @Test
    public void testAddAndCheckSkill() {
        SkillSet skillSet = new SkillSet();
        skillSet.addSkill(new Skill("Java", SkillLevel.INTERMEDIATE));

        assertEquals(1, skillSet.size());
        assertTrue(skillSet.hasSkill("Java"));
        assertTrue(skillSet.hasSkill("java"));  // 不分大小寫
        assertFalse(skillSet.hasSkill("Python"));
    }

    @Test
    public void testDuplicateSkills() {
        SkillSet skillSet = new SkillSet();
        Skill java1 = new Skill("Java", SkillLevel.INTERMEDIATE);
        Skill java2 = new Skill("Java", SkillLevel.INTERMEDIATE);

        skillSet.addSkill(java1);
        skillSet.addSkill(java2);

        assertEquals(1, skillSet.size());  // Set 自動去重
    }

    @Test
    public void testGetSkillNames() {
        SkillSet skillSet = new SkillSet();
        skillSet.addSkill(new Skill("Java", SkillLevel.INTERMEDIATE));
        skillSet.addSkill(new Skill("Python", SkillLevel.BEGINNER));
        skillSet.addSkill(new Skill("SQL", SkillLevel.ADVANCED));

        var names = skillSet.getSkillNames();
        assertEquals(3, names.size());
        assertTrue(names.contains("Java"));
        assertTrue(names.contains("Python"));
        assertTrue(names.contains("SQL"));
    }
}