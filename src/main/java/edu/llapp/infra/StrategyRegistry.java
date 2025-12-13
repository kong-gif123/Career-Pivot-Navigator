package edu.llapp.infra;

import edu.llapp.strategies.matchers.*;
import edu.llapp.strategies.recommenders.*;
import java.util.*;

/**
 * Policy Registry
 * Manages all available policies and provides dynamic selection.
 */
public class StrategyRegistry {
    private Map<String, ISkillMatcher> matchers;
    private Map<String, ICourseStrategy> recommenders;

    public StrategyRegistry() {
        this.matchers = new HashMap<>();
        this.recommenders = new HashMap<>();
        registerDefaultStrategies();
    }

    private void registerDefaultStrategies() {
        // register Matchers
        matchers.put("keyword", new KeywordBasedMatcher());
        matchers.put("semantic", new SemanticMatcher());
        matchers.put("hybrid", new HybridMatcher());

        // register Recommenders
        recommenders.put("popularity", new PopularityBasedStrategy());
        recommenders.put("personalized", new PersonalizedStrategy());
        recommenders.put("cost", new CostOptimizedStrategy());
    }

    /**
     * Retrieves the Matcher strategy based on the key
     * @param key Strategy key (keyword, semantic, hybrid)
     * @return The corresponding strategy; if not found, returns the default KeywordBasedMatcher.
     */
    public ISkillMatcher getMatcher(String key) {
        return matchers.getOrDefault(key.toLowerCase(), new KeywordBasedMatcher());
    }

    /**
     * Retrieve Recommender strategy based on key
     * @param key Strategy key (popularity, personalized, cost)
     * @return The corresponding strategy; if not found, returns the default PopularityBasedStrategy
     */
    public ICourseStrategy getRecommender(String key) {
        return recommenders.getOrDefault(key.toLowerCase(), new PopularityBasedStrategy());
    }

    /**
     * Get all available Matcher strategy names
     */
    public Set<String> getAvailableMatchers() {
        return matchers.keySet();
    }

    /**
     * Get all available Recommender policy names
     */
    public Set<String> getAvailableRecommenders() {
        return recommenders.keySet();
    }
}