package wdfeer.lunatic

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Identifier
import java.util.*

class DoremyRenderer(private val ctx: EntityRendererFactory.Context?) : EntityRenderer<Doremy>(ctx) {
    override fun getTexture(entity: Doremy?): Identifier = Identifier(Lunatic.MOD_ID, "textures/entity/doremy.png")

    override fun render(
        entity: Doremy?,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int
    ) {
        entity ?: return

        val renderer = PlayerEntityRenderer(ctx, true)
        val clientPlayerEntity =
            object : OtherClientPlayerEntity(entity.world as ClientWorld, GameProfile(UUID.randomUUID(), "Doremy")) {
                override fun getSkinTexture(): Identifier = getTexture(entity)
            }


        renderer.render(clientPlayerEntity, yaw, tickDelta, matrices, vertexConsumers, light)
    }
}