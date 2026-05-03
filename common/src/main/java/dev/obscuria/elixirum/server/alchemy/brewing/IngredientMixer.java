package dev.obscuria.elixirum.server.alchemy.brewing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.AlchemyIngredient;
import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy.brewing.BrewingProcessor;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import dev.obscuria.fragmentum.util.color.RGB;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;

public class IngredientMixer {

    public static final Codec<IngredientMixer> CODEC;

    @Getter private AlchemyRecipe recipe;
    @Getter private ElixirContents contents;

    public IngredientMixer() {
        this.recipe = AlchemyRecipe.empty();
        this.contents = ElixirContents.water();
    }

    private IngredientMixer(AlchemyRecipe recipe, ElixirContents contents) {
        this.recipe = recipe;
        this.contents = contents;
    }

    public boolean append(GlassCauldronEntity entity, ItemStack stack) {
        if (recipe.isComplete()) return false;
        if (entity.getLevel() == null) return false;
        this.recipe = recipe.append(AlchemyIngredient.of(stack));
        this.contents = new BrewingProcessor(Alchemy.get(entity.getLevel()), recipe).brew();
        return true;
    }

    public void clear() {
        this.recipe = AlchemyRecipe.empty();
        this.contents = ElixirContents.water();
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
                AlchemyRecipe.codec().fieldOf("recipe").forGetter(IngredientMixer::recipe),
                ElixirContents.codec().fieldOf("contents").forGetter(IngredientMixer::getContents)
        ).apply(codec, IngredientMixer::new));
    }
}
