package io.github.sakurawald.module.initializer.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.skin.enums.SkinVariant;
import io.github.sakurawald.module.initializer.skin.provider.MineSkinSkinProvider;
import io.github.sakurawald.module.initializer.skin.provider.MojangSkinProvider;
import io.github.sakurawald.util.MessageUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


// todo: code review
public class SkinInitializer extends ModuleInitializer {

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext, CommandManager.RegistrationEnvironment commandSelection) {
        dispatcher.register(literal("skin")
                .then(literal("set")
                        .then(literal("mojang")
                                .then(argument("skin_name", StringArgumentType.word())
                                        .executes(context ->
                                                skinAction(context.getSource(),
                                                        () -> MojangSkinProvider.getSkin(StringArgumentType.getString(context, "skin_name"))))
                                        .then(argument("targets", GameProfileArgumentType.gameProfile()).requires(source -> source.hasPermissionLevel(4))
                                                .executes(context ->
                                                        skinAction(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), true,
                                                                () -> MojangSkinProvider.getSkin(StringArgumentType.getString(context, "skin_name")))))))
                        .then(literal("web")
                                .then(literal("classic")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        skinAction(context.getSource(),
                                                                () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC)))
                                                .then(argument("targets", GameProfileArgumentType.gameProfile()).requires(source -> source.hasPermissionLevel(4))
                                                        .executes(context ->
                                                                skinAction(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), true,
                                                                        () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC))))))
                                .then(literal("slim")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        skinAction(context.getSource(),
                                                                () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM)))
                                                .then(argument("targets", GameProfileArgumentType.gameProfile()).requires(source -> source.hasPermissionLevel(4))
                                                        .executes(context ->
                                                                skinAction(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), true,
                                                                        () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM))))))))
                .then(literal("clear")
                        .executes(context ->
                                skinAction(context.getSource(),
                                        () -> SkinRestorer.getSkinStorage().getDefaultSkin()))
                        .then(argument("targets", GameProfileArgumentType.gameProfile()).executes(context ->
                                skinAction(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), true,
                                        () -> SkinRestorer.getSkinStorage().getDefaultSkin()))))
        );
    }

    private int skinAction(ServerCommandSource src, Collection<GameProfile> targets, boolean setByOperator, Supplier<Property> skinSupplier) {
        SkinRestorer.setSkinAsync(src.getServer(), targets, skinSupplier).thenAccept(pair -> {
            Collection<GameProfile> profiles = pair.right();
            Collection<ServerPlayerEntity> players = pair.left();

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

    private int skinAction(ServerCommandSource src, Supplier<Property> skinSupplier) {
        if (src.getPlayer() == null)
            return 0;

        return skinAction(src, Collections.singleton(src.getPlayer().getGameProfile()), false, skinSupplier);
    }

}
