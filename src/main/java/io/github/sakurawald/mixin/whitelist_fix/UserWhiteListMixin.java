package io.github.sakurawald.mixin.whitelist_fix;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.UserWhiteList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UserWhiteList.class)

public class UserWhiteListMixin {

    /**
     * Once an offline-player join the server, then the offline-uuid will be added to usercache.json.
     * After that, the server will always use the player's offline-uuid in usercache.json to check whitelist (and other list, like ban list, op list).
     * <p>
     * If you use white-list=true with online-mode=false, then the cases is:
     * 1. for online-player, everything is ok.
     * 2. for offline-player, the whitelist will always check the online-uuid, so you need to type /whitelist off to disable whitelist,
     * and let the offline-player join the game, so that the usercache.json can be updated to the offline-uuid.
     * <p>
     * This @Inject makes the whitelist.json only look-up for the player's name, not the uuid.
     *
     * @see UserWhiteList#isWhiteListed(GameProfile)
     * @see UserWhiteList#getKeyForUser(GameProfile)
     * @see net.minecraft.server.players.PlayerList#placeNewPlayer(Connection, ServerPlayer, CommonListenerCookie)
     * @see net.minecraft.server.players.GameProfileCache#add(GameProfile)
     **/
    @Inject(method = "getKeyForUser*", at = @At("HEAD"), cancellable = true)
    void $getKeyForUser(GameProfile gameProfile, CallbackInfoReturnable<String> ci) {
        String ret = gameProfile.getName();
        ci.setReturnValue(ret);
    }
}
