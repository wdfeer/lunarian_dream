package wdfeer.lunarian_dream.item

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireballEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

object DreamCatcher : Item(FabricItemSettings().fireproof().rarity(Rarity.UNCOMMON)) {
    private const val COOLDOWN = 100

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return if (world !is ServerWorld) super.use(world, user, hand)
        else {
            // todo: make a laser
            val projectile = user.rotationVector.run { FireballEntity(world, user, x, y, z, 2) }
            world.spawnEntity(projectile)
            user.itemCooldownManager!!.set(this, COOLDOWN)

            TypedActionResult.consume(user.getStackInHand(hand))
        }
    }
}