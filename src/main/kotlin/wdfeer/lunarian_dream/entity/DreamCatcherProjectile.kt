package wdfeer.lunarian_dream.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import wdfeer.lunarian_dream.LunarianDream
import wdfeer.lunarian_dream.item.DreamCatcher

fun LunarianDream.initializeDreamCatcherProjectile() {
    Registry.register(Registries.ENTITY_TYPE, Identifier.of(MOD_ID, ID), type)
}

private const val ID = "dream_catcher_projectile"
private val type: EntityType<DreamCatcherProjectile> =
    EntityType.Builder.create({ _, world -> DreamCatcherProjectile(world, null) }, SpawnGroup.MISC)
        .setDimensions(0.5f, 0.5f).build(ID)

class DreamCatcherProjectile(world: World, shooter: LivingEntity?) : ProjectileEntity(
    type,
    world
) {
    init {
        owner = shooter
        if (shooter != null) setVelocity(shooter, shooter.pitch, shooter.yaw, 0.0f, 1.5f, 0.5f)
    }

    override fun onCollision(hitResult: HitResult) {
        if (hitResult is EntityHitResult) {
            val target = hitResult.entity as? LivingEntity ?: return
            if (target != owner) target.damage(world.damageSources.magic(), DreamCatcher.DAMAGE)

            discard()
        }

        super.onCollision(hitResult)
    }

    override fun isCollidable(): Boolean = true

    override fun initDataTracker() {}
}