package wdfeer.lunarian_dream.world

import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import kotlin.math.max

data class BossType(
    val entityType: EntityType<out HostileEntity>,
    val count: Int,
    val onCreation: HostileEntity.() -> Unit
)

val bossTypes = run {
    fun HostileEntity.multiplyMaxHp(mult: Double) {
        getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addPersistentModifier(
            EntityAttributeModifier(
                "Dream World Dungeon Boss Health",
                max(mult - 1, 1.0),
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            )
        )
        health = maxHealth
    }

    fun HostileEntity.multiplySpeed(mult: Double) {
        getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.addPersistentModifier(
            EntityAttributeModifier(
                "Dream World Dungeon Boss Speed",
                max(mult - 1, 1.0),
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            )
        )
    }

    listOf(
        BossType(EntityType.SKELETON, 4) {
            multiplyMaxHp(8.0)
            setStackInHand(Hand.MAIN_HAND, ItemStack(Items.BOW).apply {
                addEnchantment(Enchantments.PUNCH, 1)
                addEnchantment(Enchantments.FLAME, 0)
            })
        },
        BossType(EntityType.SKELETON, 1) {
            multiplyMaxHp(12.0)
            multiplySpeed(1.6)
            equipStack(EquipmentSlot.CHEST, ItemStack(Items.CHAINMAIL_CHESTPLATE))
            equipStack(EquipmentSlot.LEGS, ItemStack(Items.CHAINMAIL_LEGGINGS))
            setStackInHand(Hand.MAIN_HAND, ItemStack(Items.DIAMOND_SWORD).apply {
                addEnchantment(Enchantments.SHARPNESS, 4)
            })
        },
        BossType(EntityType.WITHER_SKELETON, 2) {
            multiplyMaxHp(5.0)
            multiplySpeed(1.4)
            setStackInHand(
                Hand.MAIN_HAND, ItemStack(Items.NETHERITE_AXE).apply {
                    addEnchantment(Enchantments.KNOCKBACK, 1)
                    addEnchantment(Enchantments.SHARPNESS, 4)
                })
        },
        BossType(EntityType.ZOMBIE, 3) {
            multiplyMaxHp(5.0)
            multiplySpeed(1.4)
            setStackInHand(
                Hand.MAIN_HAND, ItemStack(Items.STONE_SWORD).apply {
                    addEnchantment(Enchantments.SHARPNESS, 6)
                })
        },
        BossType(EntityType.ZOMBIE_VILLAGER, 1) {
            multiplyMaxHp(20.0)
            multiplySpeed(1.25)
            equipStack(EquipmentSlot.HEAD, ItemStack(Items.IRON_HELMET))
            equipStack(EquipmentSlot.CHEST, ItemStack(Items.IRON_CHESTPLATE))
            equipStack(EquipmentSlot.LEGS, ItemStack(Items.IRON_LEGGINGS))
            equipStack(EquipmentSlot.FEET, ItemStack(Items.IRON_BOOTS))
            setStackInHand(
                Hand.MAIN_HAND, ItemStack(Items.IRON_AXE).apply {
                    addEnchantment(Enchantments.KNOCKBACK, 3)
                    addEnchantment(Enchantments.SHARPNESS, 6)
                })
        },
        BossType(EntityType.BLAZE, 4) {
            multiplyMaxHp(3.0)
            addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, Int.MAX_VALUE, 3))
        },
        BossType(EntityType.CAVE_SPIDER, 3) {
            multiplyMaxHp(2.0)
            multiplySpeed(1.4)
            addStatusEffect(StatusEffectInstance(StatusEffects.INVISIBILITY, Int.MAX_VALUE))
            addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, Int.MAX_VALUE, 1))
        },
        BossType(EntityType.SPIDER, 1) {
            multiplyMaxHp(12.0)
            multiplySpeed(1.25)
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.addPersistentModifier(
                EntityAttributeModifier(
                    "Dream World Dungeon Boss Damage",
                    4.0,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                )
            )
            addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, Int.MAX_VALUE, 0, true, true))
        }
    )
}