package wdfeer.lunarian_dream.item

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

object DreamCatcher : Item(FabricItemSettings().fireproof().rarity(Rarity.UNCOMMON)) {
    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        // TODO: fire laser

        return super.use(world, user, hand)
    }
}