package wdfeer.lunatic

import net.minecraft.util.math.intprovider.IntProvider
import net.minecraft.util.math.intprovider.IntProviderType
import net.minecraft.util.math.random.Random
import net.minecraft.world.dimension.DimensionType
import java.util.*

object DreamWorld {
    val type = DimensionType(
        OptionalLong.empty(),
        false,
        false,
        false,
        false,
        0.01,
        false,
        true,
        0,
        255,
        255,
        null,
        null,
        8f,
        DimensionType.MonsterSettings(
            true, false,
            object : IntProvider() {
                override fun get(random: Random?): Int = 0
                override fun getMin(): Int = 0
                override fun getMax(): Int = 0
                override fun getType(): IntProviderType<*> = IntProviderType.CONSTANT
            },
            0,
        )
    )
}