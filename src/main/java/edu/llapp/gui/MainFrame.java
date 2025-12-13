package edu.llapp.gui;

import edu.llapp.domain.*;
import edu.llapp.services.*;
import edu.llapp.strategies.matchers.*;
import edu.llapp.strategies.recommenders.*;
import edu.llapp.infra.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Main Window - Career Pivot Navigator
 * Provides comprehensive skills analysis, course recommendations, and learning path generation functions
 * Main Functions:
 * 1. Users can select or input their own skills
 * 2. Analyze skill gaps (compared to target roles)
 * 3. Recommend courses (using YouTube API or local directory)
 * 4. Generate resume bullet points
 */
public class MainFrame extends JFrame {

    // ==================== Services ====================
    private AppController controller;
    private StrategyRegistry registry;
    private LocalCatalogRepository catalog;
    private ApiClient apiClient;

    // ==================== Current status ====================
    private UserProfile currentUser;
    private SkillSet targetSkills;
    private GapReport currentGap;
    private LearningPath currentPath;

    // ==================== UI Components ====================
    // Settings Selection
    private JComboBox<String> roleSelector;
    private JComboBox<String> matcherSelector;
    private JComboBox<String> recommenderSelector;
    private JCheckBox apiToggle;

    // skill selection
    private JList<String> availableSkillsList;
    private DefaultListModel<String> selectedSkillsModel;
    private JList<String> selectedSkillsList;
    private JRadioButton beginnerRadio;
    private JRadioButton intermediateRadio;
    private JRadioButton advancedRadio;

    // Results display and operation buttons
    private JTextArea resultArea;
    private JButton analyzeButton;
    private JButton recommendButton;
    private JButton exportButton;

    // ==================== Construct ====================
    public MainFrame() {
        initializeServices();
        initializeUI();
        initializeData();
    }

    // ==================== Initialization method ====================

    /**
     * Initialize all services
     */
    private void initializeServices() {
        registry = new StrategyRegistry();
        catalog = new LocalCatalogRepository();
        apiClient = new ApiClient(true);  // Enabled API by default

        SkillMatcherService matcherService = new SkillMatcherService(registry.getMatcher("keyword"));
        CourseRecommenderService recommenderService = new CourseRecommenderService(registry.getRecommender("popularity"));
        PathGeneratorService pathGenerator = new PathGeneratorService();
        ResumeBuilderService resumeBuilder = new ResumeBuilderService();

        controller = new AppController(matcherService, recommenderService, pathGenerator, resumeBuilder);
    }

    /**
     * initialization UI
     */
    private void initializeUI() {
        setTitle("Career Pivot Navigator");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Top: Title
        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);

        // Left side: Settings + Skill Selection
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(createSettingsPanel(), BorderLayout.NORTH);
        leftPanel.add(createSkillInputPanel(), BorderLayout.CENTER);

        // Right side: Results display + button
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(createResultPanel(), BorderLayout.CENTER);
        rightPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        // Use SplitPane to divide into left and right.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(480);
        splitPane.setResizeWeight(0.4);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Initialize data (create a blank user)
     */
    private void initializeData() {
        currentUser = new UserProfile("user_001", "Data Analyst");

        resultArea.setText("Welcome to Career Pivot Navigator!\n\n" +
                "Getting Started:\n" +
                "  1. Select skills from 'Available Skills' on the left\n" +
                "  2. Choose skill level and click 'Add →'\n" +
                "  3. Or use Quick Presets for common profiles\n" +
                "  4. Select your target role and strategies\n" +
                "  5. Click 'Analyze Gap' to see what you need to learn\n" +
                "  6. Click 'Recommend Courses' to get your learning path\n\n" +
                "Tip: Try the 'Data Analyst' or 'Developer' preset to get started quickly!");
    }

    // ==================== UI panel creation method ====================

