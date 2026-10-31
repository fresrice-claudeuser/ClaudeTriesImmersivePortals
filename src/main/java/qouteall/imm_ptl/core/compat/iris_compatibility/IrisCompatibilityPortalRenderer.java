// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.compat.iris_compatibility;

import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;
import qouteall.imm_ptl.core.render.renderer.PortalRenderer;

public class IrisCompatibilityPortalRenderer extends PortalRenderer {
    public static IrisCompatibilityPortalRenderer instance = null;
    public static IrisCompatibilityPortalRenderer debugModeInstance = null;
    public boolean isDebugMode;
    public IrisCompatibilityPortalRenderer(boolean isDebugMode) { }
    public boolean replaceFrameBufferClearing() { return false; }
    public void onBeforeTranslucentRendering(Matrix4f modelView) { }
    public void onAfterTranslucentRendering(Matrix4f modelView) { }
    public void finishRendering() { }
    public void prepareRendering() { }
    protected void doRenderPortal(Portal portal, Matrix4f modelView) { }
    public void invokeWorldRendering(WorldRenderInfo worldRenderInfo) { }
    public void renderPortalInEntityRenderer(Portal portal) { }
    public void onBeforeHandRendering(Matrix4f modelView) { }
    public void onHandRenderingEnded() { }
    protected void renderPortals(Matrix4f modelView) { }
}

