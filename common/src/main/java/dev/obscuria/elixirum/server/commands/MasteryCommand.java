package dev.obscuria.elixirum.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.helpers.MasteryHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

public final class MasteryCommand {

    private static final String TEXT_QUERY = "commands.elixirum.mastery.query.";
    private static final String TEXT_ADD = "commands.elixirum.mastery.add.";
    private static final String TEXT_SET = "commands.elixirum.mastery.set.";

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        dispatcher.register(Commands.literal(ArsElixirum.MODID)
                .then(Commands.literal("mastery").requires(source -> source.hasPermission(3))
                        .then(Commands.literal("query")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("levels").executes(command -> {
                                            var player = EntityArgument.getPlayer(command, "player");
                                            return queryMastery(command.getSource(), player, Type.LEVELS);
                                        }))
                                        .then(Commands.literal("points").executes(command -> {
                                            var player = EntityArgument.getPlayer(command, "player");
                                            return queryMastery(command.getSource(), player, Type.POINTS);
                                        }))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .then(Commands.literal("levels").executes(command -> {
                                                    var player = EntityArgument.getPlayer(command, "player");
                                                    var amount = IntegerArgumentType.getInteger(command, "amount");
                                                    return addMastery(command.getSource(), player, amount, Type.LEVELS);
                                                }))
                                                .then(Commands.literal("points").executes(command -> {
                                                    var player = EntityArgument.getPlayer(command, "player");
                                                    var amount = IntegerArgumentType.getInteger(command, "amount");
                                                    return addMastery(command.getSource(), player, amount, Type.POINTS);
                                                })))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .then(Commands.literal("levels").executes(command -> {
                                                    var player = EntityArgument.getPlayer(command, "player");
                                                    var amount = IntegerArgumentType.getInteger(command, "amount");
                                                    return setMastery(command.getSource(), player, amount, Type.LEVELS);
                                                }))
                                                .then(Commands.literal("points").executes(command -> {
                                                    var player = EntityArgument.getPlayer(command, "player");
                                                    var amount = IntegerArgumentType.getInteger(command, "amount");
                                                    return setMastery(command.getSource(), player, amount, Type.POINTS);
                                                })))))));
    }

    private static int queryMastery(CommandSourceStack source, ServerPlayer player, Type type) {
        var value = type.query.applyAsInt(player);
        source.sendSuccess(() -> Component.translatable(TEXT_QUERY + type.name, player.getDisplayName(), value), false);
        return value;
    }

    private static int addMastery(CommandSourceStack source, ServerPlayer player, int amount, Type type) {
        type.add.accept(player, amount);
        source.sendSuccess(() -> Component.translatable(TEXT_ADD + type.name, amount, player.getDisplayName()), true);
        return 1;
    }

    private static int setMastery(CommandSourceStack source, ServerPlayer player, int amount, Type type) {
        type.set.accept(player, amount);
        source.sendSuccess(() -> Component.translatable(TEXT_SET + type.name, amount, player.getDisplayName()), true);
        return 1;
    }

    public enum Type {
        LEVELS("levels",
                MasteryHelper::levelOf,
                MasteryHelper::addLevels,
                MasteryHelper::setLevel),
        POINTS("points",
                MasteryHelper::xpOf,
                MasteryHelper::addXp,
                MasteryHelper::setXp);

        private final ToIntFunction<ServerPlayer> query;
        private final BiConsumer<ServerPlayer, Integer> add;
        private final BiConsumer<ServerPlayer, Integer> set;
        private final String name;

        Type(
                String name,
                ToIntFunction<ServerPlayer> query,
                BiConsumer<ServerPlayer, Integer> add,
                BiConsumer<ServerPlayer, Integer> set
        ) {
            this.name = name;
            this.query = query;
            this.add = add;
            this.set = set;
        }
    }
}
