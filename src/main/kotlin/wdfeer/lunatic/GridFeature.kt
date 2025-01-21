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