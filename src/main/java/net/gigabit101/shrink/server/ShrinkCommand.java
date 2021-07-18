package net.gigabit101.shrink.server;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class ShrinkCommand
{
    public static LiteralArgumentBuilder<CommandSource> register()
    {
        return Commands.literal("shrink").requires((cs) -> cs.hasPermission(4))
                .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("size", FloatArgumentType.floatArg())
                .executes((ctx) -> execute(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"),
                        FloatArgumentType.getFloat(ctx, "size")))));
    }

    public static int execute(CommandSource source, PlayerEntity playerEntity, float scale)
    {
        playerEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
        {
            iShrinkProvider.setScale(scale);
            iShrinkProvider.shrink(playerEntity);
            source.sendSuccess(new TranslationTextComponent("Set " + playerEntity.getName().getString() + " scale to " + scale), false);
        });
        return 0;
    }
}
