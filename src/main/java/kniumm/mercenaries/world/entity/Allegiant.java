package kniumm.mercenaries.world.entity;

import kniumm.mercenaries.AbstractArmedVillager;
import kniumm.mercenaries.allegiance.Allegiance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

public abstract class Allegiant extends AbstractArmedVillager {
    private boolean canJoinRally;
    private Allegiance allegiance;
    private int wave;

    protected Allegiant(EntityType<? extends AbstractArmedVillager> type, Level level) {
        super(type, level);
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

    public void setWave(final int wave) {
        this.wave = wave;
    }

    public int getWave() {
        return this.wave;
    }
}
