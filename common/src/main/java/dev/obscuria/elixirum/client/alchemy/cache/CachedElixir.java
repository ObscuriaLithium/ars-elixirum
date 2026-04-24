package dev.obscuria.elixirum.client.alchemy.cache;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.recipes.ConfiguredRecipe;
import dev.obscuria.elixirum.common.alchemy.recipes.AlchemyRecipe;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.common.alchemy.styles.StyleVariant;
import dev.obscuria.elixirum.common.network.ServerboundSetChromaRequest;
import dev.obscuria.elixirum.common.network.ServerboundSetStyleRequest;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import dev.obscuria.elixirum.helpers.StyleHelper;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public record CachedElixir(
        AtomicReference<ItemStack> stack,
        ConfiguredRecipe configured
) implements Supplier<ItemStack> {

    public static CachedElixir empty() {
        return new CachedElixir(
                new AtomicReference<>(ItemStack.EMPTY),
                ConfiguredRecipe.empty());
    }

    public CachedElixir(ItemStack stack, ConfiguredRecipe configured) {
        this(new AtomicReference<>(stack), configured);
    }

    public boolean isEmpty() {
        return stack.get().isEmpty();
    }

    public Component displayName() {
        var name = get().getDisplayName();
        return get().getHoverName().copy().withStyle(name.getStyle());
    }

    public void mapStyleSynced(Function<StyleVariant, StyleVariant> mapper) {
        this.configured.mapStyle(mapper);
        StyleHelper.setStyle(get(), configured.getStyle());
        ItemStackCache.markDirty(get());
        FragmentumNetworking.sendToServer(new ServerboundSetStyleRequest(uuid(), configured.getStyle()));
    }

    public void setChromaSynced(Chroma chroma) {
        this.configured.chroma().set(chroma);
        StyleHelper.setChroma(get(), chroma);
        ItemStackCache.markDirty(get());
        FragmentumNetworking.sendToServer(new ServerboundSetChromaRequest(uuid(), chroma));
    }

    public ElixirContents contents() {
        return ContentsHelper.elixir(stack.get());
    }

    public ItemStack get() {
        return stack.get();
    }

    public AlchemyRecipe recipe() {
        return configured.recipe();
    }

    public UUID uuid() {
        return configured.recipe().getUuid();
    }

    public boolean isInCollection() {
        return ClientAlchemy.localProfile().recipeCollection().isSaved(recipe().getUuid());
    }

    public boolean isSame(CachedElixir other) {
        return recipe().equals(other.recipe());
    }
}
