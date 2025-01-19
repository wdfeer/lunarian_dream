package wdfeer.lunatic

import com.google.common.collect.ImmutableSet
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.ladysnake.blabber.Blabber
import java.util.*

fun Lunatic.initializeDoremyEntity() {
    Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, "doremy"), DoremyEntityType)
}

class Doremy(world: World) : Entity(DoremyEntityType, world) {
    init {
        setPosition(0.5, 217.0, 0.5)
        customName = Text.literal("Doremy Sweet")
    }

    override fun initDataTracker() {}
    override fun readCustomDataFromNbt(nbt: NbtCompound?) {}
    override fun writeCustomDataToNbt(nbt: NbtCompound?) {}

    private val playersTalkingTo = mutableListOf<Pair<UUID, Long>>()
    override fun tick() {
        if ((world.time % 20).toInt() == 0) {
            world.players.filterIsInstance<ServerPlayerEntity>()
                .filter { it.distanceTo(this) < 6 }
                .filter {
                    // Looking at doremy
                    it.rotationVector.normalize().subtract(eyePos.subtract(it.eyePos).normalize()).length() < 0.1
                }
                .filter { player ->
                    playersTalkingTo.none { it.first == player.uuid }
                }
                .forEach {
                    Blabber.startDialogue(it, Identifier.of(Lunatic.MOD_ID, "doremy"))
                    playersTalkingTo += it.uuid to world.time
                }

            playersTalkingTo.removeIf { world.time - it.second > 240 }
        }


        super.tick()
    }
}

object DoremyEntityType : EntityType<Doremy>(
    { _, world -> Doremy(world) },
    SpawnGroup.MISC,
    true,
    false,
    true,
    false,
    ImmutableSet.of(),
    EntityDimensions(0.6f, 1.8f, true),
    64,
    1,
    FeatureSet.empty()
)