// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.renderer;

import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;

public class RendererDebug extends PortalRenderer {
    public void onBeforeTranslucentRendering(Matrix4f modelView) { }
    public void onAfterTranslucentRendering(Matrix4f modelView) { }
    public void onHandRenderingEnded() { }
    public void prepareRendering() { }
    public void finishRendering() { }
    public void renderPortalInEntityRenderer(Portal portal) { }
    public boolean replaceFrameBufferClearing() { return false; }
    protected void doRenderPortal(Portal portal, Matrix4f modelView) { }
    protected void renderPortals(Matrix4f modelView) { }
}

