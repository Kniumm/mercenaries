package kniumm.mercenaries;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mercenaries implements ModInitializer {
	public static final String MOD_ID = "mercenaries";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEntityTypes.initialize();
		ModEntityTypes.registerAttributes();
	}

	@Contract("_ -> new")
	public static @NonNull Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
