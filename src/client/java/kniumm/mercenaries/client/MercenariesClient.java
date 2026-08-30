package kniumm.mercenaries.client;

import kniumm.mercenaries.ModEntityTypes;
import kniumm.mercenaries.client.mercenary.MercenaryRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class MercenariesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModEntityModelLayers.registerModelLayers();
		EntityRenderers.register(ModEntityTypes.MERCENARY, MercenaryRenderer::new);
	}
}
