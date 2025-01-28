package wdfeer.lunarian_dream

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.RenderLayer

object LunarianClient : ClientModInitializer {
    override fun onInitializeClient() {
        EntityRendererRegistry.register(DoremyEntityType, ::DoremyRenderer)
        DimensionRenderingRegistry.registerSkyRenderer(dreamWorldKey, DreamWorldSkyRenderer)

        BlockRenderLayerMap.INSTANCE.putBlock(DreamPortal, RenderLayer.getCutout())
    }
}
