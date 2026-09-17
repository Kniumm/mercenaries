package kniumm.mercenaries.world.item;

import kniumm.mercenaries.Mercenaries;
import kniumm.mercenaries.allegiance.Allegiance;
import kniumm.mercenaries.world.effect.MobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class AllegianceHornItem extends InstrumentItem {
    public AllegianceHornItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        var result = super.use(level, player, hand);

        if (result == InteractionResult.CONSUME && level instanceof ServerLevel serverLevel) {
            ItemStack itemStack = player.getItemInHand(hand);

            Allegiance allegiance = new Allegiance(player.blockPosition(), serverLevel.getDifficulty());
            boolean success = allegiance.trySpawnRally(player.blockPosition(), serverLevel);

            if (!success) {
                Mercenaries.LOGGER.warn("Failed to find a suitable spawn position!");

                return InteractionResult.FAIL;
            }

            player.addEffect(new MobEffectInstance(MobEffects.RALLYING, 20 * 60 * 5, 0, false, false, true));
            itemStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        return result;
    }
}
