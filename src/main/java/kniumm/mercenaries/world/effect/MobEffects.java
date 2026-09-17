package kniumm.mercenaries.world.effect;

import kniumm.mercenaries.Mercenaries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.jspecify.annotations.NonNull;

public class MobEffects {
    public static final Holder<MobEffect> RALLYING =
            register("rallying", new RallyingMobEffect(MobEffectCategory.NEUTRAL, 0x80221d));

    private static @NonNull Holder<MobEffect> register(String name, MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Mercenaries.id(name), mobEffect);
    }

    public static void initialize() {}
}
