package net.gigabit101.shrink.server;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ShrinkCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> register()
    {
        return Commands.literal("shrink").requires((cs) -> cs.hasPermission(4))
                .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("size", FloatArgumentType.floatArg())
                .executes((ctx) -> execute((CommandSource) ctx.getSource(), EntityArgument.getPlayer(ctx, "player"),
                        FloatArgumentType.getFloat(ctx, "size")))));
    }

    public static int execute(CommandSource source, ServerPlayer playerEntity, float scale)
    {
        playerEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
        {
            iShrinkProvider.setScale(scale);
            iShrinkProvider.shrink(playerEntity);
            source.sendSystemMessage(Component.literal("Set " + playerEntity.getName().getString() + " scale to " + scale));
        });
        return 0;
    }
}
