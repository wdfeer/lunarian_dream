package wdfeer.lunatic

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object LunaticClient : ClientModInitializer {
    override fun onInitializeClient() {
        EntityRendererRegistry.register(DoremyEntityType, ::DoremyRenderer)
        DimensionRenderingRegistry.registerSkyRenderer(dreamWorldKey, DreamWorldSkyRenderer)
    }
}