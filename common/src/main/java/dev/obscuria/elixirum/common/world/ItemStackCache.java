package dev.obscuria.elixirum.common.world;

import dev.obscuria.elixirum.api.alchemy.components.CustomText;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.api.alchemy.components.ExtractContents;
import dev.obscuria.elixirum.api.alchemy.components.StyleVariant;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record ItemStackCache(
        ElixirContents elixirContents,
        ExtractContents extractContents,
        StyleVariant style,
        Chroma chroma,
        Optional<CustomText> customName,
        Optional<CustomText> customLore
) {

    public static final ItemStackCache EMPTY;

    public static ItemStackCache of(ItemStack stack) {
        return ((Provider) (Object) stack).elixirum$getCache();
    }

    public static ItemStackCache build(ItemStack stack) {
        return new ItemStackCache(
                NBTComponents.ELIXIR_CONTENTS.readOrDefault(stack.getTag(), ElixirContents.empty()),
                NBTComponents.EXTRACT_CONTENTS.readOrDefault(stack.getTag(), ExtractContents.empty()),
                NBTComponents.STYLE.readOrDefault(stack.getTag(), StyleVariant.defaultVariant()),
                NBTComponents.CHROMA.readOrDefault(stack.getTag(), Chroma.NATURAL),
                NBTComponents.CUSTOM_NAME.read(stack.getTag()),
                NBTComponents.CUSTOM_LORE.read(stack.getTag()));
    }

    public static void markDirty(ItemStack stack) {
        ((Provider) (Object) stack).elixirum$markDirty();
    }

    public static List<EssenceHolder> suppressedEssences(ItemStack stack) {
        return ((Provider) (Object) stack).elixirum$suppressedEssences();
    }

    public static void setBrewFlag(ItemStack stack, boolean flag) {
        ((Provider) (Object) stack).elixirum$setBrewFlag(flag);
    }

    public static boolean isBrewFlag(ItemStack stack) {
        return ((Provider) (Object) stack).elixirum$isBrewFlag();
    }

    public interface Provider {

        ItemStackCache elixirum$getCache();

        List<EssenceHolder> elixirum$suppressedEssences();

        void elixirum$markDirty();

        void elixirum$setBrewFlag(boolean flag);

        boolean elixirum$isBrewFlag();
    }

    static {
        EMPTY = new ItemStackCache(
                ElixirContents.empty(),
                ExtractContents.empty(),
                StyleVariant.defaultVariant(),
                Chroma.NATURAL,
                Optional.empty(),
                Optional.empty());
    }
}
