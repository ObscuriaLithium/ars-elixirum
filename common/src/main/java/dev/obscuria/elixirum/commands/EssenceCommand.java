package dev.obscuria.elixirum.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public final class EssenceCommand {
    private static final Dynamic2CommandExceptionType ERROR_NO_ESSENCE = new Dynamic2CommandExceptionType(
            (item, essence) -> Component.translatableEscape("commands.elixirum.essence.failed.no_essence", item, essence));
    private static final Dynamic3CommandExceptionType ERROR_ALREADY_EXISTS = new Dynamic3CommandExceptionType(
            (item, essence, weight) -> Component.translatableEscape("commands.elixirum.essence.failed.already_exists", item, essence, weight));
    private static final DynamicCommandExceptionType ERROR_ALREADY_EMPTY = new DynamicCommandExceptionType(
            (item) -> Component.translatableEscape("commands.elixirum.essence.failed.already_empty", item));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(Elixirum.MODID).requires(source -> source.hasPermission(3))
                .then(Commands.literal("essence")
                        .then(Commands.argument("target", ItemArgument.item(context))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("essence", ResourceArgument.resource(context, ElixirumRegistries.ESSENCE))
                                                .then(Commands.argument("weight", IntegerArgumentType.integer(1))
                                                        .executes(command -> set(command.getSource(),
                                                                ItemArgument.getItem(command, "target").getItem(),
                                                                ResourceArgument.getResource(command, "essence", ElixirumRegistries.ESSENCE),
                                                                IntegerArgumentType.getInteger(command, "weight"))))))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("essence", ResourceArgument.resource(context, ElixirumRegistries.ESSENCE))
                                                .executes(command -> remove(command.getSource(),
                                                        ItemArgument.getItem(command, "target").getItem(),
                                                        ResourceArgument.getResource(command, "essence", ElixirumRegistries.ESSENCE)))))
                                .then(Commands.literal("clear")
                                        .executes(command -> clear(command.getSource(),
                                                ItemArgument.getItem(command, "target").getItem()))))));
    }

    private static int set(CommandSourceStack source, Item item, Holder.Reference<Essence> essence, int weight) throws CommandSyntaxException {
        var map = ServerAlchemy.getItemEssences();
        var properties = map.getHolder(item);
        if (properties.getWeight(essence.key().location()) == weight)
            throw ERROR_ALREADY_EXISTS.create(
                    Component.translatableEscape(item.getDescriptionId()).getString(),
                    essence.value().getName().getString(),
                    weight);
        map.setHolder(item, properties.with(essence.key().location(), weight));
        ServerAlchemy.syncItemEssences();
        source.sendSuccess(() -> Component.translatableEscape("commands.elixirum.essence.success.set",
                essence.value().getName().getString(),
                weight,
                Component.translatableEscape(item.getDescriptionId()).getString()), true);
        return 1;
    }

    private static int remove(CommandSourceStack source, Item item, Holder.Reference<Essence> essence) throws CommandSyntaxException {
        var map = ServerAlchemy.getItemEssences();
        var properties = map.getHolder(item);
        if (!properties.contains(essence.key().location()))
            throw ERROR_NO_ESSENCE.create(
                    Component.translatableEscape(item.getDescriptionId()).getString(),
                    essence.value().getName().getString());
        map.setHolder(item, properties.exclude(essence));
        ServerAlchemy.syncItemEssences();
        source.sendSuccess(() -> Component.translatableEscape("commands.elixirum.essence.success.remove",
                essence.value().getName().getString(),
                Component.translatableEscape(item.getDescriptionId()).getString()), true);
        return 1;
    }

    private static int clear(CommandSourceStack source, Item item) throws CommandSyntaxException {
        var map = ServerAlchemy.getItemEssences();
        map.removeHolder(item);
        if (!map.getHolder(item).isEmpty())
            throw ERROR_ALREADY_EMPTY.create(
                    Component.translatableEscape(item.getDescriptionId()).getString());
        ServerAlchemy.syncItemEssences();
        source.sendSuccess(() -> Component.translatableEscape("commands.elixirum.essence.success.clear",
                Component.translatableEscape(item.getDescriptionId()).getString()), true);
        return 1;
    }
}
