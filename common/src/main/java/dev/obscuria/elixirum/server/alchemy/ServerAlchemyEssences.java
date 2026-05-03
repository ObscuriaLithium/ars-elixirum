package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.elixirum.common.alchemy.Diff;
import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyEssences;
import dev.obscuria.elixirum.server.alchemy.generation.EssenceGenerationPipeline;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ServerAlchemyEssences extends AbstractAlchemyEssences {

    ServerAlchemyEssences() {}

    public void load(MinecraftServer server) {
        var packed = AlchemyCodex.ESSENCES.load(server);
        this.registry.fill(packed.essences());
        regenerate();
    }

    public void save(MinecraftServer server) {
        AlchemyCodex.ESSENCES.save(server, pack());
    }

    public void regenerate() {

        var added = 0;
        var remove = 0;
        var updated = 0;

        for (var entry : EssenceGenerationPipeline.generate().entrySet()) {
            var effect = entry.getKey();
            var operation = entry.getValue();
            @Nullable var existing = registry.get(effect.key().location());
            @Nullable var desired = operation.value();

            if (Objects.equals(existing, desired)) continue;

            if (existing != null && desired == null) {
                registry.remove(effect.key().location());
                remove += 1;
                continue;
            }

            if (existing == null) {
                registry.register(effect.key().location(), desired);
                added += 1;
                continue;
            }

            registry.remove(effect.key().location());
            registry.register(effect.key().location(), desired);
            updated += 1;
        }

        this.generationResult = Diff.create(added, true, remove, true, updated, false);
    }
}
