// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.compat;

import java.util.List;

public class IPModInfoChecking {
    public static record ModIncompatInfo(String modId, String modName, String startVersion, String endVersion, String desc, String link) {
        boolean isModLoadedWithinVersion() { return false; }
        String getVersionRangeStr() { return null; }
    }

    public static final class ImmPtlInfo {
        public String latestReleaseVersion;
        public List<String> previewVersions;
        public List<ModIncompatInfo> severelyIncompatible;
        public List<ModIncompatInfo> incompatible;
        public List<String> incompatibleShaderpacks;
        public ImmPtlInfo(String latestReleaseVersion, List<ModIncompatInfo> severelyIncompatible, List<ModIncompatInfo> incompatible) { }
        public String toString() { return null; }
    }

    public static ImmPtlInfo fetchImmPtlInfoFromInternet() { return null; }
    public static void initClient() { }
    public static void initDedicatedServer() { }
    public static void checkShaderpack() { }
}

