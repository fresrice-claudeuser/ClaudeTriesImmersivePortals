// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import java.util.List;
import java.util.Set;

public class ShaderCodeTransformation {
    public static enum ShaderType {
        vs, fs;
    }

    public static class ConfigsObj {
        public List<Config> configs;
    }

    public static class TransformationEntry {
        public String comment;
        public String pattern;
        public String replacement;
    }

    public static class Config {
        public String comment;
        public ShaderType type;
        public Set<String> affectedShaders;
        public List<TransformationEntry> transformations;
        public boolean debugOutput;
    }

    public static void init() { }
    public static boolean shouldAddUniform(String shaderName) { return false; }
}

