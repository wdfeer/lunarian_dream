package wdfeer.lunatic

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Lunatic : ModInitializer {
    const val MOD_ID = "lunatic"
    val logger = LoggerFactory.getLogger("lunatic")

    override fun onInitialize() {
        initializeDreamWorld()
        logger.info("Lunatic loaded!")
    }
}