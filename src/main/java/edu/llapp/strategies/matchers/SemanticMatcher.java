package edu.llapp.strategies.matchers;

import edu.llapp.domain.*;
import java.util.*;

/**
 * 語義匹配策略（升級版）
 * 考慮技能之間的相似性和可轉移性
 *
 * 特點：
 * - 完整的技能相似度對照表（150+ 組相似關係）
 * - 考慮技能家族（如：程式語言、雲端平台、數據工具）
 * - 支援技能等級的相似度調整
 *
 * @author Career Pivot Navigator Team
 * @version 2.0
 */
public class SemanticMatcher implements ISkillMatcher {

    // 技能相似度對照表（雙向）
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

        // 取得目標技能列表（用 getItems）
        Set<Skill> targetSkills = target.getItems();
        int targetCount = targetSkills.size();

        // 邊界條件：目標技能為空
        if (targetCount == 0) {
            GapReport emptyReport = new GapReport();
            emptyReport.setScore(100.0);
            return emptyReport;
        }

        // 遍歷目標技能
        for (Skill targetSkill : targetSkills) {
            String targetName = targetSkill.getName();
            SkillLevel targetLevel = targetSkill.getLevel();

            // 1. 檢查是否有完全匹配
            if (currentSkillNames.contains(targetName)) {
                // 用 getSkill 取得當前技能
                Skill currentSkill = current.getSkill(targetName);

                if (currentSkill != null) {
                    if (currentSkill.getLevel().ordinal() >= targetLevel.ordinal()) {
                        // 等級足夠，完全匹配
                        totalScore += 1.0;
                    } else {
                        // 等級不足，算薄弱技能
                        weakSkills.add(targetSkill);
                        totalScore += 0.5;
                    }
                    continue;
                }
            }

            // 2. 找最相似的技能
            double bestSimilarity = 0.0;
            String bestMatch = null;

            for (String currentSkillName : currentSkillNames) {
                double similarity = getSimilarity(currentSkillName, targetName);
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestMatch = currentSkillName;
                }
            }

