// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.context_management;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import java.util.List;
import java.util.UUID;

public class WorldRenderInfo {
    public static record IsometricParameters() {
    }

    public ClientLevel world;
    public Vec3 cameraPos;
    public boolean overwriteCameraTransformation;
    public Matrix4f cameraTransformation;
    public UUID description;
    public int renderDistance;
    public boolean doRenderHand;
    public boolean enableViewBobbing;
    public boolean doRenderSky;
    public boolean hasFog;
    public IsometricParameters isometricParameters;
    public static void pushRenderInfo(WorldRenderInfo worldRenderInfo) { }
    public static void popRenderInfo() { }
    public static void adjustCameraPos(Camera camera) { }
    public static void applyAdditionalTransformations(PoseStack matrixStack) { }
    public static boolean isRendering() { return false; }
    public static int getRenderingLayer() { return 0; }
    public static List<UUID> getRenderingDescription() { return null; }
    public static int getRenderDistance() { return 0; }
    public static WorldRenderInfo getTopRenderInfo() { return null; }
    public static Vec3 getCameraPos() { return null; }
    public static boolean isViewBobbingEnabled() { return false; }
    public static boolean isFogEnabled() { return false; }
    public static class Builder {
        public Builder() { }
        public Builder setWorld(ClientLevel world) { return null; }
        public Builder setCameraPos(Vec3 cameraPos) { return null; }
        public Builder setCameraTransformation(Matrix4f cameraTransformation) { return null; }
        public Builder setOverwriteCameraTransformation(boolean overwriteCameraTransformation) { return null; }
        public Builder setDescription(UUID description) { return null; }
        public Builder setRenderDistance(int renderDistance) { return null; }
        public Builder setDoRenderHand(boolean doRenderHand) { return null; }
        public Builder setEnableViewBobbing(boolean enableViewBobbing) { return null; }
        public Builder setDoRenderSky(boolean doRenderSky) { return null; }
        public Builder setHasFog(boolean hasFog) { return null; }
        public Builder setIsometricParameters(IsometricParameters isometricParameters) { return null; }
        public WorldRenderInfo build() { return null; }
    }

}

