package wdfeer.lunarian_dream

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun LunarianDream.initializeBlocks() {
    Registry.register(Registries.BLOCK, Identifier(MOD_ID, "dream_portal"), DreamPortal)
}

object DreamPortal : Block(
    FabricBlockSettings.create()
        .nonOpaque()
        .notSolid()
        .hardness(Float.POSITIVE_INFINITY)
        .resistance(Float.POSITIVE_INFINITY)
        .luminance { 15 }) {
    override fun onSteppedOn(world: World?, pos: BlockPos?, state: BlockState?, entity: Entity?) {
        if (entity is ServerPlayerEntity) entity.teleportToDreamWorld()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onEntityCollision(state: BlockState?, world: World?, pos: BlockPos?, entity: Entity?) {
        if (entity is ServerPlayerEntity) entity.teleportToDreamWorld()

    }
}