package dev.obscuria.elixirum;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public final class ElixirumClient {
    private static final long START_TIME = System.currentTimeMillis();
    private static float seconds;

    @ApiStatus.Internal
    public static void init() {

    }

    public static float getSeconds() {
        return seconds;
    }

    public static void addEssenceTooltip(Player player, ItemStack stack, Consumer<Component> consumer) {
        final var getter = player.registryAccess().lookupOrThrow(ElixirumRegistries.ESSENCE);
        final var properties = ClientAlchemy.getIngredients().getProperties(stack.getItem());
        if (properties.isEmpty()) return;
        final var discoveredEssences = properties.getEssences(getter).object2IntEntrySet().stream()
                .filter(entry -> ClientAlchemy.getProfile().isDiscovered(stack.getItem(), entry.getKey()))
                .toList();

        consumer.accept(Component.literal("Alchemy Properties:").withStyle(ChatFormatting.GRAY));
        discoveredEssences.forEach(entry -> consumer.accept(Component.literal(" ")
                .append(Component.literal("x" + entry.getIntValue()))
                .append(Component.literal(" "))
                .append(entry.getKey().value().getName())
                .withStyle(ChatFormatting.LIGHT_PURPLE)));
        if (discoveredEssences.size() < properties.getEssences().size()) {
            consumer.accept(Component.literal(" ???").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            final var affixes = properties.getAffixes();
            if (affixes.isEmpty()) return;
            for (var affix : affixes)
                consumer.accept(Component.literal(" ")
                        .append(affix.getDescription())
                        .withStyle(ChatFormatting.GOLD));
        }
    }

    public static void addRecentElixir(ItemStack stack, ElixirRecipe recipe) {
        ClientAlchemy.addRecentElixir(stack, recipe);
    }

    public static void onRenderTick() {
        seconds = (System.currentTimeMillis() - START_TIME) / 1000f;
    }
}
