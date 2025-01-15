package wdfeer.lunatic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.block.Block
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.TeleportTarget
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext

const val DREAM_WORLD_PATH = "dream_world"
fun Lunatic.initializeDreamWorld() {
    initializeFeatures()
    initializeTeleportation()
}

private fun Lunatic.initializeFeatures() {
    class DreamWorldFeatureConfig(val blockId: Identifier) : FeatureConfig {
        val codec = RecordCodecBuilder.create {
            it.group(
                Identifier.CODEC.fieldOf("blockId").forGetter { blockId }).apply(it, ::DreamWorldFeatureConfig)
        }
    }

    class GridFeature(configCodec: Codec<DreamWorldFeatureConfig>) : Feature<DreamWorldFeatureConfig>(configCodec) {
        override fun generate(context: FeatureContext<DreamWorldFeatureConfig>): Boolean {
            val world = context.world
            val origin = context.origin
            val block = Registries.BLOCK[context.config.blockId]
            repeat(8) {
                world.setBlockState(origin.up(it), block.defaultState, Block.FORCE_STATE)
            }
            return true
        }
    }

    val config = DreamWorldFeatureConfig(Identifier.ofVanilla("bedrock"))
    val gridFeature = GridFeature(config.codec)
    Registry.register(Registries.FEATURE, Identifier.of(MOD_ID, "dream_world_grid"), gridFeature)
}

private fun Lunatic.initializeTeleportation() =
    ServerLivingEntityEvents.ALLOW_DAMAGE.register { entity: LivingEntity, _: DamageSource, amount: Float ->
        if (entity !is ServerPlayerEntity) return@register true
        if (amount < entity.health) return@register true

        return@register if (entity.isSleeping) {
            val dreamWorld =
                entity.server.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, DREAM_WORLD_PATH)))
            entity.teleportTo(TeleportTarget(dreamWorld, entity) { it.setPos(it.x, 5.0, it.z) })
            false
        } else if (entity.world.registryKey.value.path == DREAM_WORLD_PATH) {
            entity.teleportTo(TeleportTarget(entity.server.overworld, entity) {})
            false
        } else true
    }
