package wdfeer.lunarian_dream

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import wdfeer.lunarian_dream.block.initializeBlocks
import wdfeer.lunarian_dream.enchantment.initializeEnchantments
import wdfeer.lunarian_dream.entity.initializeDoremyEntity
import wdfeer.lunarian_dream.item.initializeItems
import wdfeer.lunarian_dream.world.initializeDreamWorld

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