    /**
     * Create a title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Career Pivot Navigator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel subtitleLabel = new JLabel("Build your skills, analyze gaps, find courses, create your career path");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(102, 102, 102));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        panel.add(titlePanel, BorderLayout.WEST);
        return panel;
    }

    /**
     * Create a settings panel (target role, strategy selection, API on/off switch).
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JPanel settingsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Analysis Settings",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        // API switch
        settingsPanel.add(new JLabel("Use Real API:"));
        apiToggle = new JCheckBox("", true);
        apiToggle.addActionListener(e -> {
            apiClient.setUseApi(apiToggle.isSelected());
            JOptionPane.showMessageDialog(this,
                    "API mode: " + (apiToggle.isSelected() ? "ENABLED (YouTube)" : "DISABLED (Local only)"),
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        settingsPanel.add(apiToggle);

        // Target Role
        settingsPanel.add(new JLabel("Target Role:"));
        roleSelector = new JComboBox<>(new String[]{
                "Data Analyst",
                "Software Engineer",
                "Product Manager",
                "UI/UX Designer",
                "DevOps Engineer"
        });
        roleSelector.setFont(new Font("Arial", Font.PLAIN, 12));
        settingsPanel.add(roleSelector);

        // Matching strategy
        settingsPanel.add(new JLabel("Matching Strategy:"));
        matcherSelector = new JComboBox<>(new String[]{"keyword", "semantic", "hybrid"});
        matcherSelector.setFont(new Font("Arial", Font.PLAIN, 12));
        settingsPanel.add(matcherSelector);

        // Recommended strategy
        settingsPanel.add(new JLabel("Recommendation:"));
        recommenderSelector = new JComboBox<>(new String[]{"popularity", "personalized", "cost"});
        recommenderSelector.setFont(new Font("Arial", Font.PLAIN, 12));
        settingsPanel.add(recommenderSelector);

        panel.add(settingsPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create a skill input panel
     * Includes: a list of selectable skills, level selection, display of selected skills, and a quick preset button.
     */
    private JPanel createSkillInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Your Current Skills",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        // === Left side: List of optional skills ===
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(Color.WHITE);

        JLabel leftLabel = new JLabel("Available Skills (Select multiple):");
        leftLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        String[] commonSkills = {
                "Java", "Python", "JavaScript", "C++", "C#",
                "SQL", "NoSQL", "MongoDB", "PostgreSQL",
                "Excel", "Tableau", "Power BI", "Data Analysis",
                "Git", "Docker", "Kubernetes", "AWS", "Azure", "GCP",
                "HTML", "CSS", "React", "Angular", "Vue.js",
                "Node.js", "Spring Boot", "Django", "Flask",
                "Machine Learning", "Deep Learning", "TensorFlow",
                "Agile", "Scrum", "Project Management",
                "UI/UX", "Figma", "Adobe XD",
                "REST API", "GraphQL", "Microservices"
        };

        availableSkillsList = new JList<>(commonSkills);
        availableSkillsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        availableSkillsList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane leftScrollPane = new JScrollPane(availableSkillsList);
        leftScrollPane.setPreferredSize(new Dimension(180, 150));

        leftPanel.add(leftLabel, BorderLayout.NORTH);
        leftPanel.add(leftScrollPane, BorderLayout.CENTER);

