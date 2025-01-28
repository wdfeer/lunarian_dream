package wdfeer.lunarian_dream

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.util.Identifier
import java.util.*

class DoremyRenderer(private val ctx: EntityRendererFactory.Context?) : EntityRenderer<Doremy>(ctx) {
    override fun getTexture(entity: Doremy?): Identifier = Identifier(LunarianDream.MOD_ID, "textures/entity/doremy.png")

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
            object : OtherClientPlayerEntity(entity.world as ClientWorld, GameProfile(UUID.randomUUID(), "Doremy Sweet")) {
                override fun getSkinTexture(): Identifier = getTexture(entity)
            }
        clientPlayerEntity.setPosition(entity.pos)
        clientPlayerEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, MinecraftClient.getInstance().player?.pos)

        renderer.render(clientPlayerEntity, yaw, tickDelta, matrices, vertexConsumers, light)
    }
}