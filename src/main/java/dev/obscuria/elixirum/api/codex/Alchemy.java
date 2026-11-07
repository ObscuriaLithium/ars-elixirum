package dev.obscuria.elixirum.api.codex;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface Alchemy {

    static Alchemy guess() {
        return FragmentumProxy.optionalServer()
                .filter(BlockableEventLoop::isSameThread)
                .<Alchemy>map(ServerAlchemy::get)
                .orElse(ClientAlchemy.INSTANCE);
    }

    static Alchemy get(Level level) {
        return level instanceof ServerLevel serverLevel
                ? ServerAlchemy.get(serverLevel.getServer())
                : ClientAlchemy.INSTANCE;
    }

    AlchemyEssences essences();

    AlchemyIngredients ingredients();

    AlchemyProfile profileOf(Player player);
}
