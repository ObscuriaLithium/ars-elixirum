package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ElixirMixer {
    public static final Codec<ElixirMixer> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirMixer> STREAM_CODEC;
    private ElixirContents result = ElixirContents.WATER;
    private ElixirRecipe recipe = ElixirRecipe.EMPTY;
    private boolean changed = true;

    public ElixirMixer() {}

    public ElixirMixer(ElixirRecipe recipe) {
        this.recipe = recipe;
    }

    public boolean isEmpty() {
        return this.recipe.isEmpty();
    }

    public void clear() {
        this.recipe = ElixirRecipe.EMPTY;
        this.changed = true;
    }

    public ElixirRecipe getRecipe() {
        return this.recipe;
    }

    public ElixirContents getResult(HolderGetter<Essence> getter) {
        if (this.changed) {
            this.result = ElixirProcessor.brew(getter, recipe);
            this.changed = false;
        }
        return this.result;
    }

    public boolean append(ItemStack stack) {
        var success = false;
        for (var i = 0; i < stack.getCount(); i++)
            if (append(stack.getItem()))
                success = true;
        return success;
    }

    public boolean append(Item item) {
        var essences = Elixirum.getIngredients().getProperties(item);
        if (essences.isEmpty()) return false;
        this.recipe = recipe.with(item);
        this.changed = true;
        return true;
    }

    public int getColor(HolderGetter<Essence> getter) {
        return getResult(getter).color();
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ElixirRecipe.CODEC.fieldOf("recipe").forGetter(ElixirMixer::getRecipe)
        ).apply(instance, ElixirMixer::new));
        STREAM_CODEC = StreamCodec.composite(
                ElixirRecipe.STREAM_CODEC, ElixirMixer::getRecipe,
                ElixirMixer::new);
    }
}
