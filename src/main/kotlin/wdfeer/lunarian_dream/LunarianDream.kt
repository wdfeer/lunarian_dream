package wdfeer.lunarian_dream

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object LunarianDream : ModInitializer {
    const val MOD_ID = "lunarian_dream"
    val logger = LoggerFactory.getLogger("lunarian_dream")

    override fun onInitialize() {
        initializeEnchantments()
        initializeBlocks()
        initializeItems()
        initializeDoremyEntity()
        initializeDreamWorld()
        logger.info("Lunarian Dream loaded!")
    }
}
