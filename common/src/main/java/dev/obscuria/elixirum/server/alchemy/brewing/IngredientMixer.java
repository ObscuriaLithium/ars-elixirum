package dev.obscuria.elixirum.server.alchemy.brewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.brewing.BrewingProcessor;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyIngredient;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import dev.obscuria.fragmentum.util.color.RGB;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;

public class IngredientMixer {

    public static final Codec<IngredientMixer> CODEC;

    @Getter private AlchemyRecipe recipe;
    @Getter private ElixirContents contents;

    public IngredientMixer() {
        this.recipe = AlchemyRecipe.EMPTY;
        this.contents = ElixirContents.WATER;
    }

    private IngredientMixer(AlchemyRecipe recipe, ElixirContents contents) {
        this.recipe = recipe;
        this.contents = contents;
    }

    public boolean append(GlassCauldronEntity entity, ItemStack stack) {
        if (recipe.isComplete()) return false;
        if (entity.getLevel() == null) return false;
        this.recipe = recipe.append(AlchemyIngredient.dynamic(stack));
        this.contents = new BrewingProcessor(Alchemy.get(entity.getLevel()), recipe).brew();
        return true;
    }

    public void clear() {
        this.recipe = AlchemyRecipe.EMPTY;
        this.contents = ElixirContents.WATER;
    }

    public boolean isEmpty() {
        return recipe.isEmpty();
    }

    public boolean isComplete() {
        return recipe.isComplete();
    }

    public RGB color() {
        return contents.color();
    }

    public AlchemyRecipe recipe() {
        return recipe;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AlchemyRecipe.CODEC.fieldOf("recipe").forGetter(IngredientMixer::recipe),
                ElixirContents.CODEC.fieldOf("contents").forGetter(IngredientMixer::getContents)
        ).apply(codec, IngredientMixer::new));
    }
}
