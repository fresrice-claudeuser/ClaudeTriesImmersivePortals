// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.compat.iris_compatibility;

import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;
import qouteall.imm_ptl.core.render.renderer.PortalRenderer;

public class ExperimentalIrisPortalRenderer extends PortalRenderer {
    public static ExperimentalIrisPortalRenderer instance = null;
    public static void init() { }
    public boolean replaceFrameBufferClearing() { return false; }
    public void onBeforeTranslucentRendering(Matrix4f modelView) { }
    public void onBeginIrisTranslucentRendering(Matrix4f modelView) { }
    public void onAfterTranslucentRendering(Matrix4f modelView) { }
    public void onHandRenderingEnded() { }
    public void prepareRendering() { }
    public void finishRendering() { }
    protected void restoreDepthOfPortalViewArea(Portal portal, Matrix4f modelView) { }
    public void invokeWorldRendering(WorldRenderInfo worldRenderInfo) { }
    protected void doPortalRendering(Matrix4f modelView) { }
    protected void renderPortals(Matrix4f modelView) { }
    public void onAfterIrisDeferredCompositeRendering() { }
    public void renderPortalInEntityRenderer(Portal portal) { }
    public static void clampStencilValue(int maximumValue) { }
}

