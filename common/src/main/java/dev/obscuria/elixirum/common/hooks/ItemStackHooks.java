package dev.obscuria.elixirum.common.hooks;

import dev.obscuria.elixirum.ElixirumClient;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ItemStackHooks {

    static void getTooltipLines(Item.TooltipContext context,
                                ItemStack stack,
                                @Nullable Player player,
                                TooltipFlag flag,
                                Consumer<Component> consumer) {

        ElixirContents.getOptional(stack).ifPresent(contents -> contents.addToTooltip(context, consumer, flag));
        if (player != null && player.level().isClientSide)
            ElixirumClient.addEssenceTooltip(player, stack, consumer);
    }
}
