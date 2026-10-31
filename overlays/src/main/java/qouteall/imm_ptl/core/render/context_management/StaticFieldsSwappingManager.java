// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render.context_management;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StaticFieldsSwappingManager<Context> {
    public static class ContextRecord<Ctx> {
        public ResourceKey<Level> dimension;
        public Ctx context;
        public boolean isHoldingLatestContext;
        public ContextRecord(ResourceKey<Level> dimension, Ctx context, boolean isHoldingLatestContext) { }
    }

    public Map<ResourceKey<Level>, ContextRecord<Context>> contextMap;
    public StaticFieldsSwappingManager(Consumer<Context> copyFromObject, Consumer<Context> copyToObject, boolean doStrictCheck, Supplier<Context> contextConstructor) { }
    public boolean isSwapped() { return false; }
    public void setOuterDimension(ResourceKey<Level> dim) { }
    public void resetChecks() { }
    public ResourceKey<Level> getCurrentDimension() { return null; }
    public void pushSwapping(ResourceKey<Level> newDimension) { }
    public void popSwapping() { }
    public void swapAndInvoke(ResourceKey<Level> newDimension, Runnable func) { }
    public void updateOuterDimensionAndChangeContext(ResourceKey<Level> newDimension) { }
}

