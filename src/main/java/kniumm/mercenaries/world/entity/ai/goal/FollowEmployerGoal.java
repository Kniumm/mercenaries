package kniumm.mercenaries.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Optional;

import kniumm.mercenaries.Mercenaries;
import kniumm.mercenaries.world.effect.MobEffects;
import kniumm.mercenaries.world.entity.Allegiant;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FollowEmployerGoal extends Goal {
    private final Allegiant allegiant;
    private @Nullable Player employer;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;

    public FollowEmployerGoal(final @NonNull Allegiant allegiant, final double speedModifier, final float startDistance, final float stopDistance) {
        this.allegiant = allegiant;
        this.speedModifier = speedModifier;
        this.navigation = allegiant.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

        if (!(allegiant.getNavigation() instanceof GroundPathNavigation) && !(allegiant.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowEmployerGoal");
        }
    }

    @Override
    public boolean canUse() {
        Optional<Player> employer = this.allegiant.fetchEmployer();
        if (employer.isEmpty()) {
            return false;
        }

        if (!employer.get().hasEffect(MobEffects.RALLYING)) {
            this.allegiant.resetEmployer();

            this.employer = null;

            return false;
        }

        if (this.allegiant.unableToMoveToEmployer()) {
            return false;
        }

        if (this.allegiant.distanceToSqr(employer.get()) < this.startDistance * this.startDistance) {
            return false;
        }

        this.employer = employer.get();

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else {
            return !this.allegiant.unableToMoveToEmployer() && this.employer != null && !(this.allegiant.distanceToSqr(this.employer) <= this.stopDistance * this.stopDistance);
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.allegiant.getPathfindingMalus(PathType.WATER);
        this.allegiant.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.employer = null;
        this.navigation.stop();
        this.allegiant.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        boolean isEmployerFarAway = this.allegiant.shouldTryTeleportToEmployer();

        if (!isEmployerFarAway && this.employer != null) {
            this.allegiant.getLookControl().setLookAt(this.employer, 10.0F, this.allegiant.getMaxHeadXRot());
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (isEmployerFarAway) {
                this.allegiant.tryToTeleportToEmployer();
            } else if (this.employer != null) {
                this.navigation.moveTo(this.employer, this.speedModifier);
            }
        }
    }
}