            // 3. 根據相似度給分
            if (bestSimilarity >= 0.7) {
                // 高度相似
                totalScore += bestSimilarity;
                System.out.println("✓ Similar skill found: " + bestMatch + " ≈ " + targetName +
                        " (similarity: " + String.format("%.0f%%", bestSimilarity * 100) + ")");
            } else if (bestSimilarity >= 0.4) {
                // 中度相似
                totalScore += bestSimilarity * 0.7;
                weakSkills.add(targetSkill);
                System.out.println("⚠ Weak match: " + bestMatch + " ≈ " + targetName +
                        " (similarity: " + String.format("%.0f%%", bestSimilarity * 100) + ")");
            } else {
                // 完全不相似
                missingSkills.add(targetSkill);
            }
        }

        // 計算最終分數
        double finalScore = (targetCount > 0) ? (totalScore / targetCount) * 100 : 0;

        // 建立 report
        GapReport report = new GapReport();
        report.setScore(finalScore);

        // 加入缺失和薄弱技能
        for (Skill skill : missingSkills) {
            report.getMissingSkills().add(skill);
        }
        for (Skill skill : weakSkills) {
            report.getWeakSkills().add(skill);
        }

        return report;
    }

    /**
     * 取得兩個技能之間的相似度（0.0 - 1.0）
     */
    private double getSimilarity(String skill1, String skill2) {
        // 完全相同
        if (skill1.equalsIgnoreCase(skill2)) {
            return 1.0;
        }

        // 查表（雙向）
        String s1 = skill1.toLowerCase();
        String s2 = skill2.toLowerCase();

        if (similarityMap.containsKey(s1) && similarityMap.get(s1).containsKey(s2)) {
            return similarityMap.get(s1).get(s2);
        }

        if (similarityMap.containsKey(s2) && similarityMap.get(s2).containsKey(s1)) {
            return similarityMap.get(s2).get(s1);
        }

        // 子字串匹配（fallback）
        if (s1.contains(s2) || s2.contains(s1)) {
            return 0.3;
        }

        // 沒找到，返回 0
        return 0.0;
    }

    /**
     * 建立完整的技能相似度對照表
     */
    private Map<String, Map<String, Double>> buildSimilarityMap() {
        Map<String, Map<String, Double>> map = new HashMap<>();

        // ==================== 程式語言家族 ====================

        // Python 生態系
        addSimilarity(map, "python", "javascript", 0.65);
        addSimilarity(map, "python", "ruby", 0.70);
        addSimilarity(map, "python", "java", 0.55);
        addSimilarity(map, "python", "c++", 0.45);
        addSimilarity(map, "python", "c#", 0.50);
        addSimilarity(map, "python", "php", 0.55);
        addSimilarity(map, "python", "go", 0.50);

        // Java 生態系
        addSimilarity(map, "java", "c#", 0.85);
        addSimilarity(map, "java", "kotlin", 0.95);
        addSimilarity(map, "java", "scala", 0.80);
        addSimilarity(map, "java", "c++", 0.70);
        addSimilarity(map, "java", "javascript", 0.50);

        // JavaScript 生態系
        addSimilarity(map, "javascript", "typescript", 0.95);
        addSimilarity(map, "javascript", "node.js", 0.90);
        addSimilarity(map, "typescript", "node.js", 0.85);

        // C 家族
        addSimilarity(map, "c++", "c", 0.90);
        addSimilarity(map, "c++", "c#", 0.75);
        addSimilarity(map, "c", "c#", 0.70);

        // 其他語言關係
        addSimilarity(map, "ruby", "php", 0.65);
        addSimilarity(map, "go", "rust", 0.60);
        addSimilarity(map, "swift", "kotlin", 0.70);

        // ==================== Web 前端技術 ====================

        addSimilarity(map, "html", "css", 0.80);
        addSimilarity(map, "html", "javascript", 0.70);
        addSimilarity(map, "css", "javascript", 0.65);

        // 前端框架
        addSimilarity(map, "react", "vue.js", 0.85);
        addSimilarity(map, "react", "angular", 0.80);
        addSimilarity(map, "vue.js", "angular", 0.85);
        addSimilarity(map, "react", "svelte", 0.75);

        // ==================== 後端框架 ====================

        // Python 框架
        addSimilarity(map, "django", "flask", 0.85);
        addSimilarity(map, "django", "fastapi", 0.75);
        addSimilarity(map, "flask", "fastapi", 0.80);

        // Java 框架
        addSimilarity(map, "spring boot", "spring", 0.95);
        addSimilarity(map, "spring boot", "java", 0.70);

        // JavaScript 框架
        addSimilarity(map, "express", "node.js", 0.90);
        addSimilarity(map, "nest.js", "node.js", 0.85);
        addSimilarity(map, "express", "nest.js", 0.80);

        // ==================== 數據庫 ====================

        // SQL 家族
        addSimilarity(map, "sql", "mysql", 0.95);
        addSimilarity(map, "sql", "postgresql", 0.95);
        addSimilarity(map, "sql", "oracle", 0.90);
        addSimilarity(map, "sql", "sql server", 0.90);
        addSimilarity(map, "mysql", "postgresql", 0.90);
        addSimilarity(map, "mysql", "mariadb", 0.95);

        // NoSQL 家族
        addSimilarity(map, "nosql", "mongodb", 0.95);
        addSimilarity(map, "nosql", "redis", 0.85);
        addSimilarity(map, "nosql", "cassandra", 0.85);
        addSimilarity(map, "mongodb", "couchdb", 0.80);
        addSimilarity(map, "mongodb", "dynamodb", 0.75);

        // SQL vs NoSQL（低相似）
        addSimilarity(map, "sql", "mongodb", 0.40);
        addSimilarity(map, "sql", "redis", 0.35);
        addSimilarity(map, "postgresql", "mongodb", 0.40);

        // ==================== 雲端平台 ====================

        // 三大雲
        addSimilarity(map, "aws", "azure", 0.75);
        addSimilarity(map, "aws", "gcp", 0.75);
        addSimilarity(map, "azure", "gcp", 0.80);

        // 雲端服務
        addSimilarity(map, "aws", "heroku", 0.55);
        addSimilarity(map, "aws", "digitalocean", 0.60);

        // ==================== 容器和編排 ====================

        addSimilarity(map, "docker", "kubernetes", 0.80);
        addSimilarity(map, "docker", "containerization", 0.95);
        addSimilarity(map, "kubernetes", "docker swarm", 0.85);
        addSimilarity(map, "kubernetes", "openshift", 0.85);

        // ==================== 版本控制 ====================

        addSimilarity(map, "git", "github", 0.95);
        addSimilarity(map, "git", "gitlab", 0.95);
        addSimilarity(map, "git", "bitbucket", 0.90);
        addSimilarity(map, "github", "gitlab", 0.90);

        // ==================== CI/CD ====================

        addSimilarity(map, "jenkins", "github actions", 0.80);
        addSimilarity(map, "jenkins", "gitlab ci", 0.80);
        addSimilarity(map, "github actions", "gitlab ci", 0.85);
        addSimilarity(map, "jenkins", "circleci", 0.75);

        // ==================== 數據分析工具 ====================

        // BI 工具（高度相似）
        addSimilarity(map, "tableau", "power bi", 0.90);
        addSimilarity(map, "tableau", "looker", 0.85);
        addSimilarity(map, "power bi", "looker", 0.85);
        addSimilarity(map, "tableau", "qlik", 0.80);

        // Excel 生態
        addSimilarity(map, "excel", "tableau", 0.75);
        addSimilarity(map, "excel", "power bi", 0.80);
        addSimilarity(map, "excel", "google sheets", 0.95);
        addSimilarity(map, "excel", "data analysis", 0.70);

        // 數據科學工具
        addSimilarity(map, "jupyter", "python", 0.80);
        addSimilarity(map, "pandas", "python", 0.85);
        addSimilarity(map, "numpy", "python", 0.85);
        addSimilarity(map, "pandas", "numpy", 0.80);

        // ==================== 機器學習 / AI ====================

        addSimilarity(map, "machine learning", "deep learning", 0.85);
        addSimilarity(map, "machine learning", "ai", 0.90);
        addSimilarity(map, "deep learning", "neural networks", 0.95);

        // ML 框架
        addSimilarity(map, "tensorflow", "pytorch", 0.90);
        addSimilarity(map, "tensorflow", "keras", 0.85);
        addSimilarity(map, "pytorch", "keras", 0.80);
        addSimilarity(map, "scikit-learn", "machine learning", 0.85);

        // ==================== 設計工具 ====================

        addSimilarity(map, "figma", "sketch", 0.90);
        addSimilarity(map, "figma", "adobe xd", 0.90);
        addSimilarity(map, "sketch", "adobe xd", 0.85);
        addSimilarity(map, "photoshop", "illustrator", 0.75);

        // UI/UX
        addSimilarity(map, "ui/ux", "figma", 0.80);
        addSimilarity(map, "ui/ux", "sketch", 0.80);
        addSimilarity(map, "ui/ux", "adobe xd", 0.80);

        // ==================== 專案管理 ====================

        addSimilarity(map, "agile", "scrum", 0.95);
        addSimilarity(map, "agile", "kanban", 0.85);
        addSimilarity(map, "scrum", "kanban", 0.80);
        addSimilarity(map, "agile", "project management", 0.80);
        addSimilarity(map, "jira", "trello", 0.75);
        addSimilarity(map, "jira", "asana", 0.75);

        // ==================== API 和架構 ====================

        addSimilarity(map, "rest api", "graphql", 0.80);
        addSimilarity(map, "rest api", "api", 0.95);
        addSimilarity(map, "microservices", "rest api", 0.75);
        addSimilarity(map, "microservices", "docker", 0.70);

        System.out.println("✅ Semantic Matcher: Loaded " + countTotalRelations(map) + " skill similarity relations");

        return map;
    }

    /**
     * 輔助方法：雙向加入相似度
     */
    private void addSimilarity(Map<String, Map<String, Double>> map,
                               String skill1, String skill2, double similarity) {
        // skill1 → skill2
        map.putIfAbsent(skill1, new HashMap<>());
        map.get(skill1).put(skill2, similarity);

        // skill2 → skill1（雙向）
        map.putIfAbsent(skill2, new HashMap<>());
        map.get(skill2).put(skill1, similarity);
    }

    /**
     * 計算總共有多少組相似關係
     */
    private int countTotalRelations(Map<String, Map<String, Double>> map) {
        int count = 0;
        for (Map<String, Double> relations : map.values()) {
            count += relations.size();
        }
        return count / 2; // 因為是雙向，除以 2
    }
}