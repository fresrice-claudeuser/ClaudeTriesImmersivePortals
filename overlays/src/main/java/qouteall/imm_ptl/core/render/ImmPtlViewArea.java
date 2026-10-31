// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ImmPtlViewArea {
    public static class Column {
        public long mark;
        public RenderSection[] sections;
        public Column(RenderSection[] sections) { }
    }

    public static class Preset {
        public RenderSection[] data;
        public long lastActiveTime;
        public Preset(RenderSection[] data) { }
    }

    public int minSectionY;
    public int endSectionY;
    public static void init() { }
    public ImmPtlViewArea(SectionRenderDispatcher sectionBuilder, Level world, int r, LevelRenderer worldRenderer) { }
    protected void createSections(SectionRenderDispatcher sectionBuilder_1) { }
    public void releaseAllBuffers() { }
    public void repositionCamera(double playerX, double playerZ) { }
    public void setDirty(int cx, int cy, int cz, boolean isImportant) { }
    public RenderSection provideBuiltChunkByChunkPos(int cx, int cy, int cz) { return null; }
    public Column provideColumn(long sectionPos) { return null; }
    public int getManagedSectionNum() { return 0; }
    public String getDebugString() { return null; }
    public int getRadius() { return 0; }
    public boolean isRegionActive(int cxStart, int czStart, int cxEnd, int czEnd) { return false; }
    public void onChunkUnload(int sectionX, int sectionZ) { }
    public RenderSection getSectionFromRawArray(BlockPos sectionOrigin, RenderSection[] sections) { return null; }
    protected RenderSection getRenderSectionAt(BlockPos pos) { return null; }
    public RenderSection rawFetch(int cx, int cy, int cz, long timeMark) { return null; }
    public RenderSection rawGet(int cx, int cy, int cz) { return null; }
}

