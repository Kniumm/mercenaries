package kniumm.mercenaries.client;

import kniumm.mercenaries.Mercenaries;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class ModEntityModelLayers {
    public static final ModelLayerLocation MERCENARY = createMain("mercenary");

    private static final Identifier ARMED_VILLAGER_ARMOR_ID = Identifier.fromNamespaceAndPath(
            Mercenaries.MOD_ID,
            "armed_villager_armor"
    );

    public static final ArmorModelSet<ModelLayerLocation> ARMED_VILLAGER_ARMOR_LAYERS =
            new ArmorModelSet<>(
                    new ModelLayerLocation(ARMED_VILLAGER_ARMOR_ID, "head"),
                    new ModelLayerLocation(ARMED_VILLAGER_ARMOR_ID, "chest"),
                    new ModelLayerLocation(ARMED_VILLAGER_ARMOR_ID, "legs"),
                    new ModelLayerLocation(ARMED_VILLAGER_ARMOR_ID, "feet")
            );

    @Contract("_ -> new")
    private static @NonNull ModelLayerLocation createMain(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, name), "main");
    }

    private static void registerModelLayers() {
        ModelLayerRegistry.registerModelLayer(ModEntityModelLayers.MERCENARY, IllagerModel::createBodyLayer);
    }

    private static void registerArmorModelLayers() {
        ModelLayerRegistry.registerArmorModelLayers(
                ARMED_VILLAGER_ARMOR_LAYERS,
                ArmedVillagerArmorModel::createArmorModelSet
        );
    }

    public static void initialize() {
        registerModelLayers();
        registerArmorModelLayers();
    }
}