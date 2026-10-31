// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.compat.iris_compatibility;

import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;
import qouteall.imm_ptl.core.render.renderer.PortalRenderer;

public class IrisPortalRenderer extends PortalRenderer {
    public static IrisPortalRenderer instance = null;
    IrisPortalRenderer() { }
    public boolean replaceFrameBufferClearing() { return false; }
    public void prepareRendering() { }
    public void onBeforeHandRendering(Matrix4f modelView) { }
    public void onHandRenderingEnded() { }
    public void onBeforeTranslucentRendering(Matrix4f modelView) { }
    public void onAfterTranslucentRendering(Matrix4f modelView) { }
    public void finishRendering() { }
    protected void doRenderPortal(Portal portal, Matrix4f modelView) { }
    public void invokeWorldRendering(WorldRenderInfo worldRenderInfo) { }
    public void renderPortalInEntityRenderer(Portal portal) { }
    protected void renderPortals(Matrix4f modelView) { }
}

