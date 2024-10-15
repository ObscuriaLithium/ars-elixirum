package dev.obscuria.elixirum.common.alchemy.brewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public final class IngredientMixer
{
    public static final Codec<IngredientMixer> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientMixer> STREAM_CODEC;
    private ElixirContents result = ElixirContents.WATER;
    private ElixirRecipe recipe = ElixirRecipe.EMPTY;
    private boolean changed = true;

    public IngredientMixer() {}

    public IngredientMixer(ElixirRecipe recipe)
    {
        this.recipe = recipe;
    }

    public boolean isEmpty()
    {
        return this.recipe.isEmpty();
    }

    public void clear()
    {
        this.recipe = ElixirRecipe.EMPTY;
        this.changed = true;
    }

    public ElixirRecipe getRecipe()
    {
        return this.recipe;
    }

    public ElixirContents getResult(HolderGetter<Essence> getter)
    {
        if (changed)
        {
            this.result = BrewingProcessor.brew(getter, recipe);
            this.changed = false;
        }
        return this.result;
    }

    public boolean append(Item item)
    {
        if (recipe.getSize() >= 9) return false;
        var essences = Elixirum.getIngredients().getProperties(item);
        if (essences.isEmpty()) return false;
        this.recipe = recipe.with(item);
        this.changed = true;
        return true;
    }

    public int getColor(HolderGetter<Essence> getter)
    {
        return getResult(getter).color();
    }

    static
    {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ElixirRecipe.CODEC.fieldOf("recipe").forGetter(IngredientMixer::getRecipe)
        ).apply(instance, IngredientMixer::new));
        STREAM_CODEC = StreamCodec.composite(
                ElixirRecipe.STREAM_CODEC, IngredientMixer::getRecipe,
                IngredientMixer::new);
    }
}
