package wdfeer.lunatic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.MobSpawnerBlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.ChunkRegion
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.random.nextInt

class GridFeatureConfig(val blockId: Identifier) : FeatureConfig {
    val codec: Codec<GridFeatureConfig> = RecordCodecBuilder.create {
        it.group(
            Identifier.CODEC.fieldOf("block").forGetter { blockId }).apply(it, ::GridFeatureConfig)
    }
}

class GridFeature : Feature<GridFeatureConfig>(GridFeatureConfig(Registries.BLOCK.getId(Blocks.OBSIDIAN)).codec) {
    override fun generate(context: FeatureContext<GridFeatureConfig>): Boolean {
        val world = context.world
        val origin = context.origin
        val block = Registries.BLOCK[context.config.blockId]

        for (y in origin.y until world.topY) {
            if (y % 16 == 0) {
                val pos = origin.withY(y)
                repeat(16) { world.setBlockState(pos.west(it), block.defaultState, Block.FORCE_STATE) }
                repeat(15) { world.setBlockState(pos.north(it + 1), block.defaultState, Block.FORCE_STATE) }
            }
        }
        return true
    }
}

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
        val origin = context.origin.withY((16 until context.world.topY step 16).toList().random())

        val generator = DungeonGenerator(context.world, origin, context.config)
        generator.createHollowCube()
        generator.createSpawners()
        generator.createChests()
        generator.createBoss()

        return true
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
            EntityType.BLAZE to EntityType.MAGMA_CUBE,
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
                val lootTableId = Identifier(Lunatic.MOD_ID, "chests/dream_dungeon_treasure")
                blockEntity.setLootTable(lootTableId, worldAccess.random.nextLong())
            }
        }
    }

    private fun DungeonGenerator.createBoss() {
        val bossPos =
            origin.up(2).east(Random.nextInt(2 until SIZE - 1)).north(Random.nextInt(2 until SIZE - 1)).toCenterPos()
        val entityType = when (Random.nextInt(4)) {
            0 -> EntityType.SKELETON
            1 -> EntityType.ZOMBIE
            2 -> EntityType.BLAZE
            3 -> EntityType.PIGLIN_BRUTE
            else -> return
        }

        val entity: HostileEntity = when (worldAccess) {
            is ServerWorld -> entityType.create(worldAccess) ?: return
            is ChunkRegion -> entityType.create(worldAccess.server?.getDreamWorld() ?: return) ?: return

            else -> return
        }
        entity.setPosition(bossPos)

        entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addPersistentModifier(
            EntityAttributeModifier("dream_world_dungeon_boss", 4.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
        entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.addPersistentModifier(
            EntityAttributeModifier("dream_world_dungeon_boss", 0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
        entity.health = entity.maxHealth

        worldAccess.spawnEntity(entity)
    }
}