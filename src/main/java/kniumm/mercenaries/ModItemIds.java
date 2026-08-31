package kniumm.mercenaries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

public class ModItemIds {
    public static final ResourceKey<Item> MERCENARY_SPAWN_EGG = create("mercenary_spawn_egg");

    private static @NonNull ResourceKey<Item> create(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, name);
        return ResourceKey.create(Registries.ITEM, id);
    }
}