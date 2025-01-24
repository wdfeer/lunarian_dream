package wdfeer.lunatic

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.SkyRenderer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.*


object DreamWorldSkyRenderer : SkyRenderer {
    override fun render(ctx: WorldRenderContext) {
        val matrixStack = ctx.matrixStack()

        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.depthMask(false)

        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer
        val matrices = matrixStack.peek().positionMatrix

        drawLineEffect(matrices, buffer, MinecraftClient.getInstance().player?.y?.toFloat() ?: 60f)
        tessellator.draw()

        RenderSystem.depthMask(true)
        RenderSystem.disableBlend()
    }

    private fun drawLineEffect(matrices: Matrix4f, buffer: BufferBuilder, playerY: Float) {
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR)

        val count = 17
        val x = 3000f
        val y = 10f
        for (z in (1..count).map { i ->
            (i - count / 2f) * (sqrt(playerY) + 1f)
        }) {
            buffer.line(matrices, Vector3f(-x, y, z), Vector3f(x, y, z))
            buffer.line(matrices, Vector3f(-x, -y, z), Vector3f(x, -y, z))
        }
    }

    private fun BufferBuilder.line(matrices: Matrix4f, p1: Vector3f, p2: Vector3f) {
        vertex(matrices, p1.x, p1.y, p1.z).color(1f, 0f, 0f, 0.5f).next()
        vertex(matrices, p2.x, p2.y, p2.z).color(1f, 0f, 0f, 0.5f).next()
    }
}

