package wdfeer.lunarian_dream.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.world.World
import wdfeer.lunarian_dream.LunarianDream

fun LunarianDream.initializeDreamCatcherProjectile() {
    Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, ID), type)
}

private const val ID = "danmaku_projectile"
private val type: EntityType<DreamCatcherProjectile> =
    EntityType.Builder.create({ _, world -> DreamCatcherProjectile(world) }, SpawnGroup.MISC).build(ID)

class DreamCatcherProjectile(world: World) : ProjectileEntity(
    type,
    world
) {
    override fun initDataTracker() {}
}