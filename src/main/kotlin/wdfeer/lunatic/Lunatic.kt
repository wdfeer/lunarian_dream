package wdfeer.lunatic

import net.fabricmc.api.ModInitializer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.world.dimension.DimensionType
import org.slf4j.LoggerFactory

object Lunatic : ModInitializer {
	const val MOD_ID = "lunatic"
    private val logger = LoggerFactory.getLogger("lunatic")

	override fun onInitialize() {
		DreamWorld
		logger.info("Dream World loaded!")
	}
}