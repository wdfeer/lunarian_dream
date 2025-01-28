package wdfeer.lunarian_dream

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

fun initializeEnchantments() {
    Registry.register(Registries.ENCHANTMENT, Identifier.of(LunarianDream.MOD_ID, "dream_repair"), DreamRepair)
}

private object DreamRepair : Enchantment(Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, arrayOf()) {
    init {
        EntitySleepEvents.ALLOW_RESETTING_TIME.register {
            if (it is ServerPlayerEntity) it.inventory.apply {
                (0 until size()).map { i ->
                    getStack(i)
                }.filter { stack ->
                    EnchantmentHelper.fromNbt(stack.enchantments).containsKey(DreamRepair)
                }.forEach { stack ->
                    stack.damage /= 2
                }
            }
            true
        }
    }

    override fun isTreasure(): Boolean = true
}