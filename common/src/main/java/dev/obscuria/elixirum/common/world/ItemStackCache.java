package dev.obscuria.elixirum.common.world;

import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.basics.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemStackCache(
        ElixirContents elixirContents,
        ExtractContents extractContents,
        StyleVariant style,
        Chroma chroma
) {

    public static final ItemStackCache EMPTY;

    public static ItemStackCache of(ItemStack stack) {
        return ((Provider) (Object) stack).elixirum$getCache();
    }

    public static ItemStackCache build(ItemStack stack) {
        return new ItemStackCache(
                NBTComponents.ELIXIR_CONTENTS.readOrDefault(stack.getTag(), ElixirContents.EMPTY),
                NBTComponents.EXTRACT_CONTENTS.readOrDefault(stack.getTag(), ExtractContents.EMPTY),
                NBTComponents.STYLE.readOrDefault(stack.getTag(), StyleVariant.DEFAULT),
                NBTComponents.CHROMA.readOrDefault(stack.getTag(), Chroma.NATURAL));
    }

    public static void markDirty(ItemStack stack) {
        ((Provider) (Object) stack).elixirum$markDirty();
    }

    public static List<Essence> suppressedEssences(ItemStack stack) {
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

        List<Essence> elixirum$suppressedEssences();

        void elixirum$markDirty();

        void elixirum$setBrewFlag(boolean flag);

        boolean elixirum$isBrewFlag();
    }

    static {
        EMPTY = new ItemStackCache(
                ElixirContents.EMPTY,
                ExtractContents.EMPTY,
                StyleVariant.DEFAULT,
                Chroma.NATURAL);
    }
}
