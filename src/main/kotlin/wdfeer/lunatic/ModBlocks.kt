package wdfeer.lunatic

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

fun initializeBlocks() {
    DreamPortal
}

private object DreamPortal : Block(
    FabricBlockSettings.create()
        .nonOpaque()
        .notSolid()
        .hardness(Float.POSITIVE_INFINITY)
        .resistance(Float.POSITIVE_INFINITY)
        .luminance { 15 }) {
    init {
        Registry.register(Registries.BLOCK, Identifier(Lunatic.MOD_ID, "dream_portal"), this)
    }
}
