package wdfeer.lunarian_dream.world

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.MobSpawnerBlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.ChunkRegion
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext
import wdfeer.lunarian_dream.LunarianDream
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.random.nextInt

class DungeonFeatureConfig(val outerBlock: Identifier) : FeatureConfig {
    val codec: Codec<DungeonFeatureConfig> = RecordCodecBuilder.create {
        it.group(
            Identifier.CODEC.fieldOf("outer_block").forGetter { outerBlock }).apply(it, ::DungeonFeatureConfig)
    }
}

private const val SIZE = 16

class DungeonFeature :
    Feature<DungeonFeatureConfig>(DungeonFeatureConfig(Registries.BLOCK.getId(Blocks.BEDROCK)).codec) {
    override fun generate(context: FeatureContext<DungeonFeatureConfig>): Boolean {
        if (context.origin.let { it.x.absoluteValue + it.z.absoluteValue } < 64) return false
        if (Random.nextFloat() > 0.1f) return false
        val origin = context.origin.withY((16 until context.world.topY step 16).toList().random())

        val generator = DungeonGenerator(context.world, origin, Registries.BLOCK[context.config.outerBlock])
        generator.createHollowCube()
        generator.createSpawners()
        generator.createChests()
        generator.createBoss()

        return true
    }

    companion object {
        fun tryDestroyDungeon(world: ServerWorld, pos: BlockPos) {
            val origin = run {
                val chunkPos = world.getWorldChunk(pos).pos
                BlockPos(chunkPos.x * 16, pos.y / 16 * 16, chunkPos.z * 16)
            }

            // check if dungeon contains enemies
            val entityInCube = world.iterateEntities().firstOrNull { entity ->
                (entity is HostileEntity || entity is Monster) &&
                        entity.isAlive &&
                        entity.blockPos.subtract(origin.run { Vec3i(x, y, z) })
                            .run {
                                // position within 16x16x16 cube
                                listOf(x, y, z).all { it in 1..15 }
                            }
            }
            if (entityInCube != null) return

            for (p in getHollowCubePositions(origin).filter {
                world.getBlockState(it).block == Blocks.BEDROCK
            }) {
                world.breakBlock(p, true)
            }
        }
    }
}

private data class DungeonGenerator(
    val worldAccess: StructureWorldAccess, val origin: BlockPos, val block: Block
)

private fun getHollowCubePositions(origin: BlockPos): List<BlockPos> =
    buildList {
        for (a in 1 until SIZE) {
            for (b in 1 until SIZE) {
                run { // Top and Bottom
                    val point = origin.east(a).south(b)
                    add(point)
                    add(point.up(SIZE))
                }
                run { // East and West
                    val point = origin.up(a).south(b)
                    add(point)
                    add(point.east(SIZE))
                }
                run { // South and North
                    val point = origin.up(a).east(b)
                    add(point)
                    add(point.south(SIZE))
                }
            }
        }
    }

private fun DungeonGenerator.createHollowCube() {
    val hollowCube: List<BlockPos> = getHollowCubePositions(origin).let {
        // make door
        it.minus(it.random())
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
        val spawnerPos = origin.up().east(Random.nextInt(2 until SIZE - 1)).south(Random.nextInt(2 until SIZE - 1))
        worldAccess.setBlockState(spawnerPos, Blocks.SPAWNER.defaultState, Block.FORCE_STATE)
        val blockEntity = worldAccess.getBlockEntity(spawnerPos)
        if (blockEntity is MobSpawnerBlockEntity) {
            blockEntity.setEntityType(entityType, worldAccess.random)
        }
    }
}

private fun DungeonGenerator.createChests() {
    repeat(2) {
        val chestPos = origin.up().east(Random.nextInt(2 until SIZE - 1)).south(Random.nextInt(2 until SIZE - 1))
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
                origin.up(2).east(Random.nextInt(2 until SIZE - 1)).south(Random.nextInt(2 until SIZE - 1))
                    .toCenterPos()
            )
        }
    }

    val type = bossTypes.random()
    val entities = (0 until type.count).mapNotNull { makeEntity(type.entityType)?.apply(type.onCreation) }
    for (e in entities) worldAccess.spawnEntity(e)
}