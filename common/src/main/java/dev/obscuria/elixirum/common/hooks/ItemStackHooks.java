package dev.obscuria.elixirum.common.hooks;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientProperties;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
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
        if (player == null || !player.level().isClientSide) return;
        final var properties = ClientAlchemy.getIngredients().getProperties(stack.getItem());
        if (properties.isEmpty()) return;
        final var getter = player.registryAccess().lookupOrThrow(ElixirumRegistries.ESSENCE);
        consumer.accept(Component
                .translatable("elixirum.alchemy_properties.title")
                .withStyle(ChatFormatting.GRAY));
        appendAlchemyProperties(properties, getter, stack, consumer);
    }

    static void appendAlchemyProperties(IngredientProperties properties,
                                        HolderGetter<Essence> getter,
                                        ItemStack stack,
                                        Consumer<Component> consumer) {

        final var discovered = properties.getEssences(getter).object2IntEntrySet().stream()
                .filter(entry -> ClientAlchemy.getProfile().isDiscovered(stack.getItem(), entry.getKey()))
                .toList();

        discovered.forEach(entry -> consumer.accept(Component
                .translatable("elixirum.alchemy_properties.essence",
                        entry.getIntValue(),
                        entry.getKey().value().getName())
                .withStyle(ChatFormatting.LIGHT_PURPLE)));

        if (discovered.size() >= properties.getEssences().size()) {
            properties.getAffixes().forEach(affix -> consumer.accept(Component
                    .translatable("elixirum.alchemy_properties.affix",
                            affix.getDescription())
                    .withStyle(ChatFormatting.GOLD)));
        } else {
            consumer.accept(Component
                    .translatable("elixirum.alchemy_properties.unknown")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
