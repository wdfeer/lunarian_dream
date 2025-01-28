package wdfeer.lunarian_dream

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.enchantment.MendingEnchantment
import net.minecraft.enchantment.Enchantments
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.entity.EntityGroup

fun initializeEnchantments() {
    Registry.register(Registries.ENCHANTMENT, Identifier.of(LunarianDream.MOD_ID, "dream_repair"), DreamRepair)
    Registry.register(Registries.ENCHANTMENT, Identifier.of(LunarianDream.MOD_ID, "mental_break"), MentalBreak)
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

    override fun canAccept(other: Enchantment?): Boolean {
        return super.canAccept(other) && other !is MendingEnchantment
    }
}

private object MentalBreak : Enchantment(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, arrayOf()) {
    override fun isTreasure(): Boolean = true

    override fun canAccept(other: Enchantment?): Boolean = super.canAccept(other) &&
            other != Enchantments.SHARPNESS &&
            other != Enchantments.SMITE &&
            other != Enchantments.BANE_OF_ARTHROPODS

    override fun getAttackDamage(level: Int, group: EntityGroup): Float = when (group) {
        EntityGroup.UNDEAD -> 0f
        else -> 4f
    }
}
