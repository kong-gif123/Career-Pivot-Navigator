package edu.llapp.domain;

/**
 * user profile
 * includes user id, target role, existing skills
 */
public class UserProfile {
    private String userId;
    private String targetRole;     // aim for role
    private SkillSet skills;       // existing skills

    public UserProfile(String userId, String targetRole) {
        this.userId = userId;
        this.targetRole = targetRole;
        this.skills = new SkillSet();  // auto initialize empty skill set
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public SkillSet getSkills() {
        return skills;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public void setSkills(SkillSet skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", targetRole='" + targetRole + '\'' +
                ", skills=" + skills +
                '}';
    }
}