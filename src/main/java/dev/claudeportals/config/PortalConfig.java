package dev.claudeportals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.claudeportals.ClaudePortals;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * User-facing options, stored as JSON in the Fabric config dir.
 * Edited through the Mod Menu screen ({@code dev.claudeportals.client.ConfigScreen}).
 *
 * These are this port's own settings, not a copy of upstream's. Fields are added as the
 * features they control land; see docs/PORTING_PLAN.md.
 */
public final class PortalConfig {
    public enum RenderMode { AUTO, NORMAL, COMPATIBILITY }

    public enum ShaderCompat { AUTO, ON, OFF }

    // ---- persisted fields --------------------------------------------------
    public boolean enabled = true;
    public int maxPortalLayers = 2;
    public int portalRenderLimit = 40;
    public RenderMode renderMode = RenderMode.AUTO;
    public ShaderCompat shaderCompat = ShaderCompat.AUTO;
    public boolean distantHorizonsCompat = true;
    public int dhRemoteLodRadiusChunks = 64;
    public boolean debugOverlay = false;

    // ---- plumbing ----------------------------------------------------------
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PortalConfig instance = new PortalConfig();

    private PortalConfig() {}

    public static PortalConfig get() {
        return instance;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(ClaudePortals.MOD_ID + ".json");
    }

    public static synchronized void load() {
        Path path = file();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                PortalConfig loaded = GSON.fromJson(reader, PortalConfig.class);
                if (loaded != null) {
                    instance = loaded;
                }
            } catch (IOException | JsonParseException e) {
                ClaudePortals.LOGGER.warn("Could not read {}; using defaults.", path, e);
            }
        }
        instance.sanitize();
        save(); // writes defaults, drops unknown keys, persists any clamped values
    }

    public static synchronized void save() {
        Path path = file();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            ClaudePortals.LOGGER.warn("Could not write {}.", path, e);
        }
    }

    private void sanitize() {
        maxPortalLayers = clamp(maxPortalLayers, 1, 5);
        portalRenderLimit = clamp(portalRenderLimit, 1, 200);
        dhRemoteLodRadiusChunks = clamp(dhRemoteLodRadiusChunks, 8, 512);
        if (renderMode == null) {
            renderMode = RenderMode.AUTO;
        }
        if (shaderCompat == null) {
            shaderCompat = ShaderCompat.AUTO;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
