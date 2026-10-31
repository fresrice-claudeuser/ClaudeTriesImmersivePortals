// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.renderer;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;
import java.util.List;
import java.util.function.Predicate;

public abstract class PortalRenderer {
    public static Event<Predicate<Portal>> PORTAL_RENDERING_PREDICATE = null;
    public static Minecraft client = null;
    public abstract void onBeforeTranslucentRendering(Matrix4f modelView);
    public abstract void onAfterTranslucentRendering(Matrix4f modelView);
    public abstract void onHandRenderingEnded();
    public void onBeforeHandRendering(Matrix4f modelView) { }
    public abstract void prepareRendering();
    public abstract void finishRendering();
    public abstract void renderPortalInEntityRenderer(Portal portal);
    public abstract boolean replaceFrameBufferClearing();
    protected List<Portal> getPortalsToRender(Matrix4f modelView) { return null; }
    public static double getRenderRange() { return 0d; }
    protected final void renderPortalContent(Portal portal) { }
    public void invokeWorldRendering(WorldRenderInfo worldRenderInfo) { }
    public static Matrix4f getPortalTransformation(Portal portal) { return null; }
    public static Matrix4f getPortalRotationMatrix(Portal portal) { return null; }
    public static Matrix4f combineNullable(Matrix4f a, Matrix4f b) { return null; }
    public static Matrix4f getPortalScaleMatrix(Portal portal) { return null; }
    public static boolean shouldApplyScaleToModelView(Portal portal) { return false; }
    public void onBeginIrisTranslucentRendering(Matrix4f modelView) { }
    public static void switchToCorrectRenderer() { }
}

