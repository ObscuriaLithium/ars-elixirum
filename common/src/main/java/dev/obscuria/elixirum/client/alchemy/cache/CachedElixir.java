package dev.obscuria.elixirum.client.alchemy.cache;

import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.profiles.ConfiguredRecipe;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;
import dev.obscuria.elixirum.common.network.ServerboundSetChromaRequest;
import dev.obscuria.elixirum.common.network.ServerboundSetStyleRequest;
import dev.obscuria.elixirum.common.world.ItemStackCache;
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
        ArsElixirumHelper.setStyle(get(), configured.getStyle());
        ItemStackCache.markDirty(get());
        FragmentumNetworking.sendToServer(new ServerboundSetStyleRequest(uuid(), configured.getStyle()));
    }

    public void setChromaSynced(Chroma chroma) {
        this.configured.chroma().set(chroma);
        ArsElixirumHelper.setChroma(get(), chroma);
        ItemStackCache.markDirty(get());
        FragmentumNetworking.sendToServer(new ServerboundSetChromaRequest(uuid(), chroma));
    }

    public ItemStack get() {
        return stack.get();
    }

    public AlchemyRecipe recipe() {
        return configured.recipe();
    }

    public UUID uuid() {
        return configured.recipe().uuid();
    }

    public boolean isInCollection() {
        return ClientAlchemy.INSTANCE.localProfile().collection().hasRecipe(recipe());
    }

    public boolean isSame(CachedElixir other) {
        return recipe().isSame(other.recipe());
    }
}
