package dev.obscuria.elixirum.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class RegenerateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(Elixirum.MODID).requires(source -> source.hasPermission(3))
                .then(Commands.literal("regenerate")
                        .executes(command -> regenerate(command.getSource()))));
    }

    private static int regenerate(CommandSourceStack source) {
        ServerAlchemy.getPropertyMap().regenerate();
        ServerAlchemy.syncPropertyMap();
        source.sendSuccess(() -> Component.literal("Regenerating..."), true);
        return 1;
    }
}
