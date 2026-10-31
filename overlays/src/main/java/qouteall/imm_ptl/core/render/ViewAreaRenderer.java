// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.TriangleConsumer;

public class ViewAreaRenderer {
    public static void renderPortalArea(Portal portal, Vec3 fogColor, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, boolean doFaceCulling, boolean doModifyColor, boolean doModifyDepth, boolean doClip) { }
    public static void buildPortalViewAreaTrianglesBuffer(Vec3 fogColor, Portal portal, Vec3 cameraPos, float partialTick) { }
    public static void outputTriangle(TriangleConsumer vertexOutput, Vec3 center, Vec3 localXAxis, Vec3 localYAxis, double p0x, double p0y, double p1x, double p1y, double p2x, double p2y) { }
    public static void outputFullQuad(TriangleConsumer vertexOutput, Vec3 posInPlayerCoordinate, Vec3 localXAxis, Vec3 localYAxis) { }
}

