package kniumm.mercenaries.allegiance;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class Allegiance {
    public static ItemStackTemplate allegianceBannerTemplate;
    public static ItemStackTemplate allegianceShieldTemplate;

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

    public static void initialize(@NonNull MinecraftServer server) {
        Registry<BannerPattern> bannerPatternRegistry = server.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN);

        allegianceBannerTemplate = getAllegianceBannerTemplate(bannerPatternRegistry);
        allegianceShieldTemplate = getAllegianceShieldTemplate(bannerPatternRegistry);
    }
}
