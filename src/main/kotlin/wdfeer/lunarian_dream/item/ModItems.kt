package wdfeer.lunarian_dream.item

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import wdfeer.lunarian_dream.LunarianDream

fun LunarianDream.initializeItems() {
    Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "nightmare_shard"), NightmareShard)
    Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "gravity_controller"), GravityController)
}

private object NightmareShard : Item(FabricItemSettings().rarity(Rarity.RARE).fireproof())

private object GravityController : Item(FabricItemSettings().rarity(Rarity.RARE).fireproof()) {
    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        user?.itemCooldownManager?.set(this, 60)
        user?.addStatusEffect(StatusEffectInstance(StatusEffects.LEVITATION, 30, 1))
        return TypedActionResult.success(user?.getStackInHand(hand))
    }
}
