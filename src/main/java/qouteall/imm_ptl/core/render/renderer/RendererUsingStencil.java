// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.renderer;

import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;

public class RendererUsingStencil extends PortalRenderer {
    public boolean replaceFrameBufferClearing() { return false; }
    public void onBeforeTranslucentRendering(Matrix4f modelView) { }
    protected void doPortalRendering(Matrix4f modelView) { }
    protected void renderPortals(Matrix4f modelView) { }
    public void onAfterTranslucentRendering(Matrix4f modelView) { }
    public void onHandRenderingEnded() { }
    public void prepareRendering() { }
    public void finishRendering() { }
    protected void doRenderPortal(Portal portal, Matrix4f modelView) { }
    public void renderPortalInEntityRenderer(Portal portal) { }
    protected void restoreDepthOfPortalViewArea(Portal portal, Matrix4f modelView, int portalStencilValue) { }
    public static void clampStencilValue(int maximumValue) { }
    public static void setStencilLimitation(int stencilValue) { }
    public static boolean shouldSkipRenderingInsideFuseViewPortal(Portal portal) { return false; }
}

