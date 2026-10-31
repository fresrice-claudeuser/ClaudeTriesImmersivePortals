// GENERATED compile-only stub (tools/stubgen): signatures from upstream, bodies defaulted. Replace in the render rewrite.
package qouteall.imm_ptl.core.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.portal.Portal;
import java.io.IOException;
import java.awt.*;

public class MyRenderHelper {
    public static Minecraft client = null;
    public static void init() { }
    public static class DrawFbInAreaShader {
        public DrawFbInAreaShader(ResourceProvider factory, String name, VertexFormat format) throws IOException { }
        void loadWidthHeight(int w, int h) { }
    }

    public static DrawFbInAreaShader drawFbInAreaShader;
    public static void drawPortalAreaWithFramebuffer(Portal portal, RenderTarget textureProvider, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) { }
    public static void renderScreenTriangle() { }
    public static void renderScreenTriangle(Vec3 color) { }
    public static void testOneTriangle(int r, int g, int b, int a) { }
    public static void renderScreenTriangle(int r, int g, int b, int a) { }
    public static void drawScreenFrameBuffer(RenderTarget textureProvider, boolean doUseAlphaBlend, boolean doEnableModifyAlpha) { }
    public static void drawFramebuffer(RenderTarget textureProvider, boolean doUseAlphaBlend, boolean doEnableModifyAlpha, float xMin, float xMax, float yMin, float yMax) { }
    public static void drawFramebufferWithViewport(RenderTarget textureProvider, boolean doUseAlphaBlend, boolean doEnableModifyAlpha, float left, float right, float bottom, float up, int viewportWidth, int viewportHeight) { }
    public static void drawFramebufferWithBounds(RenderTarget textureProvider, boolean doUseAlphaBlend, boolean doEnableModifyAlpha, int xMin, int xMax, int yMin, int yMax) { }
    public static void drawFramebufferWithCoordinatesAndDimensions(RenderTarget textureProvider, boolean doUseAlphaBlend, boolean doEnableModifyAlpha, int x, int y, int viewportWidth, int viewportHeight) { }
    public static void lateUpdateLight() { }
    public static void earlyRemoteUpload() { }
    public static void applyMirrorFaceCulling() { }
    public static void recoverFaceCulling() { }
    public static void clearAlphaTo1(RenderTarget mcFrameBuffer) { }
    public static void restoreViewPort() { }
    public static float transformFogDistance(float value) { return 0f; }
    public static void debugFramebufferDepth() { }
    public static void debugFramebufferColorRed() { }
}

