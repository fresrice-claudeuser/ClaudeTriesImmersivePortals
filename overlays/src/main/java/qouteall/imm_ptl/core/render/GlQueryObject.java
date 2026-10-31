// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;


public class GlQueryObject {
    public GlQueryObject(int handle) { }
    public void performQueryAnySamplePassed(Runnable renderingFunc) { }
    public void performQuerySampleNumPassed(Runnable renderingFunc) { }
    public boolean fetchQueryResult() { return false; }
    public boolean isValid() { return false; }
    public static GlQueryObject acquireQueryObject() { return null; }
    public static void returnQueryObject(GlQueryObject obj) { }
}

