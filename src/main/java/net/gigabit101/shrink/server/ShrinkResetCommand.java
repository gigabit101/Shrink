package net.gigabit101.shrink.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.gigabit101.shrink.api.ShrinkAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class ShrinkResetCommand
{
    public static LiteralArgumentBuilder<CommandSource> register()
    {
        return Commands.literal("unshrink").requires((cs) -> cs.hasPermission(4))
                .then(Commands.argument("player", EntityArgument.player())
                .executes((ctx) -> execute(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))));
    }

    public static int execute(CommandSource source, PlayerEntity playerEntity)
    {
        playerEntity.getCapability(ShrinkAPI.SHRINK_CAPABILITY).ifPresent(iShrinkProvider ->
        {
            iShrinkProvider.deShrink(playerEntity);
            source.sendSuccess(new TranslationTextComponent("reset " + playerEntity.getName().getString() + " to default"), false);
        });
        return 0;
    }
}
