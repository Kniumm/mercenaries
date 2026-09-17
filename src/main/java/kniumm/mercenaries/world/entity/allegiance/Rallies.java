package kniumm.mercenaries.world.entity.allegiance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kniumm.mercenaries.Mercenaries;
import kniumm.mercenaries.allegiance.Allegiance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

public class Rallies extends SavedData {
    private static final Codec<Rallies> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Rallies.RallyWithId.CODEC
                            .listOf()
                            .optionalFieldOf("rallies", List.of())
                            .forGetter(r -> r.rallyMap.int2ObjectEntrySet().stream().map(Rallies.RallyWithId::from).toList()),
                    Codec.INT.fieldOf("tick").forGetter(r -> r.tick)
            ).apply(
                    instance,
                    Rallies::new
            )
    );

    private static final SavedDataType<Rallies> TYPE =
            new SavedDataType<>(
                    Mercenaries.id("rallies"),
                    Rallies::new,
                    CODEC,
                    null
            );

    private final Int2ObjectMap<Allegiance> rallyMap = new Int2ObjectOpenHashMap<>();
    private int tick = 0;

    public Rallies() {
        this.setDirty();
    }

    private Rallies(final List<Rallies.RallyWithId> rallies, int tick) {
        for (Rallies.RallyWithId rally : rallies) {
            this.rallyMap.put(rally.id, rally.rally);
        }

        this.tick = tick;
    }

    public @Nullable Allegiance getRally(final int rallyId) {
        return this.rallyMap.get(rallyId);
    }

    public OptionalInt getId(final Allegiance rally) {
        for (Int2ObjectMap.Entry<Allegiance> entry : this.rallyMap.int2ObjectEntrySet()) {
            if (entry.getValue() == rally) {
                return OptionalInt.of(entry.getIntKey());
            }
        }

        return OptionalInt.empty();
    }

    public int addRally(final Allegiance allegiance) {
        int id = this.rallyMap.size();

        this.rallyMap.put(id, allegiance);

        return id;
    }

    public void tick(@NonNull ServerLevel level) {
        this.tick++;
        Iterator<Allegiance> rallyIterator = this.rallyMap.values().iterator();

        while (rallyIterator.hasNext()) {
            Allegiance rally = rallyIterator.next();

            if (rally.isStopped()) {
                rallyIterator.remove();
                this.setDirty();
            } else {
                rally.tick(level);
            }
        }

        if (this.tick % 200 == 0) {
            this.setDirty();
        }
    }

    public static @NonNull Rallies get(@NonNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    private record RallyWithId(int id, Allegiance rally) {
        public static final Codec<RallyWithId> CODEC = RecordCodecBuilder.create(
                i -> i.group(Codec.INT.fieldOf("id").forGetter(RallyWithId::id), Allegiance.CODEC.fieldOf("rally").forGetter(RallyWithId::rally)).apply(i, RallyWithId::new)
        );

        @Contract("_ -> new")
        public static @NonNull RallyWithId from(final Int2ObjectMap.@NonNull Entry<Allegiance> entry) {
            return new RallyWithId(entry.getIntKey(), entry.getValue());
        }
    }
}
