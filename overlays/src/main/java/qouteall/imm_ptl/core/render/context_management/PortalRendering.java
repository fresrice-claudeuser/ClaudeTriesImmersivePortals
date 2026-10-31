// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.context_management;

import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.Plane;

public class PortalRendering {
    public static void pushPortalLayer(Portal portal) { }
    public static void popPortalLayer() { }
    public static int getPortalLayer() { return 0; }
    public static boolean isRendering() { return false; }
    public static boolean isRenderingOddNumberOfMirrors() { return false; }
    public static int getMaxPortalLayer() { return 0; }
    public static Portal getRenderingPortal() { return null; }
    public static void onBeginPortalWorldRendering() { }
    public static void onEndPortalWorldRendering() { }
    public static Vec3 getRenderingCameraPos() { return null; }
    public static double getExtraModelViewScaling() { return 0d; }
    public static boolean shouldEnableSodiumCaveCulling() { return false; }
    public static boolean shouldRenderHitResult() { return false; }
    public static Plane getActiveClippingPlane() { return null; }
    public static boolean isInvalidRecursionRendering(Portal toRender) { return false; }
}

