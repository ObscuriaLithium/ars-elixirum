package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.core.api.ObscureAPI;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public final class ElixirHolder
{
    public static final Codec<ElixirHolder> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirHolder> STREAM_CODEC;

    private final ElixirRecipe recipe;
    private @Nullable ItemStack stack;
    private @Nullable ElixirStyle style;
    private @Nullable Component name;
    private @Nullable Component description;
    private boolean changed = true;

    public static ElixirHolder empty()
    {
        return ElixirRecipe.EMPTY.asHolder();
    }

    public ElixirHolder(ElixirRecipe recipe)
    {
        this.recipe = recipe;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ElixirHolder(ElixirRecipe recipe,
                         Optional<ElixirStyle> style,
                         Optional<Component> name,
                         Optional<Component> description)
    {
        this.recipe = recipe;
        this.style = style.orElse(null);
        this.name = name.orElse(null);
        this.description = description.orElse(null);
    }

    public boolean isEmpty()
    {
        return this.recipe.isEmpty();
    }

    public boolean is(ElixirRecipe recipe)
    {
        return this.recipe.equals(recipe);
    }

    public ElixirRecipe getRecipe()
    {
        return this.recipe;
    }

    public Optional<ElixirStyle> getStyle()
    {
        return Optional.ofNullable(style);
    }

    public Optional<Component> getName()
    {
        return Optional.ofNullable(name);
    }

    public Optional<Component> getDescription()
    {
        return Optional.ofNullable(description);
    }

    public ElixirHolder setStyle(@Nullable ElixirStyle style)
    {
        this.style = style;
        this.changed = true;
        this.getCachedStack().ifPresent(stack -> getStyle().ifPresentOrElse(
                value -> stack.set(ElixirumDataComponents.ELIXIR_STYLE, value),
                () -> stack.remove(ElixirumDataComponents.ELIXIR_STYLE)));
        return this;
    }

    public ElixirHolder setName(@Nullable Component name)
    {
        this.name = name;
        this.changed = true;
        this.getCachedStack().ifPresent(stack -> getName().ifPresentOrElse(
                value -> stack.set(DataComponents.ITEM_NAME, value),
                () -> stack.remove(DataComponents.ITEM_NAME)));
        return this;
    }

    public ElixirHolder setDescription(@Nullable Component description)
    {
        this.description = description;
        this.changed = true;
        return this;
    }

    public Optional<ItemStack> getCachedStack()
    {
        return ObscureAPI.PLATFORM.isClient() && !isEmpty()
                ? Optional.of(ClientAlchemy.getCache().getOrCreateStack(this))
                : Optional.empty();
    }

    public ItemStack createStack(HolderGetter<Essence> getter)
    {
        final var stack = ElixirumItems.ELIXIR.value().getDefaultInstance();
        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS, recipe.brew(getter));
        return applyAppearance(stack);
    }

    public ItemStack applyAppearance(ItemStack stack)
    {
        getStyle().ifPresent(style -> stack.set(ElixirumDataComponents.ELIXIR_STYLE, style));
        getName().ifPresent(name -> stack.set(DataComponents.ITEM_NAME, name));
        return stack;
    }

    public boolean isSame(ElixirHolder other)
    {
        return this.recipe.equals(other.recipe);
    }

    public void consumeChanges(Consumer<ElixirHolder> consumer)
    {
        if (!changed) return;
        consumer.accept(this);
        this.changed = false;
    }

    static
    {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ElixirRecipe.CODEC.fieldOf("recipe").forGetter(ElixirHolder::getRecipe),
                ElixirStyle.CODEC.optionalFieldOf("style").forGetter(ElixirHolder::getStyle),
                ComponentSerialization.FLAT_CODEC.optionalFieldOf("name").forGetter(ElixirHolder::getName),
                ComponentSerialization.FLAT_CODEC.optionalFieldOf("description").forGetter(ElixirHolder::getDescription)
        ).apply(instance, ElixirHolder::new));
        STREAM_CODEC = StreamCodec.composite(
                ElixirRecipe.STREAM_CODEC, ElixirHolder::getRecipe,
                ByteBufCodecs.optional(ElixirStyle.STREAM_CODEC), ElixirHolder::getStyle,
                ComponentSerialization.OPTIONAL_STREAM_CODEC, ElixirHolder::getName,
                ComponentSerialization.OPTIONAL_STREAM_CODEC, ElixirHolder::getDescription,
                ElixirHolder::new);
    }
}
