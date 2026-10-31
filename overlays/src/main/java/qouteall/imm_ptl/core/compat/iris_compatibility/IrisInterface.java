// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.compat.iris_compatibility;

import net.minecraft.client.renderer.LevelRenderer;

public class IrisInterface {
    public static class Invoker {
        public boolean isIrisPresent() { return false; }
        public boolean isShaders() { return false; }
        public boolean isRenderingShadowMap() { return false; }
        public Object getPipeline(LevelRenderer worldRenderer) { return null; }
        public void setPipeline(LevelRenderer worldRenderer, Object pipeline) { }
        public void reloadPipelines() { }
        public String getShaderpackName() { return null; }
    }

    public static class OnIrisPresent extends Invoker {
        public boolean isIrisPresent() { return false; }
        public boolean isShaders() { return false; }
        public boolean isRenderingShadowMap() { return false; }
        public Object getPipeline(LevelRenderer worldRenderer) { return null; }
        public void setPipeline(LevelRenderer worldRenderer, Object pipeline) { }
        public void reloadPipelines() { }
        public String getShaderpackName() { return null; }
    }

    public static Invoker invoker = new Invoker();
}

