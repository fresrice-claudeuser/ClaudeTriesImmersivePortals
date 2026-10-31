// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.animation.UnilateralPortalState;
import qouteall.q_misc_util.my_util.BoxPredicateF;

public class FrustumCuller {
    public FrustumCuller() { }
    public void update(double cameraX, double cameraY, double cameraZ) { }
    public boolean canDetermineInvisibleWithCameraCoord(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) { return false; }
    public boolean canDetermineInvisibleWithCameraCoord(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) { return false; }
    public boolean canDetermineInvisibleWithWorldCoord(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) { return false; }
    public static Vec3[] getRectPortalFourVerticesCounterClockwise(UnilateralPortalState thisSideState) { return null; }
    public static Frustum4Planes getFrustumPlanesFromFourVerticesCounterClockwise(Vec3[] vertices) { return null; }
    public static BoxPredicateF getFlatPortalInnerFrustumCullingFunc(Portal portal, Vec3 cameraPos) { return null; }
    public static BoxPredicateF getFlatPortalOuterFrustumCullingFunc(Portal portal, Vec3 cameraPos) { return null; }
    public static boolean isFullyInFrontOfPlane(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float planeX, float planeY, float planeZ, float planeW) { return false; }
    public static boolean isFullyBehindPlane(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float planeX, float planeY, float planeZ, float planeW) { return false; }
    public record Frustum4Planes(float p0X, float p0Y, float p0Z, float p0W, float p1X, float p1Y, float p1Z, float p1W, float p2X, float p2Y, float p2Z, float p2W, float p3X, float p3Y, float p3Z, float p3W) {
        public boolean isFullyOutside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) { return false; }
        public boolean isFullyInside(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) { return false; }
    }

    public static enum BatchTestResult {
        all_true, all_false, both;
    }

    public static BatchTestResult testBoxTwoVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double planeNormalX, double planeNormalY, double planeNormalZ, double planePosX, double planePosY, double planePosZ) { return null; }
    public static BatchTestResult testBoxTwoVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Vec3 planeNormal) { return null; }
}

