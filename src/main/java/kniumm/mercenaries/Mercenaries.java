package kniumm.mercenaries;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kniumm.mercenaries.allegiance.Allegiance;
import kniumm.mercenaries.mercenary.Mercenary;
import kniumm.mercenaries.mixin.StructureTemplatePoolAccessor;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Mercenaries implements ModInitializer {
	public static final String MOD_ID = "mercenaries";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEntityTypes.initialize();
		ModEntityTypes.registerAttributes();
		ModItems.initialize();

		ServerLifecycleEvents.SERVER_STARTING.register(ModStructurePoolElements::initialize);
		ServerLifecycleEvents.SERVER_STARTING.register(Allegiance::initialize);

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof Mercenary mercenary && mercenary.shouldRandomize()) {
				mercenary.setShouldRandomize(false);

				mercenary.finalizeSpawn(
						world,
						world.getCurrentDifficultyAt(mercenary.blockPosition()),
						EntitySpawnReason.STRUCTURE,
						null
				);
			}
		});
	}

	@Contract("_ -> new")
	public static @NonNull Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
