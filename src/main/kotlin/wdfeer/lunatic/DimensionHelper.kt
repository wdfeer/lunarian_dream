package wdfeer.lunatic

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType

val dimensionTypeRegistry: Registry<DimensionType> = Registries.REGISTRIES[RegistryKeys.DIMENSION_TYPE.registry]
        as Registry<DimensionType>
val dimensionOptionsRegistry: Registry<DimensionOptions> = Registries.REGISTRIES[RegistryKeys.DIMENSION.registry]
        as Registry<DimensionOptions>