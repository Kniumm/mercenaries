package kniumm.mercenaries;

import kniumm.mercenaries.allegiance.Allegiance;
import kniumm.mercenaries.world.item.AllegianceHornItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.InstrumentComponent;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class ModItems {
    public static final Item MERCENARY_SPAWN_EGG = register(
            ModItemIds.MERCENARY_SPAWN_EGG,
            SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntityTypes.MERCENARY)
    );

    public static final Item ALLEGIANCE_HORN = register(
            ModItemIds.ALLEGIANCE_HORN,
            AllegianceHornItem::new,
            new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1).durability(1).delayedComponent(DataComponents.INSTRUMENT, (context) -> new InstrumentComponent(context.getOrThrow(Instruments.CALL_GOAT_HORN)))
    );

    public static Item register(ResourceKey<Item> itemKey, @NonNull Function<Item.Properties, Item> itemFactory, Item.@NonNull Properties settings) {
        Item item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register((creativeTab) -> {
            creativeTab.insertAfter(Items.WANDERING_TRADER_SPAWN_EGG, ModItems.MERCENARY_SPAWN_EGG);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((creativeTab) -> {
            creativeTab.insertAfter(Items.BANNER.white(), Allegiance.getAllegianceBannerInstance());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register((creativeTab) -> {
            creativeTab.insertAfter(Items.SHIELD, Allegiance.getAllegianceShieldInstance());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((creativeTab) -> {
            creativeTab.insertAfter(Items.GOAT_HORN, ModItems.ALLEGIANCE_HORN);
        });
    }
}