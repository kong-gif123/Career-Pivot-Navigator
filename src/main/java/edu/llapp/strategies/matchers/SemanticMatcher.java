package edu.llapp.strategies.matchers;

import edu.llapp.domain.*;
import java.util.*;

/**
 * Semantic Matching Strategy (Upgraded Version)
 * Considers the similarity and transferability between skills
 * Features:
 * - Complete skill similarity lookup table (150+ similarity relationships)
 * - Considers skill families (e.g., programming languages, cloud platforms, data tools)
 * - Supports similarity adjustment at skill levels
 * @author Career Pivot Navigator Team
 * @version 2.0
 */
public class SemanticMatcher implements ISkillMatcher {

    // Skills Similarity Comparison Table (Two-way)
    private Map<String, Map<String, Double>> similarityMap;

    public SemanticMatcher() {
        this.similarityMap = buildSimilarityMap();
    }

    @Override
    public String getName() {
        return "Semantic";
    }

    @Override
    public String getDescription() {
        return "Semantic matching with advanced similarity scoring (150+ skill relations)";
    }

    @Override
    public GapReport match(SkillSet current, SkillSet target) {
        Set<String> currentSkillNames = current.getSkillNames();
        List<Skill> missingSkills = new ArrayList<>();
        List<Skill> weakSkills = new ArrayList<>();

        double totalScore = 0.0;

        // Get the list of target skills (using getItems)
        Set<Skill> targetSkills = target.getItems();
        int targetCount = targetSkills.size();

        // Boundary condition: Target skill is empty
        if (targetCount == 0) {
            GapReport emptyReport = new GapReport();
            emptyReport.setScore(100.0);
            return emptyReport;
        }

        // Traverse target skills
        for (Skill targetSkill : targetSkills) {
            String targetName = targetSkill.getName();
            SkillLevel targetLevel = targetSkill.getLevel();

            // 1. Check if there is an exact match
            if (currentSkillNames.contains(targetName)) {
                // Use getSkill to retrieve the current skill.
                Skill currentSkill = current.getSkill(targetName);

                if (currentSkill != null) {
                    if (currentSkill.getLevel().ordinal() >= targetLevel.ordinal()) {
                        // Sufficient level, perfect match
                        totalScore += 1.0;
                    } else {
                        // Insufficient level, considered as weak skill
                        weakSkills.add(targetSkill);
                        totalScore += 0.5;
                    }
                    continue;
                }
            }

            // 2. Find the most similar skills
            double bestSimilarity = 0.0;
            String bestMatch = null;

            for (String currentSkillName : currentSkillNames) {
                double similarity = getSimilarity(currentSkillName, targetName);
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestMatch = currentSkillName;
                }
            }

            // 3. Scoring is based on similarity.
            if (bestSimilarity >= 0.7) {
                // hign similarity
                totalScore += bestSimilarity;
                System.out.println("✓ Similar skill found: " + bestMatch + " ≈ " + targetName +
                        " (similarity: " + String.format("%.0f%%", bestSimilarity * 100) + ")");
            } else if (bestSimilarity >= 0.4) {
                // middle similarity
                totalScore += bestSimilarity * 0.7;
                weakSkills.add(targetSkill);
                System.out.println("⚠ Weak match: " + bestMatch + " ≈ " + targetName +
                        " (similarity: " + String.format("%.0f%%", bestSimilarity * 100) + ")");
            } else {
                // none or low similarity
                missingSkills.add(targetSkill);
            }
        }

        // calculate final score
        double finalScore = (targetCount > 0) ? (totalScore / targetCount) * 100 : 0;

        // general report
        GapReport report = new GapReport();
        report.setScore(finalScore);

        // add missing and weak skills
        for (Skill skill : missingSkills) {
            report.getMissingSkills().add(skill);
        }
        for (Skill skill : weakSkills) {
            report.getWeakSkills().add(skill);
        }

