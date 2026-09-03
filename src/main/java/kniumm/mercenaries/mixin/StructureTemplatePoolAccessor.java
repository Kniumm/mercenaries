package kniumm.mercenaries.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolAccessor {

    @Accessor("rawTemplates")
    List<Pair<StructurePoolElement, Integer>> mercenaries$getRawTemplates();

    @Accessor("rawTemplates")
    @Mutable
    void mercenaries$setRawTemplates(
            List<Pair<StructurePoolElement, Integer>> value
    );

    @Accessor("templates")
    ObjectArrayList<StructurePoolElement> mercenaries$getTemplates();

    @Accessor("templates")
    @Mutable
    void mercenaries$setTemplates(
            ObjectArrayList<StructurePoolElement> value
    );
}