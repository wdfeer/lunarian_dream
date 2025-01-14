package wdfeer.lunatic

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.TeleportTarget
import org.slf4j.LoggerFactory

object Lunatic : ModInitializer {
    private const val MOD_ID = "lunatic"
    private const val DREAM_WORLD_PATH = "dream_world"
    private val logger = LoggerFactory.getLogger("lunatic")

    override fun onInitialize() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register { entity: LivingEntity, _: DamageSource, amount: Float ->
            if (entity !is ServerPlayerEntity) return@register true
            if (amount < entity.health) return@register true

            return@register if (entity.isSleeping) {
                val dreamWorld =
                    entity.server.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier.of(MOD_ID, DREAM_WORLD_PATH)))
                entity.teleportTo(TeleportTarget(dreamWorld, entity) {})
                entity.health = entity.maxHealth
                false
            } else if (entity.world.registryKey.value.path == DREAM_WORLD_PATH) {
                entity.teleportTo(TeleportTarget(entity.server.overworld, entity) {})
                false
            } else
                true
        }

        logger.info("Lunatic loaded!")
    }
}