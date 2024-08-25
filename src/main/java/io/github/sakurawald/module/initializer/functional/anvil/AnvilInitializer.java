package io.github.sakurawald.module.initializer.functional.anvil;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AnvilInitializer extends ModuleInitializer {

    @Command("anvil")
    private int $anvil(@CommandSource ServerPlayerEntity player) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new AnvilScreenHandler(i, inventory, ScreenHandlerContext.create(p.getWorld(), p.getBlockPos())) {
            @Override
            public boolean canUse(PlayerEntity player) {
                return true;
            }
        }, Text.translatable("container.repair")));
        return CommandHelper.Return.SUCCESS;
    }
}
