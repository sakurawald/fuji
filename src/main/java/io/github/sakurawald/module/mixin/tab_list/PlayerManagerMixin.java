package io.github.sakurawald.module.mixin.tab_list;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.tab_list.TabListInitializer;
import io.github.sakurawald.util.PermissionUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;

@Mixin(PlayerManager.class)
@Slf4j
public class PlayerManagerMixin {

    @Shadow
    @Final
    private MinecraftServer server;
    @Unique
    private final String[] ALPHA_TABLE = {"aa", "ab", "ac", "ad", "ae", "af", "ag", "ah", "ai", "aj", "ak", "al", "am", "an", "ao", "ap", "aq", "ar", "as", "at", "au", "av", "aw", "ax", "ay", "az", "ba", "bb", "bc", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bk", "bl", "bm", "bn", "bo", "bp", "bq", "br", "bs", "bt", "bu", "bv", "bw", "bx", "by", "bz", "ca", "cb", "cc", "cd", "ce", "cf", "cg", "ch", "ci", "cj", "ck", "cl", "cm", "cn", "co", "cp", "cq", "cr", "cs", "ct", "cu", "cv", "cw", "cx", "cy", "cz", "da", "db", "dc", "dd", "de", "df", "dg", "dh", "di", "dj", "dk", "dl", "dm", "dn", "do", "dp", "dq", "dr", "ds", "dt", "du", "dv", "dw", "dx", "dy", "dz", "ea", "eb", "ec", "ed", "ee", "ef", "eg", "eh", "ei", "ej", "ek", "el", "em", "en", "eo", "ep", "eq", "er", "es", "et", "eu", "ev", "ew", "ex", "ey", "ez", "fa", "fb", "fc", "fd", "fe", "ff", "fg", "fh", "fi", "fj", "fk", "fl", "fm", "fn", "fo", "fp", "fq", "fr", "fs", "ft", "fu", "fv", "fw", "fx", "fy", "fz", "ga", "gb", "gc", "gd", "ge", "gf", "gg", "gh", "gi", "gj", "gk", "gl", "gm", "gn", "go", "gp", "gq", "gr", "gs", "gt", "gu", "gv", "gw", "gx", "gy", "gz", "ha", "hb", "hc", "hd", "he", "hf", "hg", "hh", "hi", "hj", "hk", "hl", "hm", "hn", "ho", "hp", "hq", "hr", "hs", "ht", "hu", "hv", "hw", "hx", "hy", "hz", "ia", "ib", "ic", "id", "ie", "if", "ig", "ih", "ii", "ij", "ik", "il", "im", "in", "io", "ip", "iq", "ir", "is", "it", "iu", "iv", "iw", "ix", "iy", "iz", "ja", "jb", "jc", "jd", "je", "jf", "jg", "jh", "ji", "jj", "jk", "jl", "jm", "jn", "jo", "jp", "jq", "jr", "js", "jt", "ju", "jv", "jw", "jx", "jy", "jz", "ka", "kb", "kc", "kd", "ke", "kf", "kg", "kh", "ki", "kj", "kk", "kl", "km", "kn", "ko", "kp", "kq", "kr", "ks", "kt", "ku", "kv", "kw", "kx", "ky", "kz", "la", "lb", "lc", "ld", "le", "lf", "lg", "lh", "li", "lj", "lk", "ll", "lm", "ln", "lo", "lp", "lq", "lr", "ls", "lt", "lu", "lv", "lw", "lx", "ly", "lz", "ma", "mb", "mc", "md", "me", "mf", "mg", "mh", "mi", "mj", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nb", "nc", "nd", "ne", "nf", "ng", "nh", "ni", "nj", "nk", "nl", "nm", "nn", "no", "np", "nq", "nr", "ns", "nt", "nu", "nv", "nw", "nx", "ny", "nz", "oa", "ob", "oc", "od", "oe", "of", "og", "oh", "oi", "oj", "ok", "ol", "om", "on", "oo", "op", "oq", "or", "os", "ot", "ou", "ov", "ow", "ox", "oy", "oz", "pa", "pb", "pc", "pd", "pe", "pf", "pg", "ph", "pi", "pj", "pk", "pl", "pm", "pn", "po", "pp", "pq", "pr", "ps", "pt", "pu", "pv", "pw", "px", "py", "pz", "qa", "qb", "qc", "qd", "qe", "qf", "qg", "qh", "qi", "qj", "qk", "ql", "qm", "qn", "qo", "qp", "qq", "qr", "qs", "qt", "qu", "qv", "qw", "qx", "qy", "qz", "ra", "rb", "rc", "rd", "re", "rf", "rg", "rh", "ri", "rj", "rk", "rl", "rm", "rn", "ro", "rp", "rq", "rr", "rs", "rt", "ru", "rv", "rw", "rx", "ry", "rz", "sa", "sb", "sc", "sd", "se", "sf", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sp", "sq", "sr", "ss", "st", "su", "sv", "sw", "sx", "sy", "sz", "ta", "tb", "tc", "td", "te", "tf", "tg", "th", "ti", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tq", "tr", "ts", "tt", "tu", "tv", "tw", "tx", "ty", "tz", "ua", "ub", "uc", "ud", "ue", "uf", "ug", "uh", "ui", "uj", "uk", "ul", "um", "un", "uo", "up", "uq", "ur", "us", "ut", "uu", "uv", "uw", "ux", "uy", "uz", "va", "vb", "vc", "vd", "ve", "vf", "vg", "vh", "vi", "vj", "vk", "vl", "vm", "vn", "vo", "vp", "vq", "vr", "vs", "vt", "vu", "vv", "vw", "vx", "vy", "vz", "wa", "wb", "wc", "wd", "we", "wf", "wg", "wh", "wi", "wj", "wk", "wl", "wm", "wn", "wo", "wp", "wq", "wr", "ws", "wt", "wu", "wv", "ww", "wx", "wy", "wz", "xa", "xb", "xc", "xd", "xe", "xf", "xg", "xh", "xi", "xj", "xk", "xl", "xm", "xn", "xo", "xp", "xq", "xr", "xs", "xt", "xu", "xv", "xw", "xx", "xy", "xz", "ya", "yb", "yc", "yd", "ye", "yf", "yg", "yh", "yi", "yj", "yk", "yl", "ym", "yn", "yo", "yp", "yq", "yr", "ys", "yt", "yu", "yv", "yw", "yx", "yy", "yz", "za", "zb", "zc", "zd", "ze", "zf", "zg", "zh", "zi", "zj", "zk", "zl", "zm", "zn", "zo", "zp", "zq", "zr", "zs", "zt", "zu", "zv", "zw", "zx", "zy", "zz"};

