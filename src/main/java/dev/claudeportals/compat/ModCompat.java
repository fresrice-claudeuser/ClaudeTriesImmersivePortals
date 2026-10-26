package dev.claudeportals.compat;

import dev.claudeportals.ClaudePortals;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Single place that answers "is mod X present?". Compat code elsewhere must go through here and
 * must not reference another mod's classes unless the matching check returned true.
 */
public final class ModCompat {
    public static final String IRIS = "iris";
    public static final String SODIUM = "sodium";
    public static final String DISTANT_HORIZONS = "distanthorizons";
    public static final String MOD_MENU = "modmenu";

    private ModCompat() {}

    public static boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static String version(String modId) {
        return FabricLoader.getInstance()
                .getModContainer(modId)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("not installed");
    }

    public static boolean iris() {
        return isLoaded(IRIS);
    }

    public static boolean sodium() {
        return isLoaded(SODIUM);
    }

    public static boolean distantHorizons() {
        return isLoaded(DISTANT_HORIZONS);
    }

    public static void logSummary() {
        ClaudePortals.LOGGER.info("Compat targets: Sodium={}, Iris={}, DistantHorizons={}, ModMenu={}",
                version(SODIUM), version(IRIS), version(DISTANT_HORIZONS), version(MOD_MENU));
    }
}
