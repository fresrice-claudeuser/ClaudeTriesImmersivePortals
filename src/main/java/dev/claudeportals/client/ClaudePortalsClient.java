package dev.claudeportals.client;

import dev.claudeportals.ClaudePortals;
import net.fabricmc.api.ClientModInitializer;

/** Client-only entrypoint. Portal rendering hooks are registered from here as they are ported. */
public class ClaudePortalsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClaudePortals.LOGGER.info("Claude Immersive Portals client side ready.");
    }
}
