package fun.sakurawald.module.skin.command;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fun.sakurawald.module.skin.SkinRestorer;
import fun.sakurawald.module.skin.enums.SkinVariant;
import fun.sakurawald.module.skin.lang.LanguageUtils;
import fun.sakurawald.module.skin.provider.MineSkinSkinProvider;
import fun.sakurawald.module.skin.provider.MojangSkinProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import static fun.sakurawald.module.skin.io.SkinStorage.DEFAULT_SKIN;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SkinModule {
    @SuppressWarnings("unused")
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(literal("skin")
                .then(literal("set")
                        .then(literal("mojang")
                                .then(argument("skin_name", StringArgumentType.word())
                                        .executes(context ->
                                                skinAction(context.getSource(),
                                                        () -> MojangSkinProvider.getSkin(StringArgumentType.getString(context, "skin_name"))))
                                        .then(argument("targets", GameProfileArgument.gameProfile()).requires(source -> source.hasPermission(3))
                                                .executes(context ->
                                                        skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                                                () -> MojangSkinProvider.getSkin(StringArgumentType.getString(context, "skin_name")))))))
                        .then(literal("web")
                                .then(literal("classic")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        skinAction(context.getSource(),
                                                                () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC)))
                                                .then(argument("targets", GameProfileArgument.gameProfile()).requires(source -> source.hasPermission(3))
                                                        .executes(context ->
                                                                skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                                                        () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.CLASSIC))))))
                                .then(literal("slim")
                                        .then(argument("url", StringArgumentType.string())
                                                .executes(context ->
                                                        skinAction(context.getSource(),
                                                                () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM)))
                                                .then(argument("targets", GameProfileArgument.gameProfile()).requires(source -> source.hasPermission(3))
                                                        .executes(context ->
                                                                skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                                                        () -> MineSkinSkinProvider.getSkin(StringArgumentType.getString(context, "url"), SkinVariant.SLIM))))))))
                .then(literal("clear")
                        .executes(context ->
                                skinAction(context.getSource(),
                                        () -> DEFAULT_SKIN))
                        .then(argument("targets", GameProfileArgument.gameProfile()).executes(context ->
                                skinAction(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), true,
                                        () -> DEFAULT_SKIN))))
        );
    }


    private static int skinAction(CommandSourceStack src, Collection<GameProfile> targets, boolean setByOperator, Supplier<Property> skinSupplier) {
        SkinRestorer.setSkinAsync(src.getServer(), targets, skinSupplier).thenAccept(pair -> {
            Collection<GameProfile> profiles = pair.right();
            Collection<ServerPlayer> players = pair.left();
            if (profiles.size() == 0) {
                src.sendFailure(Component.nullToEmpty(LanguageUtils.translation.skinActionFailed));
                return;
            }
            if (setByOperator) {
                src.sendSuccess(() -> Component.nullToEmpty(
                        String.format(LanguageUtils.translation.skinActionAffectedProfile,
                                String.join(", ", profiles.stream().map(GameProfile::getName).toList()))), true);
                if (players.size() != 0) {
                    src.sendSuccess(() -> Component.nullToEmpty(
                            String.format(LanguageUtils.translation.skinActionAffectedPlayer,
                                    String.join(", ", players.stream().map(p -> p.getGameProfile().getName()).toList()))), true);
                }
            } else {
                src.sendSuccess(() -> Component.nullToEmpty(LanguageUtils.translation.skinActionOk), true);
            }
        });
        return targets.size();
    }

    private static int skinAction(CommandSourceStack src, Supplier<Property> skinSupplier) {
        if (src.getPlayer() == null)
            return 0;

        return skinAction(src, Collections.singleton(src.getPlayer().getGameProfile()), false, skinSupplier);
    }

}
