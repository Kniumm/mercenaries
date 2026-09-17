package kniumm.mercenaries.world.entity;

import kniumm.mercenaries.AbstractArmedVillager;
import kniumm.mercenaries.allegiance.Allegiance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public abstract class Allegiant extends AbstractArmedVillager {
    private boolean canJoinRally;
    private Allegiance allegiance;
    private int wave;
    private Optional<Player> employer;

    protected Allegiant(EntityType<? extends AbstractArmedVillager> type, Level level) {
        super(type, level);

        this.resetEmployer();
    }

    public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
        this.setCanJoinRally(spawnReason != EntitySpawnReason.NATURAL);
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    @Override
    public boolean removeWhenFarAway(final double distSqr) {
        return this.getCurrentRally() == null && super.removeWhenFarAway(distSqr);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCurrentRally() != null;
    }

    public abstract void applyRallyBuffs(final ServerLevel level, final int wave, final boolean isCaptain);

    public boolean canJoinRally() {
        return this.canJoinRally;
    }

    public void setCanJoinRally(final boolean canJoinRaid) {
        this.canJoinRally = canJoinRaid;
    }

    public void setCurrentRally(final @Nullable Allegiance allegiance) {
        this.allegiance = allegiance;
    }

    public @Nullable Allegiance getCurrentRally() {
        return this.allegiance;
    }

    public void resetEmployer() {
        this.employer = Optional.empty();
    }

    public Optional<Player> fetchEmployer() {
        if (this.employer.isPresent()) {
            return this.employer;
        }

        Allegiance rally = this.getCurrentRally();

        if (rally == null || rally.isStopped()) {
            return Optional.empty();
        }

        MinecraftServer server = this.level().getServer();

        if (server == null) {
            return Optional.empty();
        }

        this.employer = Optional.ofNullable(rally.getPlayer(server));

        return employer;
    }

    public void tryToTeleportToEmployer() {
        Optional<Player> employer = this.fetchEmployer();

        employer.ifPresent(player -> this.teleportToAroundBlockPos(player.blockPosition()));
    }

    public boolean shouldTryTeleportToEmployer() {
        Optional<Player> employer = this.fetchEmployer();

        return employer.isPresent() && this.distanceToSqr(employer.get()) >= 144.0;
    }

    private void teleportToAroundBlockPos(final BlockPos targetPos) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int xd = this.random.nextIntBetweenInclusive(-3, 3);
            int zd = this.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(xd) >= 2 || Math.abs(zd) >= 2) {
                int yd = this.random.nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(targetPos.getX() + xd, targetPos.getY() + yd, targetPos.getZ() + zd)) {
                    return;
                }
            }
        }
    }

    private boolean maybeTeleportTo(final int x, final int y, final int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        }

        this.snapTo(x + 0.5, y, z + 0.5, this.getYRot(), this.getXRot());
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(final BlockPos pos) {
        PathType pathType = WalkNodeEvaluator.getPathTypeStatic(this, pos);
        if (pathType != PathType.WALKABLE) {
            return false;
        }

        BlockState blockStateBelow = this.level().getBlockState(pos.below());
        if (blockStateBelow.getBlock() instanceof LeavesBlock) {
            return false;
        }

        BlockPos delta = pos.subtract(this.blockPosition());
        return this.level().noCollision(this, this.getBoundingBox().move(delta));
    }

    public final boolean unableToMoveToEmployer() {
        return this.isPassenger() || this.mayBeLeashed() || this.fetchEmployer().isPresent() && this.fetchEmployer().get().isSpectator();
    }

    public void setWave(final int wave) {
        this.wave = wave;
    }

    public int getWave() {
        return this.wave;
    }
}
