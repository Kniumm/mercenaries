package kniumm.mercenaries.world.entity.allegiance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kniumm.mercenaries.Mercenaries;
import kniumm.mercenaries.allegiance.Allegiance;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rallies extends SavedData {
    private static final Codec<Rallies> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Allegiance.CODEC.listOf()
                            .fieldOf("rallies")
                            .forGetter(Rallies::getRallies),
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

    private List<Allegiance> rallies = new ArrayList<>();
    private int tick = 0;

    public Rallies() {
        this.setDirty();
    }

    private Rallies(List<Allegiance> rallies, int tick) {
        this.rallies = new ArrayList<>(rallies);
        this.tick = tick;
    }

    public List<Allegiance> getRallies() {
        return this.rallies;
    }

    public void addRally(final Allegiance allegiance) {
        this.rallies.add(allegiance);
    }

    public void tick(@NonNull ServerLevel level) {
        this.tick++;
        Iterator<Allegiance> rallyIterator = this.rallies.iterator();

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
}
