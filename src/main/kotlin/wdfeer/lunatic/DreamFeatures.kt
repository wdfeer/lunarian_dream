package wdfeer.lunatic

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.util.FeatureContext

class GridFeatureConfig(val blockId: Identifier) : FeatureConfig {
    val codec: Codec<GridFeatureConfig> = RecordCodecBuilder.create {
        it.group(
            Identifier.CODEC.fieldOf("block").forGetter { blockId }).apply(it, ::GridFeatureConfig)
    }
}

class GridFeature() : Feature<GridFeatureConfig>(GridFeatureConfig(Registries.BLOCK.getId(Blocks.OBSIDIAN)).codec) {
    override fun generate(context: FeatureContext<GridFeatureConfig>): Boolean {
        val world = context.world
        val origin = context.origin
        val block = Registries.BLOCK[context.config.blockId]

        for (y in origin.y until world.topY) {
            if (y % 16 == 0) {
                val pos = origin.up(y)
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

class DungeonFeature() :
    Feature<DungeonFeatureConfig>(DungeonFeatureConfig(Registries.BLOCK.getId(Blocks.BEDROCK)).codec) {
    override fun generate(context: FeatureContext<DungeonFeatureConfig>): Boolean {
        val world = context.world
        val origin = context.origin.withY((16 until world.topY step 16).toList().random())
        val block = Registries.BLOCK[context.config.outerBlock]
        val size = 16

        // Make hollow cube
        for (a in 0 until 16) {
            for (b in 0 until 16) {
                run { // Top and Bottom
                    val point = origin.west(a).north(b)
                    world.setBlockState(point, block.defaultState, Block.FORCE_STATE)
                    world.setBlockState(point.up(size), block.defaultState, Block.FORCE_STATE)
                }
                run { // West and East
                    val point = origin.up(a).north(b)
                    world.setBlockState(point, block.defaultState, Block.FORCE_STATE)
                    world.setBlockState(point.west(size), block.defaultState, Block.FORCE_STATE)
                }
                run { // North and South
                    val point = origin.up(a).west(b)
                    world.setBlockState(point, block.defaultState, Block.FORCE_STATE)
                    world.setBlockState(point.north(size), block.defaultState, Block.FORCE_STATE)
                }
            }
        }

        return true
    }
}