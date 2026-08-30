package kniumm.mercenaries.client;

import kniumm.mercenaries.Mercenaries;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class ModEntityModelLayers {
    public static final ModelLayerLocation MERCENARY = createMain("mercenary");

    @Contract("_ -> new")
    private static @NonNull ModelLayerLocation createMain(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, name), "main");
    }

    public static void registerModelLayers() {
        ModelLayerRegistry.registerModelLayer(ModEntityModelLayers.MERCENARY, IllagerModel::createBodyLayer);
    }
}