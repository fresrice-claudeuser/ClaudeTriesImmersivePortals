// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.context_management;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;

public class CloudContext {
    public int lastCloudsBlockX;
    public int lastCloudsBlockY;
    public int lastCloudsBlockZ;
    public ResourceKey<Level> dimension;
    public Vec3 cloudColor;
    public static ArrayList<CloudContext> contexts = null;
    public static void init() { }
    public CloudContext() { }
    public void dispose() { }
    public static CloudContext findAndTakeContext(int lastCloudsBlockX, int lastCloudsBlockY, int lastCloudsBlockZ, ResourceKey<Level> dimension, Vec3 cloudColor) { return null; }
    public static void appendContext(CloudContext context) { }
}

