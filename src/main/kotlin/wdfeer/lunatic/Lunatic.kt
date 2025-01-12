package wdfeer.lunatic

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Lunatic : ModInitializer {
	const val MOD_ID = "lunatic"
    private val logger = LoggerFactory.getLogger("lunatic")

	override fun onInitialize() {
		logger.info("Lunatic loaded!")
	}
}