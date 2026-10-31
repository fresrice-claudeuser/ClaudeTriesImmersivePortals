// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.Plane;

public class FrontClipping {
    public static boolean isClippingEnabled;
    public static double ADJUSTMENT = 0d;
    public static void disableClipping() { }
    public static void updateInnerClipping(PoseStack matrixStack) { }
    public static void updateInnerClipping(Matrix4f modelView) { }
    public static void setupInnerClipping(Plane clipping, Matrix4f modelView, double adjustment) { }
    public static void setupOuterClipping(PoseStack matrixStack, Portal portal) { }
    public static double[] getActiveClipPlaneEquationBeforeModelView() { return null; }
    public static double[] getActiveClipPlaneEquationAfterModelView() { return null; }
    public static void updateClippingEquationUniformForCurrentShader(boolean isRenderingEntities) { }
    public static void unsetClippingUniform() { }
}

