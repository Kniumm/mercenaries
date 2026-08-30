package kniumm.mercenaries;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class DefendVillageTargetGoal extends TargetGoal {
    private final PathfinderMob mob;
    private @Nullable LivingEntity potentialTarget;
    private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0F);

    public DefendVillageTargetGoal(final PathfinderMob mob) {
        super(mob, false, true);
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        AABB grow = this.mob.getBoundingBox().inflate(10.0F, 8.0F, 10.0F);
        ServerLevel level = getServerLevel(this.mob);
        List<? extends LivingEntity> villagers = level.getNearbyEntities(Villager.class, this.attackTargeting, this.mob, grow);
        List<Player> players = level.getNearbyPlayers(this.attackTargeting, this.mob, grow);

        for(LivingEntity livingEntity : villagers) {
            Villager villager = (Villager)livingEntity;

            for(Player player : players) {
                int reputation = villager.getPlayerReputation(player);
                if (reputation <= -100) {
                    this.potentialTarget = player;
                }
            }
        }

        if (this.potentialTarget == null) {
            return false;
        } else {
            LivingEntity var12 = this.potentialTarget;
            if (var12 instanceof Player player) {
                return !player.isSpectator() && !player.isCreative();
            }

            return true;
        }
    }

    public void start() {
        this.mob.setTarget(this.potentialTarget);
        super.start();
    }
}
