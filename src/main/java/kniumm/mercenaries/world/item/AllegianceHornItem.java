package kniumm.mercenaries.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

        if (result == InteractionResult.CONSUME) {
            ItemStack itemStack = player.getItemInHand(hand);

            itemStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        return result;
    }
}
