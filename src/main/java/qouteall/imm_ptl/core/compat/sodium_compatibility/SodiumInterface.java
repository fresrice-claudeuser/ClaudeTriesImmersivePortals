// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.compat.sodium_compatibility;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import qouteall.imm_ptl.core.render.FrustumCuller;

public class SodiumInterface {
    public static FrustumCuller frustumCuller;
    public static class Invoker {
        public boolean isSodiumPresent() { return false; }
        public Object createNewContext(int renderDistance) { return null; }
        public void switchContextWithCurrentWorldRenderer(Object context) { }
        public void markSpriteActive(TextureAtlasSprite sprite) { }
        public void onClientChunkLoaded(ClientLevel world, int chunkX, int chunkZ) { }
        public void onClientChunkUnloaded(ClientLevel world, int chunkX, int chunkZ) { }
    }

    public static Invoker invoker = new Invoker();
    public static class OnSodiumPresent extends Invoker {
        public boolean isSodiumPresent() { return false; }
        public Object createNewContext(int renderDistance) { return null; }
        public void switchContextWithCurrentWorldRenderer(Object context) { }
        public void markSpriteActive(TextureAtlasSprite sprite) { }
        public void onClientChunkLoaded(ClientLevel world, int chunkX, int chunkZ) { }
        public void onClientChunkUnloaded(ClientLevel world, int chunkX, int chunkZ) { }
    }

}

