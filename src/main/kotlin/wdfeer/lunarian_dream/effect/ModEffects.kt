package wdfeer.lunarian_dream.effect

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Colors
import net.minecraft.util.Identifier
import wdfeer.lunarian_dream.LunarianDream

fun LunarianDream.initializeEffects() {
    Registry.register(Registries.STATUS_EFFECT, Identifier(MOD_ID, "confidence"), Confidence)
    Registry.register(Registries.STATUS_EFFECT, Identifier(MOD_ID, "fear"), Fear)
}

private object Confidence : StatusEffect(StatusEffectCategory.BENEFICIAL, Colors.WHITE)
private object Fear : StatusEffect(StatusEffectCategory.BENEFICIAL, Colors.RED)
