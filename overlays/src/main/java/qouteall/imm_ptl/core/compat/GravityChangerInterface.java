// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.compat;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import qouteall.q_misc_util.my_util.DQuaternion;

public class GravityChangerInterface {
    public static Invoker invoker = new Invoker();
    public static class Invoker {
        public boolean isGravityChangerPresent() { return false; }
        public Vec3 getEyeOffset(Entity entity) { return null; }
        public Direction getGravityDirection(Entity entity) { return null; }
        public Direction getBaseGravityDirection(Entity entity) { return null; }
        public void setClientPlayerGravityDirection(Player player, Direction direction) { }
        public void setBaseGravityDirectionServer(Entity entity, Direction direction) { }
        public DQuaternion getExtraCameraRotation(Direction gravityDirection) { return null; }
        public Vec3 getWorldVelocity(Entity entity) { return null; }
        public void setWorldVelocity(Entity entity, Vec3 newVelocity) { }
        public Vec3 transformPlayerToWorld(Direction gravity, Vec3 vec3d) { return null; }
        public Vec3 transformWorldToPlayer(Direction gravity, Vec3 vec3d) { return null; }
        public Direction transformDirPlayerToWorld(Direction gravity, Direction direction) { return null; }
        public Direction transformDirWorldToPlayer(Direction gravity, Direction direction) { return null; }
    }

    public static class OnGravityChangerPresent extends Invoker {
        public boolean isGravityChangerPresent() { return false; }
        public Vec3 getEyeOffset(Entity entity) { return null; }
        public Direction getGravityDirection(Entity entity) { return null; }
        public Direction getBaseGravityDirection(Entity entity) { return null; }
        public void setBaseGravityDirectionServer(Entity entity, Direction direction) { }
        public void setClientPlayerGravityDirection(Player player, Direction direction) { }
        public DQuaternion getExtraCameraRotation(Direction gravityDirection) { return null; }
        public Vec3 getWorldVelocity(Entity entity) { return null; }
        public void setWorldVelocity(Entity entity, Vec3 newVelocity) { }
        public Vec3 transformPlayerToWorld(Direction gravity, Vec3 vec3d) { return null; }
        public Vec3 transformWorldToPlayer(Direction gravity, Vec3 vec3d) { return null; }
        public Direction transformDirPlayerToWorld(Direction gravity, Direction direction) { return null; }
        public Direction transformDirWorldToPlayer(Direction gravity, Direction direction) { return null; }
    }

}

