package dev.obscuria.elixirum.server.commands;

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
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

public final class EssenceCommand {
    private static final Dynamic2CommandExceptionType ERROR_NO_ESSENCE = new Dynamic2CommandExceptionType(
            (item, essence) -> Component.translatableEscape("commands.elixirum.essence.failed.no_essence", item, essence));
    private static final DynamicCommandExceptionType ERROR_NO_ESSENCES = new DynamicCommandExceptionType(
            (item) -> Component.translatableEscape("commands.elixirum.essence.failed.no_essences", item));
    private static final Dynamic3CommandExceptionType ERROR_ALREADY_EXISTS = new Dynamic3CommandExceptionType(
            (item, essence, weight) -> Component.translatableEscape("commands.elixirum.essence.failed.already_exists", item, essence, weight));
    private static final DynamicCommandExceptionType ERROR_ALREADY_EMPTY = new DynamicCommandExceptionType(
            (item) -> Component.translatableEscape("commands.elixirum.essence.failed.already_empty", item));
    private static final DynamicCommandExceptionType NOTHING_TO_CHANGE = new DynamicCommandExceptionType(
            (player) -> Component.translatableEscape("commands.elixirum.essence.failed.nothing_to_change", player));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(Elixirum.MODID).requires(source -> source.hasPermission(3))
                .then(Commands.literal("essence")
                        .then(Commands.literal("set")
                                .then(Commands.argument("target", ItemArgument.item(context))
                                        .then(Commands.argument("essence", ResourceArgument.resource(context, ElixirumRegistries.ESSENCE))
                                                .then(Commands.argument("weight", IntegerArgumentType.integer(1))
                                                        .executes(command -> set(command.getSource(),
                                                                ItemArgument.getItem(command, "target").getItem(),
                                                                ResourceArgument.getResource(command, "essence", ElixirumRegistries.ESSENCE),
                                                                IntegerArgumentType.getInteger(command, "weight")))))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("target", ItemArgument.item(context))
                                        .then(Commands.argument("essence", ResourceArgument.resource(context, ElixirumRegistries.ESSENCE))
                                                .executes(command -> remove(command.getSource(),
                                                        ItemArgument.getItem(command, "target").getItem(),
                                                        ResourceArgument.getResource(command, "essence", ElixirumRegistries.ESSENCE))))))
                        .then(Commands.literal("clear")
                                .then(Commands.argument("target", ItemArgument.item(context))
                                        .executes(command -> clear(command.getSource(),
                                                ItemArgument.getItem(command, "target").getItem()))))
                        .then(Commands.literal("discover")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("all")
                                                .executes(command -> discoverAll(command.getSource(),
                                                        EntityArgument.getPlayer(command, "player"))))
                                        .then(Commands.argument("target", ItemArgument.item(context))
                                                .executes(command -> discover(command.getSource(),
                                                        EntityArgument.getPlayer(command, "player"),
                                                        ItemArgument.getItem(command, "target").getItem())))))
                        .then(Commands.literal("forget")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("all")
                                                .executes(command -> forgetAll(command.getSource(),
                                                        EntityArgument.getPlayer(command, "player"))))
                                        .then(Commands.argument("target", ItemArgument.item(context))
                                                .executes(command -> forget(command.getSource(),
                                                        EntityArgument.getPlayer(command, "player"),
                                                        ItemArgument.getItem(command, "target").getItem())))))));
    }

    private static int set(CommandSourceStack source, Item item, Holder.Reference<Essence> essence, int weight) throws CommandSyntaxException {
        var map = ServerAlchemy.getIngredients();
        var properties = map.getProperties(item);
        if (properties.getWeight(essence.key().location()) == weight)
            throw ERROR_ALREADY_EXISTS.create(
                    Component.translatableEscape(item.getDescriptionId()).getString(),
                    essence.value().getDisplayName().getString(),
                    weight);
        map.setProperties(item, properties.with(essence.key().location(), weight));
        ServerAlchemy.syncIngredients();
        ServerAlchemy.validateProfiles();
        source.sendSuccess(() -> Component.translatableEscape("commands.elixirum.essence.success.set",
                essence.value().getDisplayName().getString(),
                weight,
                Component.translatableEscape(item.getDescriptionId()).getString()), true);
        return 1;
    }

    private static int remove(CommandSourceStack source, Item item, Holder.Reference<Essence> essence) throws CommandSyntaxException {
        var map = ServerAlchemy.getIngredients();
        var properties = map.getProperties(item);
        if (!properties.contains(essence.key().location()))
            throw ERROR_NO_ESSENCE.create(
                    Component.translatableEscape(item.getDescriptionId()).getString(),
                    essence.value().getDisplayName().getString());
        map.setProperties(item, properties.exclude(essence.key().location()));
        ServerAlchemy.syncIngredients();
        ServerAlchemy.validateProfiles();
        source.sendSuccess(() -> Component.translatableEscape("commands.elixirum.essence.success.remove",
                essence.value().getDisplayName().getString(),
                Component.translatableEscape(item.getDescriptionId()).getString()), true);
        return 1;
    }

    private static int clear(CommandSourceStack source, Item item) throws CommandSyntaxException {
        var map = ServerAlchemy.getIngredients();
        map.removeProperties(item);
        if (!map.getProperties(item).isEmpty())
            throw ERROR_ALREADY_EMPTY.create(Component.translatableEscape(item.getDescriptionId()).getString());
        ServerAlchemy.syncIngredients();
        ServerAlchemy.validateProfiles();
        source.sendSuccess(() -> Component.translatableEscape(
                "commands.elixirum.essence.success.clear",
                Component.translatableEscape(item.getDescriptionId()).getString()), true);
        return 1;
    }

    private static int discover(CommandSourceStack source, ServerPlayer player, Item item) throws CommandSyntaxException {
        final var getter = source.registryAccess().lookupOrThrow(ElixirumRegistries.ESSENCE);
        final var profile = ServerAlchemy.getProfile(player);
        final var properties = ServerAlchemy.getIngredients().getProperties(item);
        if (properties.isEmpty())
            throw ERROR_NO_ESSENCES.create(Component.translatableEscape(item.getDescriptionId()).getString());
        var total = 0;
        for (var essence : properties.getEssences(getter).keySet()) {
            if (profile.isEssenceDiscovered(item, essence)) continue;
            profile.discoverEssence(item, essence, false);
            total += 1;
        }
        if (total > 0) {
            profile.syncWithPlayer();
            final var totalDiscovered = total;
            source.sendSuccess(() -> Component.translatableEscape(
                    "commands.elixirum.essence.success.discover",
                    player.getName(),
                    totalDiscovered), true);
            return total;
        } else {
            throw NOTHING_TO_CHANGE.create(player.getName());
        }
    }

    private static int discoverAll(CommandSourceStack source, ServerPlayer player) throws CommandSyntaxException {
        final var getter = source.registryAccess().lookupOrThrow(ElixirumRegistries.ESSENCE);
        final var profile = ServerAlchemy.getProfile(player);
        var total = 0;
        for (var entry : ServerAlchemy.getIngredients()) {
            for (var essence : entry.properties().getEssences(getter).keySet()) {
                if (profile.isEssenceDiscovered(entry.item(), essence)) continue;
                profile.discoverEssence(entry.item(), essence, false);
                total += 1;
            }
        }
        if (total > 0) {
            profile.syncWithPlayer();
            final var totalDiscovered = total;
            source.sendSuccess(() -> Component.translatableEscape(
                    "commands.elixirum.essence.success.discover",
                    player.getName(),
                    totalDiscovered), true);
            return total;
        } else {
            throw NOTHING_TO_CHANGE.create(player.getName());
        }
    }

    private static int forget(CommandSourceStack source, ServerPlayer player, Item item) throws CommandSyntaxException {
        final var getter = source.registryAccess().lookupOrThrow(ElixirumRegistries.ESSENCE);
        final var profile = ServerAlchemy.getProfile(player);
        final var discovered = profile.getDiscoveredEssences(item);
        final var properties = ServerAlchemy.getIngredients().getProperties(item);
        if (properties.isEmpty())
            throw ERROR_NO_ESSENCES.create(Component.translatableEscape(item.getDescriptionId()).getString());
        var total = 0;
        for (var essence : properties.getEssences(getter).keySet()) {
            if (!discovered.contains(essence)) continue;
            profile.forgetEssence(item, essence);
            total += 1;
        }
        if (total > 0) {
            profile.syncWithPlayer();
            final var totalForgot = total;
            source.sendSuccess(() -> Component.translatableEscape(
                    "commands.elixirum.essence.success.forget",
                    player.getName(),
                    totalForgot), true);
            return total;
        } else {
            throw NOTHING_TO_CHANGE.create(player.getName());
        }
    }

    private static int forgetAll(CommandSourceStack source, ServerPlayer player) throws CommandSyntaxException {
        final var getter = source.registryAccess().lookupOrThrow(ElixirumRegistries.ESSENCE);
        final var profile = ServerAlchemy.getProfile(player);
        var total = 0;
        for (var entry : ServerAlchemy.getIngredients()) {
            for (var essence : entry.properties().getEssences(getter).keySet()) {
                if (!profile.isEssenceDiscovered(entry.item(), essence)) continue;
                profile.forgetEssence(entry.item(), essence);
                total += 1;
            }
        }
        if (total > 0) {
            profile.syncWithPlayer();
            final var totalForgot = total;
            source.sendSuccess(() -> Component.translatableEscape(
                    "commands.elixirum.essence.success.forget",
                    player.getName(),
                    totalForgot), true);
            return total;
        } else {
            throw NOTHING_TO_CHANGE.create(player.getName());
        }
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept("commands.elixirum.essence.success.set", "Applied essence %s (x%s) to %s");
        consumer.accept("commands.elixirum.essence.success.remove", "Removed essence %s from %s");
        consumer.accept("commands.elixirum.essence.success.clear", "Removed all properties from %s");
        consumer.accept("commands.elixirum.essence.success.discover", "%s discovered %s essences");
        consumer.accept("commands.elixirum.essence.success.forget", "%s forgot %s essences");
        consumer.accept("commands.elixirum.essence.failed.no_essence", "%s has no essence %s");
        consumer.accept("commands.elixirum.essence.failed.no_essences", "%s has no essences");
        consumer.accept("commands.elixirum.essence.failed.already_exists", "%s already has essence %s (x%s)");
        consumer.accept("commands.elixirum.essence.failed.already_empty", "%s has no properties");
        consumer.accept("commands.elixirum.essence.failed.nothing_to_change", "Nothing to change for %s");
    }
}
