package wdfeer.lunatic

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

fun initializeEnchantments() {
    Registry.register(Registries.ENCHANTMENT, Identifier.of(Lunatic.MOD_ID, "dream_repair"), DreamRepair)
}

private object DreamRepair : Enchantment(Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, arrayOf()) {
    override fun isTreasure(): Boolean = true
}