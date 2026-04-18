package dev.obscuria.elixirum.api;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.profiles.AlchemyProfileView;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.core.HolderOwner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface Alchemy extends HolderOwner<Essence> {

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

    AlchemyEssencesView essences();

    AlchemyIngredientsView ingredients();

    AlchemyProfileView profileOf(Player player);
}
