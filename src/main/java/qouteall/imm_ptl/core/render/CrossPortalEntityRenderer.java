// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CrossPortalEntityRenderer {
    public static boolean isRenderingEntityNormally;
    public static boolean isRenderingEntityProjection;
    public static void init() { }
    public static void onEntityTickClient(Entity entity) { }
    public static void onBeginRenderingEntitiesAndBlockEntities(Matrix4f modelView) { }
    public static void onEndRenderingEntitiesAndBlockEntities(PoseStack matrixStack) { }
    public static void beforeRenderingEntity(Entity entity, PoseStack matrixStack) { }
    public static void afterRenderingEntity(Entity entity) { }
    public static boolean hasIntersection(Vec3 outerPlanePos, Vec3 outerPlaneNormal, Vec3 entityPos, Vec3 collidingPortalNormal) { return false; }
    public static boolean shouldRenderPlayerDefault() { return false; }
    public static boolean shouldRenderEntityNow(Entity entity) { return false; }
    public static boolean shouldRenderPlayerNormally(Entity entity) { return false; }
    public static Vec3 getRenderingCameraPos(Entity entity) { return null; }
}

