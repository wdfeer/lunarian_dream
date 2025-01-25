package wdfeer.lunatic

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.world.World
import wdfeer.lunatic.Lunatic.MOD_ID

const val DREAM_WORLD_PATH = "dream_world"
val dreamWorldKey: RegistryKey<World> = RegistryKey.of(
    RegistryKeys.WORLD,
    Identifier.of(MOD_ID, DREAM_WORLD_PATH)
)

fun Lunatic.initializeDreamWorld() {
    initializeFeatures()
    initializeTeleportation()
}

private fun Lunatic.initializeFeatures() {
    Registry.register(Registries.FEATURE, Identifier.of(MOD_ID, "dream_grid"), GridFeature())
    Registry.register(Registries.FEATURE, Identifier.of(MOD_ID, "dream_dungeon"), DungeonFeature())
}

private fun initializeTeleportation() =
    ServerLivingEntityEvents.ALLOW_DAMAGE.register { entity: LivingEntity, _: DamageSource, amount: Float ->
        if (entity !is ServerPlayerEntity) return@register true
        if (amount < entity.health) return@register true

        return@register if (entity.isSleeping) {
            entity.clearSleepingPosition()
            entity.teleportToDreamWorld()

            false
        } else if (entity.world.registryKey.value.path == DREAM_WORLD_PATH) {
            entity.teleport(entity.server.overworld, entity.x, 256.0, entity.z, 0f, 0f)
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER))
            false
        } else true
    }

fun MinecraftServer.getDreamWorld(): ServerWorld = getWorld(
    RegistryKey.of(
        RegistryKeys.WORLD,
        Identifier.of(MOD_ID, DREAM_WORLD_PATH)
    )
)!! // Must be registered

fun ServerPlayerEntity.teleportToDreamWorld() {
    teleport(
        server.getDreamWorld(),
        0.0, // Flag not set - uses player pos
        40.0,
        0.0, // Flag not set - uses player pos
        setOf(PositionFlag.Y),
        0f,
        0f
    )
}