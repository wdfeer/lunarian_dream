package wdfeer.lunarian_dream.item

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import wdfeer.lunarian_dream.LunarianDream

fun LunarianDream.initializeItems() {
    Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "nightmare_shard"), NightmareShard)
}

private object NightmareShard : Item(FabricItemSettings().rarity(Rarity.RARE).fireproof())