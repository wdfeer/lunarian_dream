package wdfeer.lunatic

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Lunatic : ModInitializer {
    private val logger = LoggerFactory.getLogger("lunatic")

	override fun onInitialize() {
		DreamWorld
		logger.info("Dream World loaded!")
	}
}