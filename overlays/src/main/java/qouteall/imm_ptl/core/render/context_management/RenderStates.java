// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.context_management;

import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

public class RenderStates {
    public static int frameIndex;
    public static ResourceKey<Level> originalPlayerDimension;
    public static Vec3 originalPlayerPos;
    public static Vec3 originalPlayerLastTickPos;
    public static GameType originalGameMode;
    public static AABB originalPlayerBoundingBox;
    public static Set<ResourceKey<Level>> renderedDimensions;
    public static List<List<WeakReference<Portal>>> lastPortalRenderInfos;
    public static List<List<WeakReference<Portal>>> portalRenderInfos;
    public static int portalsRenderedThisFrame;
    public static Vec3 lastCameraPos;
    public static Vec3 cameraPosDelta;
    public static boolean shouldForceDisableCull;
    public static long renderStartNanoTime;
    public static double viewBobFactor;
    public static Matrix4f basicProjectionMatrix;
    public static Camera originalCamera;
    public static String debugText;
    public static boolean isLaggy;
    public static boolean isRenderingEntities;
    public static boolean renderedScalingPortal;
    public static boolean isRenderingPortalWeather;
    public static void updatePreRenderInfo(float newPartialTick) { }
    public static double getViewBobbingOffsetMultiplier() { return 0d; }
    public static void onTotalRenderEnd() { }
    public static int getRenderedPortalNum() { return 0; }
    public static boolean isDimensionRendered(ResourceKey<Level> dimensionType) { return false; }
    public static boolean shouldRenderParticle(Particle particle) { return false; }
    public static void setPartialTick(float partialTick_) { }
    public static float getPartialTick() { return 0f; }
    public static List<String> collectDebugText() { return null; }
}

