package wdfeer.lunarian_dream.world

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.MobSpawnerBlockEntity
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
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkRegion
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext
import wdfeer.lunarian_dream.LunarianDream
import wdfeer.lunarian_dream.world.DungeonFeature.Companion.SIZE
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random
import kotlin.random.nextInt

class DungeonFeatureConfig(val outerBlock: Identifier) : FeatureConfig {
    val codec: Codec<DungeonFeatureConfig> = RecordCodecBuilder.create {
        it.group(
            Identifier.CODEC.fieldOf("outer_block").forGetter { outerBlock }).apply(it, ::DungeonFeatureConfig)
    }
}

class DungeonFeature :
    Feature<DungeonFeatureConfig>(DungeonFeatureConfig(Registries.BLOCK.getId(Blocks.BEDROCK)).codec) {
    companion object {
        const val SIZE = 16
    }

    override fun generate(context: FeatureContext<DungeonFeatureConfig>): Boolean {
        if (context.origin.let { it.x.absoluteValue + it.z.absoluteValue } < 64) return false
        if (Random.nextFloat() > 0.1f) return false
        val origin = context.origin.withY((16 until context.world.topY step 16).toList().random())

        val generator = DungeonGenerator(context.world, origin, context.config)
        generator.createHollowCube()
        generator.createSpawners()
        generator.createChests()
        generator.createBoss()

        return true
    }
}

private data class DungeonGenerator(
    val worldAccess: StructureWorldAccess, val origin: BlockPos, val config: DungeonFeatureConfig
)

private fun DungeonGenerator.createHollowCube() {
    val block = Registries.BLOCK[config.outerBlock]
    val hollowCube: List<BlockPos> = buildList {
        for (a in 1 until SIZE) {
            for (b in 1 until SIZE) {
                run { // Top and Bottom
                    val point = origin.east(a).north(b)
                    add(point)
                    add(point.up(SIZE))
                }
                run { // East and West
                    val point = origin.up(a).north(b)
                    add(point)
                    add(point.east(SIZE))
                }
                run { // North and South
                    val point = origin.up(a).east(b)
                    add(point)
                    add(point.north(SIZE))
                }
            }
        }

        // Make door
        remove(random())
    }
    for (pos in hollowCube) worldAccess.setBlockState(pos, block.defaultState, Block.FORCE_STATE)
}

private fun DungeonGenerator.createSpawners() {
    val entityTypes = listOf(
        EntityType.PHANTOM to EntityType.SHULKER,
        EntityType.PHANTOM to EntityType.MAGMA_CUBE,
    ).random()
    repeat(6) {
        val entityType = if (it % 2 == 0) entityTypes.first else entityTypes.second
        val spawnerPos = origin.up().east(Random.nextInt(2 until SIZE - 1)).north(Random.nextInt(2 until SIZE - 1))
        worldAccess.setBlockState(spawnerPos, Blocks.SPAWNER.defaultState, Block.FORCE_STATE)
        val blockEntity = worldAccess.getBlockEntity(spawnerPos)
        if (blockEntity is MobSpawnerBlockEntity) {
            blockEntity.setEntityType(entityType, worldAccess.random)
        }
    }
}

private fun DungeonGenerator.createChests() {
    repeat(2) {
        val chestPos = origin.up().east(Random.nextInt(2 until SIZE - 1)).north(Random.nextInt(2 until SIZE - 1))
        worldAccess.setBlockState(chestPos, Blocks.CHEST.defaultState, Block.FORCE_STATE)
        val blockEntity = worldAccess.getBlockEntity(chestPos)
        if (blockEntity is ChestBlockEntity) {
            val lootTableId = Identifier(LunarianDream.MOD_ID, "chests/dream_dungeon_treasure")
            blockEntity.setLootTable(lootTableId, worldAccess.random.nextLong())
        }
    }
}

private fun DungeonGenerator.createBoss() {
    fun makeEntity(type: EntityType<out HostileEntity>): HostileEntity? {
        return when (worldAccess) {
            is ServerWorld -> type.create(worldAccess)
            is ChunkRegion -> worldAccess.server?.getDreamWorld()?.let { type.create(it) }
            else -> null
        }?.apply {
            setPersistent()
            setPosition(
                origin.up(2)
                    .east(Random.nextInt(2 until SIZE - 1))
                    .north(Random.nextInt(2 until SIZE - 1))
                    .toCenterPos()
            )
        }
    }

    val type = bossTypes.random()
    val entities = (0 until type.count).mapNotNull { makeEntity(type.entityType)?.apply(type.onCreation) }
    for (e in entities) worldAccess.spawnEntity(e)
}

private data class BossType(
    val entityType: EntityType<out HostileEntity>,
    val count: Int,
    val onCreation: HostileEntity.() -> Unit
)

private val bossTypes = run {
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