package net.hynse.stackable.util;

import net.hynse.stackable.Stackable;
import org.bukkit.Material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Utility class for pattern matching with optimized caching
 * Supports simple wildcard matching using * (glob-style)
 */
public class RegexUtil {
    private static final char WILDCARD = '*';
    private static final Map<Material, Integer> matchCache = new ConcurrentHashMap<>();

    /**
     * Gets a cached match result or computes and caches it
     * @param material The material to check
     * @param patterns Map of wildcard patterns to their values
     * @param def Default value if no match is found
     * @return The matched value or default value
     */
    public static int getMatchOrDefault(Material material, Map<String, Integer> patterns, int def) {
        if (matchCache.containsKey(material)) {
            return matchCache.get(material);
        }

        String materialName = material.name().toUpperCase();
        logDebug("Checking patterns for material: " + materialName);

        for (Map.Entry<String, Integer> entry : patterns.entrySet()) {
            String pattern = entry.getKey().toUpperCase();
            boolean matches = matchWildcard(pattern, materialName);

            logDebug("  Pattern '" + pattern + "' " +
                    (matches ? "MATCHES" : "does not match") +
                    " material '" + materialName + "'");

            if (matches) {
                int value = entry.getValue();
                matchCache.put(material, value);
                logDebug("  Found match! Setting stack size to " + value);
                return value;
            }
        }

        logDebug("  No pattern matched. Using default value: " + def);
        return def;
    }

    /**
     * Clears the match cache
     */
    public static void clearCache() {
        matchCache.clear();
    }

    /**
     * Logs debug information if debug mode is enabled
     */
    private static void logDebug(String msg) {
        if (Stackable.instance != null &&
                Stackable.instance.getConfig().getBoolean("debug", false)) {
            Stackable.instance.getLogger().log(Level.INFO, "[DEBUG] " + msg);
        }
    }

    /**
     * Tests a pattern against a material name
     */
    public static boolean testPattern(String pattern, String materialName) {
        boolean matches = matchWildcard(pattern.toUpperCase(), materialName.toUpperCase());
        logDebug("Testing pattern '" + pattern + "' against '" + materialName + "': " +
                (matches ? "MATCH" : "NO MATCH"));
        return matches;
    }

    /**
     * Matches a string against a wildcard pattern.
     * Supports:
     * - *NAME    (ends with)
     * - NAME*    (starts with)
     * - *NAME*   (contains)
     * - NAME*NAME (multi-part sequence)
     * - *        (matches everything)
     * @param pattern The wildcard pattern
     * @param text The text to test against
     * @return true if pattern matches text
     */
    public static boolean matchWildcard(String pattern, String text) {
        if (pattern.equals("*")) return true;
        if (!pattern.contains(String.valueOf(WILDCARD))) return pattern.equals(text);

        String[] parts = pattern.split("\\*", -1);
        int pos = 0;

        // Check first part (must match start if not empty)
        if (!parts[0].isEmpty()) {
            if (!text.startsWith(parts[0])) return false;
            pos = parts[0].length();
        }

        // Check middle parts
        for (int i = 1; i < parts.length - 1; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue; // handles multiple ** in pattern
            int idx = text.indexOf(part, pos);
            if (idx == -1) return false;
            pos = idx + part.length();
        }

        // Check last part (must match end if not empty)
        String last = parts[parts.length - 1];
        if (!last.isEmpty() && !text.substring(pos).endsWith(last)) {
            return false;
        }

        return true;
    }
}
