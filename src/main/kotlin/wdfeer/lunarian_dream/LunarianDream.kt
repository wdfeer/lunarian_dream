package wdfeer.lunarian_dream

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object LunarianDream : ModInitializer {
    const val MOD_ID = "lunarian_dream"
    val logger = LoggerFactory.getLogger("lunarian_dream")

    override fun onInitialize() {
        initializeEnchantments()
        initializeBlocks()
        initializeDoremyEntity()
        initializeDreamWorld()
        logger.info("LunarianDream loaded!")
    }
}
