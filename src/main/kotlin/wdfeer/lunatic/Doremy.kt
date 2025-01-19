package wdfeer.lunatic

import com.google.common.collect.ImmutableSet
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.text.Text
import net.minecraft.world.World

class Doremy(world: World) : Entity(DoremyEntityType, world) {
    init {
        customName = Text.literal("Doremy Sweet")
        setPos(0.5, 216.0, 0.5)
    }

    override fun initDataTracker() {}
    override fun readCustomDataFromNbt(nbt: NbtCompound?) {}
    override fun writeCustomDataToNbt(nbt: NbtCompound?) {}
}

object DoremyEntityType : EntityType<Doremy>(
    { _, world -> Doremy(world) },
    SpawnGroup.MISC,
    true,
    false,
    true,
    false,
    ImmutableSet.of(),
    EntityDimensions(0.5f, 2f, true),
    64,
    1,
    FeatureSet.empty()
)