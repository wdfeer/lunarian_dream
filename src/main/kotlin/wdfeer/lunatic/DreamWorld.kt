package wdfeer.lunatic

import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier
import net.minecraft.util.math.intprovider.IntProvider
import net.minecraft.util.math.intprovider.IntProviderType
import net.minecraft.util.math.random.Random
import net.minecraft.world.biome.Biome
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.gen.chunk.FlatChunkGenerator
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
import java.util.*

object DreamWorld {
    private val identifier = Identifier.of(Lunatic.MOD_ID, "dream_world")

    private val type = DimensionType(
        OptionalLong.empty(),
        false,
        false,
        false,
        false,
        0.01,
        false,
        true,
        0,
        256,
        256,
        null,
        null,
        8f,
        DimensionType.MonsterSettings(
            true, false,
            object : IntProvider() {
                override fun get(random: Random?): Int = 0
                override fun getMin(): Int = 0
                override fun getMax(): Int = 0
                override fun getType(): IntProviderType<*> = IntProviderType.CONSTANT
            },
            0,
        )
    )
    private val typeEntry = RegistryEntry.of(type)

    private val biome = Biome.Builder().build()
    private val biomeEntry = RegistryEntry.of(biome)
    private val chunkGenerator = FlatChunkGenerator(FlatChunkGeneratorConfig(Optional.empty(), biomeEntry, mutableListOf()))

    private val options = DimensionOptions(typeEntry, chunkGenerator)

    init {
        Registry.register(dimensionTypeRegistry, identifier, type)
        Registry.register(dimensionOptionsRegistry, identifier, options)
    }
}