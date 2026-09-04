package kniumm.mercenaries;

import kniumm.mercenaries.allegiance.Allegiance;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class ModItems {
    public static final Item MERCENARY_SPAWN_EGG = register(
            ModItemIds.MERCENARY_SPAWN_EGG,
            SpawnEggItem::new,
            new Item.Properties().spawnEgg(ModEntityTypes.MERCENARY)
    );

    public static Item register(ResourceKey<Item> itemKey, @NonNull Function<Item.Properties, Item> itemFactory, Item.@NonNull Properties settings) {
        Item item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register((creativeTab) -> {
            creativeTab.accept(ModItems.MERCENARY_SPAWN_EGG);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((creativeTab) -> {
            creativeTab.accept(Allegiance.getAllegianceBannerInstance());
        });
    }
}