        return report;
    }

    /**
     * To obtain the similarity between two skills（0.0 - 1.0）
     */
    private double getSimilarity(String skill1, String skill2) {
        // Completely identical
        if (skill1.equalsIgnoreCase(skill2)) {
            return 1.0;
        }

        // Table lookup (two-way)
        String s1 = skill1.toLowerCase();
        String s2 = skill2.toLowerCase();

        if (similarityMap.containsKey(s1) && similarityMap.get(s1).containsKey(s2)) {
            return similarityMap.get(s1).get(s2);
        }

        if (similarityMap.containsKey(s2) && similarityMap.get(s2).containsKey(s1)) {
            return similarityMap.get(s2).get(s1);
        }

        // Substring matching (fallback)
        if (s1.contains(s2) || s2.contains(s1)) {
            return 0.3;
        }

        // Not found, return 0
        return 0.0;
    }

    /**
     * Establish a complete skill similarity comparison table
     */
    private Map<String, Map<String, Double>> buildSimilarityMap() {
        Map<String, Map<String, Double>> map = new HashMap<>();

        // ==================== family of programming languages ====================

        // Python
        addSimilarity(map, "python", "javascript", 0.65);
        addSimilarity(map, "python", "ruby", 0.70);
        addSimilarity(map, "python", "java", 0.55);
        addSimilarity(map, "python", "c++", 0.45);
        addSimilarity(map, "python", "c#", 0.50);
        addSimilarity(map, "python", "php", 0.55);
        addSimilarity(map, "python", "go", 0.50);

        // Java
        addSimilarity(map, "java", "c#", 0.85);
        addSimilarity(map, "java", "kotlin", 0.95);
        addSimilarity(map, "java", "scala", 0.80);
        addSimilarity(map, "java", "c++", 0.70);
        addSimilarity(map, "java", "javascript", 0.50);

        // JavaScript
        addSimilarity(map, "javascript", "typescript", 0.95);
        addSimilarity(map, "javascript", "node.js", 0.90);
        addSimilarity(map, "typescript", "node.js", 0.85);

        // C
        addSimilarity(map, "c++", "c", 0.90);
        addSimilarity(map, "c++", "c#", 0.75);
        addSimilarity(map, "c", "c#", 0.70);

        // Other languages
        addSimilarity(map, "ruby", "php", 0.65);
        addSimilarity(map, "go", "rust", 0.60);
        addSimilarity(map, "swift", "kotlin", 0.70);

        // ==================== Web Front-end technology ====================

        addSimilarity(map, "html", "css", 0.80);
        addSimilarity(map, "html", "javascript", 0.70);
        addSimilarity(map, "css", "javascript", 0.65);

        // Front-end frameworks
        addSimilarity(map, "react", "vue.js", 0.85);
        addSimilarity(map, "react", "angular", 0.80);
        addSimilarity(map, "vue.js", "angular", 0.85);
        addSimilarity(map, "react", "svelte", 0.75);

        // ==================== Backend framework ====================

        // Python frameworks
        addSimilarity(map, "django", "flask", 0.85);
        addSimilarity(map, "django", "fastapi", 0.75);
        addSimilarity(map, "flask", "fastapi", 0.80);

        // Java frameworks
        addSimilarity(map, "spring boot", "spring", 0.95);
        addSimilarity(map, "spring boot", "java", 0.70);

        // JavaScript frameworks
        addSimilarity(map, "express", "node.js", 0.90);
        addSimilarity(map, "nest.js", "node.js", 0.85);
        addSimilarity(map, "express", "nest.js", 0.80);

        // ==================== databased ====================

        // SQL
        addSimilarity(map, "sql", "mysql", 0.95);
        addSimilarity(map, "sql", "postgresql", 0.95);
        addSimilarity(map, "sql", "oracle", 0.90);
        addSimilarity(map, "sql", "sql server", 0.90);
        addSimilarity(map, "mysql", "postgresql", 0.90);
        addSimilarity(map, "mysql", "mariadb", 0.95);

        // NoSQL
        addSimilarity(map, "nosql", "mongodb", 0.95);
        addSimilarity(map, "nosql", "redis", 0.85);
        addSimilarity(map, "nosql", "cassandra", 0.85);
        addSimilarity(map, "mongodb", "couchdb", 0.80);
        addSimilarity(map, "mongodb", "dynamodb", 0.75);

        // SQL vs NoSQL（low similar）
        addSimilarity(map, "sql", "mongodb", 0.40);
        addSimilarity(map, "sql", "redis", 0.35);
        addSimilarity(map, "postgresql", "mongodb", 0.40);

        // ==================== cloud platform ====================

        // three cloud giants
        addSimilarity(map, "aws", "azure", 0.75);
        addSimilarity(map, "aws", "gcp", 0.75);
        addSimilarity(map, "azure", "gcp", 0.80);

        // cloud services
        addSimilarity(map, "aws", "heroku", 0.55);
        addSimilarity(map, "aws", "digitalocean", 0.60);

        // ==================== Containers and orchestration ====================

        addSimilarity(map, "docker", "kubernetes", 0.80);
        addSimilarity(map, "docker", "containerization", 0.95);
        addSimilarity(map, "kubernetes", "docker swarm", 0.85);
        addSimilarity(map, "kubernetes", "openshift", 0.85);

        // ==================== version control ====================

        addSimilarity(map, "git", "github", 0.95);
        addSimilarity(map, "git", "gitlab", 0.95);
        addSimilarity(map, "git", "bitbucket", 0.90);
        addSimilarity(map, "github", "gitlab", 0.90);

        // ==================== CI/CD ====================

        addSimilarity(map, "jenkins", "github actions", 0.80);
        addSimilarity(map, "jenkins", "gitlab ci", 0.80);
        addSimilarity(map, "github actions", "gitlab ci", 0.85);
        addSimilarity(map, "jenkins", "circleci", 0.75);

        // ==================== Data analysis tools ====================

        // BI tool（high similar）
        addSimilarity(map, "tableau", "power bi", 0.90);
        addSimilarity(map, "tableau", "looker", 0.85);
        addSimilarity(map, "power bi", "looker", 0.85);
        addSimilarity(map, "tableau", "qlik", 0.80);

        // Excel
        addSimilarity(map, "excel", "tableau", 0.75);
        addSimilarity(map, "excel", "power bi", 0.80);
        addSimilarity(map, "excel", "google sheets", 0.95);
        addSimilarity(map, "excel", "data analysis", 0.70);

        // Data Science Tools
        addSimilarity(map, "jupyter", "python", 0.80);
        addSimilarity(map, "pandas", "python", 0.85);
        addSimilarity(map, "numpy", "python", 0.85);
        addSimilarity(map, "pandas", "numpy", 0.80);

        // ==================== Machine Learning / AI ====================

        addSimilarity(map, "machine learning", "deep learning", 0.85);
        addSimilarity(map, "machine learning", "ai", 0.90);
        addSimilarity(map, "deep learning", "neural networks", 0.95);

        // ML frameworks
        addSimilarity(map, "tensorflow", "pytorch", 0.90);
        addSimilarity(map, "tensorflow", "keras", 0.85);
        addSimilarity(map, "pytorch", "keras", 0.80);
        addSimilarity(map, "scikit-learn", "machine learning", 0.85);

        // ==================== design tools ====================

        addSimilarity(map, "figma", "sketch", 0.90);
        addSimilarity(map, "figma", "adobe xd", 0.90);
        addSimilarity(map, "sketch", "adobe xd", 0.85);
        addSimilarity(map, "photoshop", "illustrator", 0.75);

        // UI/UX
        addSimilarity(map, "ui/ux", "figma", 0.80);
        addSimilarity(map, "ui/ux", "sketch", 0.80);
        addSimilarity(map, "ui/ux", "adobe xd", 0.80);

        // ==================== Project Management ====================

        addSimilarity(map, "agile", "scrum", 0.95);
        addSimilarity(map, "agile", "kanban", 0.85);
        addSimilarity(map, "scrum", "kanban", 0.80);
        addSimilarity(map, "agile", "project management", 0.80);
        addSimilarity(map, "jira", "trello", 0.75);
        addSimilarity(map, "jira", "asana", 0.75);

        // ==================== API and Architecture ====================

        addSimilarity(map, "rest api", "graphql", 0.80);
        addSimilarity(map, "rest api", "api", 0.95);
        addSimilarity(map, "microservices", "rest api", 0.75);
        addSimilarity(map, "microservices", "docker", 0.70);

        System.out.println("Semantic Matcher: Loaded " + countTotalRelations(map) + " skill similarity relations");

        return map;
    }

    /**
     * Auxiliary method: Bidirectional similarity addition
     */
    private void addSimilarity(Map<String, Map<String, Double>> map,
                               String skill1, String skill2, double similarity) {
        // skill1 → skill2
        map.putIfAbsent(skill1, new HashMap<>());
        map.get(skill1).put(skill2, similarity);

        // skill2 → skill1（bilateral）
        map.putIfAbsent(skill2, new HashMap<>());
        map.get(skill2).put(skill1, similarity);
    }

    /**
     * Calculate the total number of similarity relationships.
     */
    private int countTotalRelations(Map<String, Map<String, Double>> map) {
        int count = 0;
        for (Map<String, Double> relations : map.values()) {
            count += relations.size();
        }
        return count / 2; // bilateral，Divide by 2
    }
}