package io.github.sakurawald.module.initializer.skin.command;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.skin.SkinRestorer;
import io.github.sakurawald.module.initializer.skin.enums.SkinVariant;
import io.github.sakurawald.module.initializer.skin.provider.MineSkinSkinProvider;
import io.github.sakurawald.module.initializer.skin.provider.MojangSkinProvider;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class SkinModule extends ModuleInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);

    }

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(literal("skin")
                .then(literal("set")
                        .then(literal("mojang")
                                .then(argument("skin_name", StringArgumentType.word())
                                        .executes(context ->
                                                skinAction(context.getSource(),
                                                        () -> MojangSkinProvider.getSkin(StringArgumentType.getString(context, "skin_name"))))
                                        .then(argument("targets", GameProfileArgument.gameProfile()).requires(source -> source.hasPermission(4))
                                                .executes(context ->
                                                        skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                                                () -> MojangSkinProvider.getSkin(StringArgumentType.getString(context, "skin_name")))))))
                        .then(literal("web")
                                .then(literal("classic")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        skinAction(context.getSource(),
                                                                () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC)))
                                                .then(argument("targets", GameProfileArgument.gameProfile()).requires(source -> source.hasPermission(4))
                                                        .executes(context ->
                                                                skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                                                        () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC))))))
                                .then(literal("slim")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        skinAction(context.getSource(),
                                                                () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM)))
                                                .then(argument("targets", GameProfileArgument.gameProfile()).requires(source -> source.hasPermission(4))
                                                        .executes(context ->
                                                                skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                                                        () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM))))))))
                .then(literal("clear")
                        .executes(context ->
                                skinAction(context.getSource(),
                                        () -> SkinRestorer.getSkinStorage().getDefaultSkin()))
                        .then(argument("targets", GameProfileArgument.gameProfile()).executes(context ->
                                skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                        () -> SkinRestorer.getSkinStorage().getDefaultSkin()))))
        );
    }

    private int skinAction(CommandSourceStack src, Collection<GameProfile> targets, boolean setByOperator, Supplier<Property> skinSupplier) {
        SkinRestorer.setSkinAsync(src.getServer(), targets, skinSupplier).thenAccept(pair -> {
            Collection<GameProfile> profiles = pair.right();
            Collection<ServerPlayer> players = pair.left();

            if (profiles.isEmpty()) {
                MessageUtil.sendMessage(src, "skin.action.failed");
                return;
            }
            if (setByOperator) {
                MessageUtil.sendMessage(src, "skin.action.affected_profile", String.join(", ", profiles.stream().map(GameProfile::getName).toList()));
                if (!players.isEmpty()) {
                    MessageUtil.sendMessage(src, "skin.action.affected_player", String.join(", ", players.stream().map(p -> p.getGameProfile().getName()).toList()));
                }
            } else {
                MessageUtil.sendMessage(src, "skin.action.ok");
            }
        });
        return targets.size();
    }

    private int skinAction(CommandSourceStack src, Supplier<Property> skinSupplier) {
        if (src.getPlayer() == null)
            return 0;

        return skinAction(src, Collections.singleton(src.getPlayer().getGameProfile()), false, skinSupplier);
    }

}
