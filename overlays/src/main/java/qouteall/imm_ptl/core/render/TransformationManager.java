// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.DQuaternion;

public class TransformationManager {
    public static Minecraft client = null;
    public static boolean isIsometricView;
    public static float isometricViewLength;
    public static DQuaternion getPlayerCameraRotation() { return null; }
    public static Matrix4f processTransformation(Camera camera, Matrix4f matrix4f) { return null; }
    public static boolean isAnimationRunning() { return false; }
    public static Double getAnimationProgress() { return null; }
    public static double mapProgress(double progress) { return 0d; }
    public static void managePlayerRotationAndChangeGravity(Portal portal) { }
    public static Matrix4f getMirrorTransformation(Vec3 normal) { return null; }
    public static Matrix4f getIsometricProjection() { return null; }
    public static boolean isCalculatingViewBobbingOffset;
    public static class RemoteCallables {
        public static void enableIsometricView(float viewLength) { }
        public static void disableIsometricView() { }
    }

    public static Vec3 getIsometricAdjustedCameraPos() { return null; }
    public static Vec3 getIsometricAdjustedCameraPos(Camera camera) { return null; }
}