    @Unique
    private static ServerPlayerEntity makeServerPlayerEntity(String playerName) {
        MinecraftServer server = Fuji.SERVER;
        ServerWorld world = server.getWorlds().iterator().next();
        GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes(playerName.getBytes()), playerName);
        SyncedClientOptions syncedClientOptions = SyncedClientOptions.createDefault();
        ServerPlayerEntity player = new ServerPlayerEntity(server, world, gameProfile, syncedClientOptions);
        ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
        ConnectedClientData connectedClientData = ConnectedClientData.createDefault(gameProfile, false);
        player.networkHandler = new ServerPlayNetworkHandler(server, clientConnection, player, connectedClientData);
        return player;
    }

    @Unique
    private static Integer getWeight(ServerPlayerEntity player) {
        Optional<Integer> weight = PermissionUtil.getMeta(player, "fuji.tab_list.sort.weight", Integer::valueOf);
        return weight.orElse(0);
    }


    @Unique
    private void sortPlayerList(List<ServerPlayerEntity> players) {
        /* sort real players */
        players.sort((p1, p2) -> {
            Integer w1 = getWeight(p1);
            Integer w2 = getWeight(p2);

            if (w1.equals(w2)) {
                return p1.getGameProfile().getName().compareTo(p2.getGameProfile().getName());
            }

            return w2.compareTo(w1);
        });
    }

    @Unique
    void sendToOtherPlayers(ServerPlayerEntity player, Packet<?> packet) {
        for (ServerPlayerEntity serverPlayerEntity : Fuji.SERVER.getPlayerManager().getPlayerList()) {
            if (serverPlayerEntity == player) continue;
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }

    @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;entryFromPlayer(Ljava/util/Collection;)Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;", ordinal = 0))
    Collection<ServerPlayerEntity> makeEncodedPlayerList(Collection<ServerPlayerEntity> collection, @Local(argsOnly = true) ServerPlayerEntity player) {

        List<ServerPlayerEntity> sortedPlayerList = new ArrayList<>(Fuji.SERVER.getPlayerManager().getPlayerList());
        // add new joined player into the player list.
        sortedPlayerList.add(player);
        sortPlayerList(sortedPlayerList);

        List<ServerPlayerEntity> sortedFakePlayerList = new ArrayList<>();

        for (int i = 0; i < sortedPlayerList.size(); i++) {
            ServerPlayerEntity sortedPlayer = sortedPlayerList.get(i);

//            if (sortedPlayer == player) {
//                continue;
//            }

            /* make fake sortedPlayerList */
            String sort_prefix = ALPHA_TABLE[i];
            String playerName = sortedPlayer.getGameProfile().getName();
            String encodedName = sort_prefix + TabListInitializer.META_SEPARATOR + playerName;

            sortedFakePlayerList.add(makeServerPlayerEntity(encodedName));
        }

        // broadcast to others
//        EnumSet<PlayerListS2CPacket.Action> enumSet = EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.INITIALIZE_CHAT, PlayerListS2CPacket.Action.UPDATE_GAME_MODE, PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
//        PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(enumSet, List.of(player));
//        player.networkHandler.sendPacket(playerListS2CPacket);
//        sendToOtherPlayers(player, );

        return sortedFakePlayerList;
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    void dontSendAddPlayerActionToNewJoinedPlayer(PlayerManager instance, Packet<?> packet, @Local(argsOnly = true) ServerPlayerEntity serverPlayerEntity) {

//        this.sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(serverPlayerEntity)));

        // remove Action.ADD_PLAYER here
//        EnumSet<PlayerListS2CPacket.Action> enumSet = EnumSet.of(PlayerListS2CPacket.Action.INITIALIZE_CHAT, PlayerListS2CPacket.Action.UPDATE_GAME_MODE, PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
//
//        // re-send the packet
//        PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(enumSet, List.of(serverPlayerEntity));
//        Fuji.SERVER.getPlayerManager().sendToAll(playerListS2CPacket);
    }

}
