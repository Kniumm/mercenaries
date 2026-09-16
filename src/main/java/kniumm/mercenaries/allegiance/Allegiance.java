package kniumm.mercenaries.allegiance;

import kniumm.mercenaries.Mercenaries;
import kniumm.mercenaries.ModEntityTypes;
import kniumm.mercenaries.world.entity.Allegiant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class Allegiance {
    public static ItemStackTemplate allegianceBannerTemplate;
    public static ItemStackTemplate allegianceShieldTemplate;

    private int groupsSpawned;
    private final RandomSource random = RandomSource.create();
    private BlockPos center;
    private Optional<BlockPos> waveSpawnPos;

    public Allegiance(final BlockPos center, final Difficulty difficulty) {
        this.waveSpawnPos = Optional.empty();
        this.center = center;
        // this.numGroups = this.getNumGroups(difficulty);
    }

    public boolean trySpawnRally(BlockPos center, ServerLevel serverLevel) {
        this.center = center;
        this.waveSpawnPos = this.getValidSpawnPos(serverLevel);

        if (this.waveSpawnPos.isEmpty()) {
            return false;
        }

        this.spawnGroup(serverLevel, this.waveSpawnPos.get());

        return true;
    }

    public static @NonNull BannerPatternLayers getBannerPatternLayers(@NonNull Registry<BannerPattern> bannerPatternRegistry, boolean shield) {
        BannerPatternLayers.Builder builder = new BannerPatternLayers.Builder();

        if (shield) {
            Holder<BannerPattern> halfHorizontal =
                    bannerPatternRegistry.getOrThrow(BannerPatterns.HALF_HORIZONTAL);

            Holder<BannerPattern> halfHorizontalMirror =
                    bannerPatternRegistry.getOrThrow(BannerPatterns.HALF_HORIZONTAL_MIRROR);

            builder.add(halfHorizontal, DyeColor.RED);
            builder.add(halfHorizontalMirror, DyeColor.RED);
        }

        Holder<BannerPattern> rhombusMiddle =
                bannerPatternRegistry.getOrThrow(BannerPatterns.RHOMBUS_MIDDLE);

        Holder<BannerPattern> circleMiddle =
                bannerPatternRegistry.getOrThrow(BannerPatterns.CIRCLE_MIDDLE);

        Holder<BannerPattern> triangleTop =
                bannerPatternRegistry.getOrThrow(BannerPatterns.TRIANGLE_TOP);

        Holder<BannerPattern> border =
                bannerPatternRegistry.getOrThrow(BannerPatterns.BORDER);

        return builder
                .add(rhombusMiddle, DyeColor.WHITE)
                .add(circleMiddle, DyeColor.LIME)
                .add(triangleTop, DyeColor.WHITE)
                .add(border, DyeColor.RED)
                .add(border, DyeColor.WHITE)
                .build();
    }

    public static @NonNull DataComponentPatch getBannerComponentPatch(@NonNull Registry<BannerPattern> bannerPatternRegistry) {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        BannerPatternLayers bannerPatternLayers = getBannerPatternLayers(bannerPatternRegistry, false);

        builder.set(DataComponents.BANNER_PATTERNS, bannerPatternLayers);
        builder.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.BANNER_PATTERNS, true));
        builder.set(DataComponents.ITEM_NAME, Component.translatable("block.mercenaries.allegiance_banner"));
        builder.set(DataComponents.RARITY, Rarity.UNCOMMON);

        return builder.build();
    }

    @Contract("_ -> new")
    public static @NonNull ItemStackTemplate getAllegianceBannerTemplate(Registry<BannerPattern> bannerPatternRegistry) {
        return new ItemStackTemplate(Items.BANNER.red(), getBannerComponentPatch(bannerPatternRegistry));
    }

    public static @NonNull ItemStack getAllegianceBannerInstance() {
        return allegianceBannerTemplate.create();
    }

    public static @NonNull DataComponentPatch getShieldComponentPatch(@NonNull Registry<BannerPattern> bannerPatternRegistry) {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        BannerPatternLayers bannerPatternLayers = getBannerPatternLayers(bannerPatternRegistry, true);

        // HACK: BASE_COLOR overrides ITEM_NAME and TOOLTIP_DISPLAY if set on a banner.
        //       So the shield pattern is white then recolored as red using two red stripes.

        // builder.set(DataComponents.BASE_COLOR, DyeColor.RED);
        builder.set(DataComponents.BANNER_PATTERNS, bannerPatternLayers);
        builder.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.BANNER_PATTERNS, true));
        builder.set(DataComponents.ITEM_NAME, Component.translatable("block.mercenaries.allegiance_shield"));
        builder.set(DataComponents.RARITY, Rarity.UNCOMMON);

        return builder.build();
    }

    @Contract("_ -> new")
    public static @NonNull ItemStackTemplate getAllegianceShieldTemplate(Registry<BannerPattern> bannerPatternRegistry) {
        return new ItemStackTemplate(Items.SHIELD, getShieldComponentPatch(bannerPatternRegistry));
    }

    public static @NonNull ItemStack getAllegianceShieldInstance() {
        return allegianceShieldTemplate.create();
    }

    private Optional<BlockPos> getValidSpawnPos(final ServerLevel level) {
        BlockPos spawnPos = this.findRandomSpawnPos(level, 8);
        return spawnPos != null ? Optional.of(spawnPos) : Optional.empty();
    }

    private @Nullable BlockPos findRandomSpawnPos(final ServerLevel level, final int maxTries) {
        int secondsRemaining = 12;

        float howFar = 0.22F * (float)secondsRemaining - 0.24F;

        BlockPos.MutableBlockPos spawnPos = new BlockPos.MutableBlockPos();

        float startAngle = this.random.nextFloat() * ((float)Math.PI * 2F);

        for(int i = 0; i < maxTries; ++i) {
            float angle = startAngle + (float)Math.PI * (float)i / 8.0F;

            int spawnX = this.center.getX() + Mth.floor(Mth.cos(angle) * 32.0F * howFar) + this.random.nextInt(3) * Mth.floor(howFar);
            int spawnZ = this.center.getZ() + Mth.floor(Mth.sin(angle) * 32.0F * howFar) + this.random.nextInt(3) * Mth.floor(howFar);
            int spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, spawnX, spawnZ);

            Mercenaries.LOGGER.info("spawnX={}", spawnX);
            Mercenaries.LOGGER.info("spawnZ={}", spawnZ);
            Mercenaries.LOGGER.info("spawnY={}", spawnY);
            Mercenaries.LOGGER.info("(Mth.abs(spawnY - this.center.getY()) <= 9) = {}", Mth.abs(spawnY - this.center.getY()) <= 96);
            Mercenaries.LOGGER.info("(!level.isVillage(spawnPos) || secondsRemaining <= 7) = {}", !level.isVillage(spawnPos) || secondsRemaining <= 7);

            if (Mth.abs(spawnY - this.center.getY()) <= 96) {
                spawnPos.set(spawnX, spawnY, spawnZ);

                if (!level.isVillage(spawnPos) || secondsRemaining <= 7) {
                    int delta = 10;

//                    if (level.hasChunksAt(spawnPos.getX() - delta, spawnPos.getZ() - delta, spawnPos.getX() + delta, spawnPos.getZ() + delta) && level.isPositionEntityTicking(spawnPos) && (level.getBlockState(spawnPos.below()).is(Blocks.SNOW) && level.getBlockState(spawnPos).isAir())) {
//                        Mercenaries.LOGGER.info("ok");
//
//                        return spawnPos;
//                    }

                    return spawnPos;
                }
            }
        }

        return null;
    }

    private void spawnGroup(final @NonNull ServerLevel level, final BlockPos pos) {
        boolean leaderSet = false;
        int groupNumber = this.groupsSpawned + 1;
        DifficultyInstance difficulty = level.getCurrentDifficultyAt(pos);

        Mercenaries.LOGGER.info("groupNumber={}", groupNumber);

        for(Allegiance.AllegiantType allegiantType : Allegiance.AllegiantType.VALUES) {
            Mercenaries.LOGGER.info("allegiantType={}", allegiantType);

            int numSpawns = this.getDefaultNumSpawns(allegiantType, groupNumber) + this.getPotentialBonusSpawns(allegiantType, this.random, groupNumber, difficulty);

            Mercenaries.LOGGER.info("numSpawns={}", numSpawns);

            for(int i = 0; i < numSpawns; ++i) {
                Allegiant allegiant = allegiantType.entityType.create(level, EntitySpawnReason.EVENT);

                Mercenaries.LOGGER.info("allegiant={}", allegiant);

                if (allegiant == null) {
                    break;
                }

                //                if (!leaderSet && allegiant.canBeLeader()) {
                //                    allegiant.setPatrolLeader(true);
                //                    this.setLeader(groupNumber, allegiant);
                //                    leaderSet = true;
                //                }

                this.joinRally(level, groupNumber, allegiant, pos, false);
            }
        }

        this.waveSpawnPos = Optional.empty();
    }

    public void joinRally(final ServerLevel level, final int groupNumber, final @NonNull Allegiant allegiant, final @Nullable BlockPos pos, final boolean exists) {
        allegiant.setCurrentRally(this);
        allegiant.setWave(groupNumber);
        allegiant.setCanJoinRally(true);

        if (!exists && pos != null) {
            allegiant.setPos((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)1.0F, (double)pos.getZ() + (double)0.5F);
            allegiant.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.EVENT, null);
            allegiant.applyRallyBuffs(level, groupNumber, false);
            allegiant.setOnGround(true);
            level.addFreshEntityWithPassengers(allegiant);
        }
    }

    private int getDefaultNumSpawns(final Allegiance.@NonNull AllegiantType type, final int wav) {
        return type.spawnsPerWaveBeforeBonus[wav];
    }

    private int getPotentialBonusSpawns(final Allegiance.@NonNull AllegiantType type, final RandomSource random, final int wav, final @NonNull DifficultyInstance difficultyInstance) {
        Difficulty difficulty = difficultyInstance.getDifficulty();
        boolean isEasy = difficulty == Difficulty.EASY;
        boolean isNormal = difficulty == Difficulty.NORMAL;
        int bonusSpawns;

        switch (type) {
            default:
                return 0;
        }
    }

    public static void initialize(@NonNull MinecraftServer server) {
        Registry<BannerPattern> bannerPatternRegistry = server.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN);

        allegianceBannerTemplate = getAllegianceBannerTemplate(bannerPatternRegistry);
        allegianceShieldTemplate = getAllegianceShieldTemplate(bannerPatternRegistry);
    }

    public enum AllegiantType {
        MERCENARY(ModEntityTypes.MERCENARY, new int[]{0, 5});

        public static final Allegiance.AllegiantType[] VALUES = values();
        public final EntityType<? extends Allegiant> entityType;
        public final int[] spawnsPerWaveBeforeBonus;

        AllegiantType(final EntityType<? extends Allegiant> entityType, final int[] spawnsPerWaveBeforeBonus) {
            this.entityType = entityType;
            this.spawnsPerWaveBeforeBonus = spawnsPerWaveBeforeBonus;
        }
    }
}
