// Based on https://github.com/Faboslav/friends-and-foes/blob/master/common/src/main/java/com/faboslav/friendsandfoes/common/util/StructurePoolHelper.java

package kniumm.mercenaries.util;

import kniumm.mercenaries.mixin.StructureTemplatePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class StructurePoolHelper
{
    public static void addLegacyElementToPool(
            @NonNull Registry<StructureTemplatePool> templatePoolRegistry,
            Identifier poolRL,
            Identifier name,
            int weight
    ) {
        StructureTemplatePool pool = templatePoolRegistry.getValue(poolRL);

        if (pool == null) {
            return;
        }

        SinglePoolElement piece = SinglePoolElement.legacy(String.valueOf(name)).apply(StructureTemplatePool.Projection.RIGID);

        addElementToPool(weight, (StructureTemplatePoolAccessor) pool, piece);
    }

    public static void addSingleElementToPool(
            @NonNull Registry<StructureTemplatePool> templatePoolRegistry,
            Identifier poolRL,
            Identifier name,
            int weight
    ) {
        StructureTemplatePool pool = templatePoolRegistry.getValue(poolRL);

        if (pool == null) {
            return;
        }

        SinglePoolElement piece = SinglePoolElement.single(String.valueOf(name)).apply(StructureTemplatePool.Projection.RIGID);

        addElementToPool(weight, (StructureTemplatePoolAccessor) pool, piece);
    }

    private static void addElementToPool(int weight, StructureTemplatePoolAccessor pool, SinglePoolElement piece) {
        for (int i = 0; i < weight; i++) {
            pool.mercenaries$getTemplates().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.mercenaries$getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.mercenaries$setRawTemplates(listOfPieceEntries);
    }
}