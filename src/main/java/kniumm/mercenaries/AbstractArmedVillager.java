package kniumm.mercenaries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

public abstract class AbstractArmedVillager extends AbstractVillager {
    public AbstractArmedVillager(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    public AbstractIllager.IllagerArmPose getArmPose() {
        return AbstractIllager.IllagerArmPose.CROSSED;
    }
}
