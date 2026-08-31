package kniumm.mercenaries.client.mercenary;

import kniumm.mercenaries.Mercenaries;
import kniumm.mercenaries.client.ArmedVillagerModel;
import kniumm.mercenaries.mercenary.Mercenary;
import kniumm.mercenaries.client.ArmedVillagerRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class MercenaryRenderer extends ArmedVillagerRenderer<Mercenary, IllagerRenderState> {
    private static final Identifier MERCENARY = Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, "textures/entity/mercenary.png");

    public MercenaryRenderer(final EntityRendererProvider.Context context) {
        super(context, new ArmedVillagerModel<>(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public @NonNull Identifier getTextureLocation(final @NonNull IllagerRenderState state) {
        return MERCENARY;
    }

    @Override
    public @NonNull IllagerRenderState createRenderState() {
        return new IllagerRenderState();
    }
}