        // === Middle: Level selection + button ===
        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel levelLabel = new JLabel("Skill Level:");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 11));

        beginnerRadio = new JRadioButton("Beginner", false);
        intermediateRadio = new JRadioButton("Intermediate", true);
        advancedRadio = new JRadioButton("Advanced", false);

        beginnerRadio.setBackground(Color.WHITE);
        intermediateRadio.setBackground(Color.WHITE);
        advancedRadio.setBackground(Color.WHITE);

        ButtonGroup levelGroup = new ButtonGroup();
        levelGroup.add(beginnerRadio);
        levelGroup.add(intermediateRadio);
        levelGroup.add(advancedRadio);

        JButton addButton = createStyledButton("Add →", new Color(46, 204, 113));
        addButton.setPreferredSize(new Dimension(100, 30));
        addButton.addActionListener(e -> handleAddSkills());

        JButton removeButton = createStyledButton("← Remove", new Color(231, 76, 60));
        removeButton.setPreferredSize(new Dimension(100, 30));
        removeButton.addActionListener(e -> handleRemoveSkills());

        centerPanel.add(levelLabel);
        centerPanel.add(beginnerRadio);
        centerPanel.add(intermediateRadio);
        centerPanel.add(advancedRadio);
        centerPanel.add(addButton);
        centerPanel.add(removeButton);

        // === Right side: Selected skills displayed ===
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(Color.WHITE);

        JLabel rightLabel = new JLabel("Your Skills:");
        rightLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        selectedSkillsModel = new DefaultListModel<>();
        selectedSkillsList = new JList<>(selectedSkillsModel);
        selectedSkillsList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane rightScrollPane = new JScrollPane(selectedSkillsList);
        rightScrollPane.setPreferredSize(new Dimension(180, 150));

        rightPanel.add(rightLabel, BorderLayout.NORTH);
        rightPanel.add(rightScrollPane, BorderLayout.CENTER);

        // === Combine three panels ===
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // === Bottom: Quick Preset Button ===
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        quickPanel.setBackground(Color.WHITE);

        JLabel quickLabel = new JLabel("Quick Presets:");
        quickLabel.setFont(new Font("Arial", Font.PLAIN, 10));

        JButton preset1 = new JButton("Data Analyst");
        JButton preset2 = new JButton("Developer");
        JButton preset3 = new JButton("Clear All");

        preset1.setFont(new Font("Arial", Font.PLAIN, 10));
        preset2.setFont(new Font("Arial", Font.PLAIN, 10));
        preset3.setFont(new Font("Arial", Font.PLAIN, 10));

        preset1.addActionListener(e -> loadPreset("data_analyst"));
        preset2.addActionListener(e -> loadPreset("developer"));
        preset3.addActionListener(e -> loadPreset("clear"));

        quickPanel.add(quickLabel);
        quickPanel.add(preset1);
        quickPanel.add(preset2);
        quickPanel.add(preset3);

        mainPanel.add(quickPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Create a results display panel
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Results",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create an operation button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);

        analyzeButton = createStyledButton("1. Analyze Gap", new Color(52, 152, 219));
        analyzeButton.addActionListener(e -> handleAnalyze());

        recommendButton = createStyledButton("2. Recommend Courses", new Color(46, 204, 113));
        recommendButton.setEnabled(false);
        recommendButton.addActionListener(e -> handleRecommend());

        exportButton = createStyledButton("3. Export Resume", new Color(155, 89, 182));
        exportButton.setEnabled(false);
        exportButton.addActionListener(e -> handleExport());

        panel.add(analyzeButton);
        panel.add(recommendButton);
        panel.add(exportButton);

        return panel;
    }

    /**
     * Create an operation button panel
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // ==================== Event handling methods ====================

    /**
     * Handling new skills
     */
    private void handleAddSkills() {
        List<String> selected = availableSkillsList.getSelectedValuesList();

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one skill from the list.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Determine skill level
        SkillLevel level = beginnerRadio.isSelected() ? SkillLevel.BEGINNER :
                intermediateRadio.isSelected() ? SkillLevel.INTERMEDIATE :
                        SkillLevel.ADVANCED;

        // Add skills
        for (String skillName : selected) {
            boolean exists = currentUser.getSkills().getSkillNames().stream()
                    .anyMatch(s -> s.equalsIgnoreCase(skillName));

            if (!exists) {
                currentUser.getSkills().addSkill(new Skill(skillName, level));
                selectedSkillsModel.addElement(skillName + " (" + level + ")");
            }
        }

        availableSkillsList.clearSelection();
        System.out.println("Added skills: " + selected + " at level " + level);
    }

    /**
     * Handling removal skills
     */
    private void handleRemoveSkills() {
        List<String> selected = selectedSkillsList.getSelectedValuesList();

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select skills to remove from your list.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extract the skill names to be deleted
        Set<String> toRemove = new HashSet<>();
        for (String item : selected) {
            String skillName = item.split(" \\(")[0];
            toRemove.add(skillName);
        }

        // Rebuild Skills (Keep only not delete)
        SkillSet newSkillSet = new SkillSet();

        for (int i = 0; i < selectedSkillsModel.size(); i++) {
            String modelItem = selectedSkillsModel.get(i);
            String skillName = modelItem.split(" \\(")[0];

            if (!toRemove.contains(skillName)) {
                String levelStr = modelItem.split("\\(")[1].replace(")", "").trim();
                SkillLevel level = SkillLevel.valueOf(levelStr);
                newSkillSet.addSkill(new Skill(skillName, level));
            }
        }

        currentUser.setSkills(newSkillSet);

        // Remove from display list
        for (String item : selected) {
            selectedSkillsModel.removeElement(item);
        }

        System.out.println("Removed skills: " + toRemove);
    }

    /**
     * Load preset skill combinations
     */
    private void loadPreset(String presetName) {
        currentUser.setSkills(new SkillSet());
        selectedSkillsModel.clear();

        switch (presetName) {
            case "data_analyst":
                addPresetSkill("Excel", SkillLevel.ADVANCED);
                addPresetSkill("SQL", SkillLevel.INTERMEDIATE);
                addPresetSkill("Tableau", SkillLevel.INTERMEDIATE);
                addPresetSkill("Python", SkillLevel.BEGINNER);
                JOptionPane.showMessageDialog(this,
                        "Loaded Data Analyst preset!",
                        "Preset Loaded",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case "developer":
                addPresetSkill("Java", SkillLevel.INTERMEDIATE);
                addPresetSkill("JavaScript", SkillLevel.INTERMEDIATE);
                addPresetSkill("SQL", SkillLevel.INTERMEDIATE);
                addPresetSkill("Git", SkillLevel.INTERMEDIATE);
                JOptionPane.showMessageDialog(this,
                        "Loaded Developer preset!",
                        "Preset Loaded",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case "clear":
                JOptionPane.showMessageDialog(this,
                        "All skills cleared!",
                        "Cleared",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
        }

        System.out.println("Loaded preset: " + presetName);
    }

    /**
     * Add preset skills (auxiliary methods)
     */
    private void addPresetSkill(String name, SkillLevel level) {
        currentUser.getSkills().addSkill(new Skill(name, level));
        selectedSkillsModel.addElement(name + " (" + level + ")");
    }

    /**
     * Skills gap analysis
     */
    private void handleAnalyze() {
        try {
            // Check if the user has selected skills
            if (selectedSkillsModel.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add at least one skill before analyzing!\n" +
                                "Use the 'Available Skills' list on the left.",
                        "No Skills Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update target character
            currentUser.setTargetRole((String) roleSelector.getSelectedItem());

            // Set target skills based on the character.
            targetSkills = getTargetSkillsForRole((String) roleSelector.getSelectedItem());

            // Update strategy
            String matcherKey = (String) matcherSelector.getSelectedItem();
            controller.getMatcherService().setStrategy(registry.getMatcher(matcherKey));

            // Execution Analysis
            resultArea.setText("Analyzing skill gaps...\n\n");
            currentGap = controller.getMatcherService().match(currentUser.getSkills(), targetSkills);

            // Display results
            StringBuilder sb = new StringBuilder();
            sb.append("=== SKILL GAP ANALYSIS ===\n\n");

            // Displays user skills (read from the UI list)
            sb.append("Your Skills (").append(selectedSkillsModel.size()).append("):\n");
            for (int i = 0; i < selectedSkillsModel.size(); i++) {
                sb.append("  ✓ ").append(selectedSkillsModel.get(i)).append("\n");
            }
            sb.append("\n");

            sb.append("Target Role: ").append(currentUser.getTargetRole()).append("\n");
            sb.append("Matching Strategy: ").append(matcherKey.toUpperCase()).append("\n");
            sb.append("Match Score: ").append(String.format("%.1f%%", currentGap.getScore())).append("\n\n");

            sb.append("Missing Skills (").append(currentGap.getMissingSkills().size()).append("):\n");
            for (Skill skill : currentGap.getMissingSkills()) {
                sb.append("  ✗ ").append(skill.getName())
                        .append(" (").append(skill.getLevel()).append(")\n");
            }

            if (!currentGap.getWeakSkills().isEmpty()) {
                sb.append("\nWeak Skills (").append(currentGap.getWeakSkills().size()).append("):\n");
                for (Skill skill : currentGap.getWeakSkills()) {
                    sb.append("  ⚠  ").append(skill.getName())
                            .append(" (need ").append(skill.getLevel()).append(")\n");
                }
            }

            sb.append("\n");
            if (currentGap.hasGaps()) {
                sb.append("→ Click 'Recommend Courses' to get personalized learning path\n");
            } else {
                sb.append("✓ Congratulations! You meet all requirements!\n");
            }

            resultArea.setText(sb.toString());
            recommendButton.setEnabled(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during analysis: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Processing course recommendations
     */
    private void handleRecommend() {
        try {
            // Update recommendation strategy
            String recommenderKey = (String) recommenderSelector.getSelectedItem();
            controller.getRecommenderService().setStrategy(registry.getRecommender(recommenderKey));

            // Show loading
            resultArea.setText("Searching courses...\nThis may take a few seconds...\n");

            // Search courses using the API
            List<String> missingSkillNames = currentGap.getMissingSkills().stream()
                    .map(Skill::getName)
                    .collect(java.util.stream.Collectors.toList());

            List<Course> allCourses;
            String dataSourceNote;

            if (!missingSkillNames.isEmpty()) {
                resultArea.append("Searching for: " + String.join(", ", missingSkillNames) + "\n\n");
                allCourses = apiClient.searchMultipleSkills(missingSkillNames, 2);

                // show fallback notice (only when API is enabled and fallback happened)
                if (apiClient.wasLastBatchFallbackUsed()) {
                    resultArea.append("API unavailable for: "
                            + String.join(", ", apiClient.getLastBatchFallbackSkills())
                            + "\nUsing local catalog as fallback.\n\n");
                }

                dataSourceNote = apiClient.getLastBatchNote();
            } else {
                allCourses = catalog.listAll();
                dataSourceNote = "Local catalog (no missing skills)";
            }

            if (allCourses.isEmpty()) {
                resultArea.setText("No courses found. Please check your internet connection or try again later.");
                return;
            }

            // Execution Recommendation
            CourseListWithReasons recommendations = controller.getRecommenderService()
                    .recommend(currentGap, currentUser, allCourses);

            // Generate learning path
            PathGeneratorService pathGen = new PathGeneratorService();
            currentPath = pathGen.generate(recommendations);

            // Display results
            StringBuilder sb = new StringBuilder();
            sb.append("=== RECOMMENDED LEARNING PATH ===\n\n");
            sb.append("Recommendation Strategy: ").append(recommenderKey.toUpperCase()).append("\n");
            sb.append("Data Source: ").append(dataSourceNote).append("\n");
            sb.append("Total Courses: ").append(currentPath.size()).append("\n");
            sb.append("Estimated Time: ").append(currentPath.getTotalHours()).append(" hours\n\n");

            for (PlanStep step : currentPath.getSteps()) {
                Course course = step.getCourse();
                sb.append("Step ").append(step.getOrder()).append(": ")
                        .append(course.getTitle()).append("\n");
                sb.append("  Provider: ").append(course.getProvider()).append("\n");
                sb.append("  Duration: ").append(course.getDurationHours()).append(" hours\n");
                sb.append("  Skills: ").append(String.join(", ", course.getSkills())).append("\n");

                if (course.getUrl() != null && !course.getUrl().isEmpty()) {
                    sb.append("   URL: ").append(course.getUrl()).append("\n");
                }

                if (step.getOrder() - 1 < recommendations.getReasons().size()) {
                    Reason reason = recommendations.getReasons().get(step.getOrder() - 1);
                    sb.append("   Reason: ").append(reason.getMessage()).append("\n");
                }
                sb.append("\n");
            }

            sb.append("→ Click 'Export Resume' to generate resume bullets\n");

            resultArea.setText(sb.toString());
            exportButton.setEnabled(true);

        } catch (Exception e) {
            resultArea.setText(" Error: " + e.getMessage() + "\n\n" +
                    "Possible reasons:\n" +
                    "• No internet connection\n" +
                    "• API quota exceeded\n" +
                    "• API key invalid\n\n" +
                    "Tip: The system will automatically use local courses as fallback.");

            JOptionPane.showMessageDialog(this,
                    "Search failed, but local courses are available.\n" +
                            "Error: " + e.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Processing resume export
     */
    private void handleExport() {
        try {
            ResumeBuilderService resumeBuilder = new ResumeBuilderService();
            ResumeBullets bullets = resumeBuilder.build(currentUser, currentPath);

            StringBuilder sb = new StringBuilder();
            sb.append("=== RESUME BULLETS ===\n\n");
            sb.append("Copy these bullets to your resume:\n\n");

            for (String bullet : bullets.getItems()) {
                sb.append("• ").append(bullet).append("\n\n");
            }

            sb.append("\n=== PLAIN TEXT FORMAT ===\n\n");
            sb.append(bullets.export());

            resultArea.setText(sb.toString());

            JOptionPane.showMessageDialog(this,
                    "Resume bullets generated successfully!\n" +
                            "Copy the text from the results area.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during export: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Acquire target skills based on the character
     */
    private SkillSet getTargetSkillsForRole(String role) {
        SkillSet skills = new SkillSet();

        switch (role) {
            case "Data Analyst":
                skills.addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));
                skills.addSkill(new Skill("SQL", SkillLevel.INTERMEDIATE));
                skills.addSkill(new Skill("Excel", SkillLevel.ADVANCED));
                skills.addSkill(new Skill("Tableau", SkillLevel.INTERMEDIATE));
                break;

            case "Software Engineer":
                skills.addSkill(new Skill("Java", SkillLevel.ADVANCED));
                skills.addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));
                skills.addSkill(new Skill("SQL", SkillLevel.INTERMEDIATE));
                skills.addSkill(new Skill("Git", SkillLevel.INTERMEDIATE));
                break;

            case "Product Manager":
                skills.addSkill(new Skill("Agile", SkillLevel.ADVANCED));
                skills.addSkill(new Skill("Business Analysis", SkillLevel.ADVANCED));
                skills.addSkill(new Skill("SQL", SkillLevel.BEGINNER));
                skills.addSkill(new Skill("Excel", SkillLevel.INTERMEDIATE));
                break;

            case "UI/UX Designer":
                skills.addSkill(new Skill("UI/UX", SkillLevel.ADVANCED));
                skills.addSkill(new Skill("HTML", SkillLevel.INTERMEDIATE));
                skills.addSkill(new Skill("CSS", SkillLevel.INTERMEDIATE));
                break;

            case "DevOps Engineer":
                skills.addSkill(new Skill("Docker", SkillLevel.ADVANCED));
                skills.addSkill(new Skill("AWS", SkillLevel.INTERMEDIATE));
                skills.addSkill(new Skill("Git", SkillLevel.ADVANCED));
                skills.addSkill(new Skill("Python", SkillLevel.INTERMEDIATE));
                break;
        }

        return skills;
    }

    // ==================== Getters (for testing) ====================

    public AppController getController() {
        return controller;
    }

    public SkillMatcherService getMatcherService() {
        return controller.getMatcherService();
    }

    public CourseRecommenderService getRecommenderService() {
        return controller.getRecommenderService();
    }

    // ==================== Main ====================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}