package dev.claudeportals.client;

import dev.claudeportals.config.PortalConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Options screen opened from Mod Menu. Deliberately uses only plain buttons (click to cycle) so it
 * depends on as little GUI API as possible; that keeps it cheap to carry across Minecraft versions.
 */
public class ConfigScreen extends Screen {
    private static final String KEY = "claude_immersive_portals.config.";
    private static final int COL_W = 150;
    private static final int ROW_H = 24;

    private final Screen parent;
    private final PortalConfig cfg = PortalConfig.get();

    public ConfigScreen(Screen parent) {
        super(Component.translatable(KEY + "title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - COL_W - 5;
        int right = this.width / 2 + 5;
        int y = Math.max(36, this.height / 6);

        addCycle(left, y, "enabled", List.of(Boolean.FALSE, Boolean.TRUE),
                () -> cfg.enabled, v -> cfg.enabled = v, ConfigScreen::onOff);
        addCycle(right, y, "max_layers", List.of(1, 2, 3, 4, 5),
                () -> cfg.maxPortalLayers, v -> cfg.maxPortalLayers = v, ConfigScreen::number);

        y += ROW_H;
        addCycle(left, y, "render_limit", List.of(10, 20, 40, 80, 160),
                () -> cfg.portalRenderLimit, v -> cfg.portalRenderLimit = v, ConfigScreen::number);
        addCycle(right, y, "render_mode", List.of(PortalConfig.RenderMode.values()),
                () -> cfg.renderMode, v -> cfg.renderMode = v, ConfigScreen::named);

        y += ROW_H;
        addCycle(left, y, "shader_compat", List.of(PortalConfig.ShaderCompat.values()),
                () -> cfg.shaderCompat, v -> cfg.shaderCompat = v, ConfigScreen::named);
        addCycle(right, y, "dh_compat", List.of(Boolean.FALSE, Boolean.TRUE),
                () -> cfg.distantHorizonsCompat, v -> cfg.distantHorizonsCompat = v, ConfigScreen::onOff);

        y += ROW_H;
        addCycle(left, y, "dh_radius", List.of(16, 32, 64, 128, 256),
                () -> cfg.dhRemoteLodRadiusChunks, v -> cfg.dhRemoteLodRadiusChunks = v, ConfigScreen::number);
        addCycle(right, y, "debug_overlay", List.of(Boolean.FALSE, Boolean.TRUE),
                () -> cfg.debugOverlay, v -> cfg.debugOverlay = v, ConfigScreen::onOff);

        addRenderableWidget(Button.builder(Component.translatable(KEY + "done"), b -> onClose())
                .bounds(this.width / 2 - 100, this.height - 28, 200, 20)
                .build());
    }

    /** One button that cycles through {@code values} on click and writes straight into the config. */
    private <T> void addCycle(int x, int y, String name, List<T> values,
                              Supplier<T> get, Consumer<T> set, Function<T, Component> show) {
        addRenderableWidget(Button.builder(label(name, show.apply(get.get())), b -> {
                    int index = values.indexOf(get.get()); // -1 (hand-edited JSON) wraps to the first value
                    T next = values.get((index + 1) % values.size());
                    set.accept(next);
                    b.setMessage(label(name, show.apply(next)));
                })
                .bounds(x, y, COL_W, 20)
                .tooltip(Tooltip.create(Component.translatable(KEY + name + ".tip")))
                .build());
    }

    private static Component label(String name, Component value) {
        return Component.translatable(KEY + name, value);
    }

    private static Component onOff(Boolean on) {
        return Component.translatable(KEY + (on ? "on" : "off"));
    }

    private static Component number(Integer value) {
        return Component.literal(String.valueOf(value));
    }

    private static Component named(Enum<?> value) {
        return Component.translatable(KEY + "value." + value.name().toLowerCase(java.util.Locale.ROOT));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Screen#render draws the background and widgets itself; calling renderBackground as well
        // would blur twice in one frame and crash on current versions.
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        PortalConfig.save();
        this.minecraft.setScreen(parent);
    }
}
