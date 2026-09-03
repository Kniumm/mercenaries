// Based on https://github.com/Faboslav/friends-and-foes/blob/master/common/src/main/java/com/faboslav/friendsandfoes/common/init/FriendsAndFoesStructurePoolElements.java

package kniumm.mercenaries;

import kniumm.mercenaries.util.StructurePoolHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jspecify.annotations.NonNull;

public class ModStructurePoolElements {
    public static void initialize(@NonNull MinecraftServer server) {
        Registry<StructureTemplatePool> templatePoolRegistry = server.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);

        Identifier plainsPoolLocation = Identifier.parse("minecraft:village/plains/houses");
        Identifier savannaPoolLocation = Identifier.parse("minecraft:village/savanna/houses");
        Identifier taigaPoolLocation = Identifier.parse("minecraft:village/taiga/houses");

        Identifier plainsBarracks = Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, "village/plains/houses/barracks");
        Identifier savannaBarracks = Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, "village/savanna/houses/barracks");
        Identifier taigaBarracks = Identifier.fromNamespaceAndPath(Mercenaries.MOD_ID, "village/taiga/houses/barracks");

        StructurePoolHelper.addLegacyElementToPool(templatePoolRegistry, plainsPoolLocation, plainsBarracks, 5);
        // StructurePoolHelper.addLegacyElementToPool(templatePoolRegistry, savannaPoolLocation, savannaBarracks, 5);
        // StructurePoolHelper.addLegacyElementToPool(templatePoolRegistry, taigaPoolLocation, taigaBarracks, 5);
    }
}