package kniumm.mercenaries;

import kniumm.mercenaries.mercenary.Mercenary;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jspecify.annotations.NonNull;

public class ModEntityTypes {
    public static final EntityType<Mercenary> MERCENARY = register(
            "mercenary",
            EntityType.Builder.<Mercenary>of(Mercenary::new, MobCategory.CREATURE).sized(0.6F, 1.95F)
    );

    private static <T extends Entity> @NonNull EntityType<T> register(String name, EntityType.@NonNull Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void initialize() {
    }

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(MERCENARY, Mercenary.createAttributes());
    }
}
