// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.renderer;

import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.SecondaryFrameBuffer;

public class RendererUsingFrameBuffer extends PortalRenderer {
    SecondaryFrameBuffer secondaryFrameBuffer;
    public void onBeforeTranslucentRendering(Matrix4f modelView) { }
    public void onAfterTranslucentRendering(Matrix4f modelView) { }
    public void onHandRenderingEnded() { }
    public void finishRendering() { }
    public void prepareRendering() { }
    protected void doRenderPortal(Portal portal, Matrix4f modelView) { }
    public void renderPortalInEntityRenderer(Portal portal) { }
    public boolean replaceFrameBufferClearing() { return false; }
    protected void renderPortals(Matrix4f modelView) { }
}

