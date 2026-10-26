package dev.claudeportals;

import dev.claudeportals.compat.ModCompat;
import dev.claudeportals.config.PortalConfig;
import net.fabricmc.api.ModInitializer;

/** Common (client + dedicated server) entrypoint. */
public class ClaudePortalsMain implements ModInitializer {
    @Override
    public void onInitialize() {
        PortalConfig.load();
        ModCompat.logSummary();
        ClaudePortals.LOGGER.info("Claude Immersive Portals scaffold initialised (portal code not ported yet).");
    }
}
