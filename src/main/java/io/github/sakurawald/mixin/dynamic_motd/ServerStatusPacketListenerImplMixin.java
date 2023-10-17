/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2023 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.sakurawald.mixin.dynamic_motd;

import io.github.sakurawald.ServerMain;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.motd.DynamicMotdModule;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ServerStatusPacketListenerImpl.class)
@Slf4j
abstract class ServerStatusPacketListenerImplMixin {

    @Unique
    private static final DynamicMotdModule module = ModuleManager.getOrNewInstance(DynamicMotdModule.class);

    @Redirect(method = "handleStatusRequest", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerStatusPacketListenerImpl;status:Lnet/minecraft/network/protocol/status/ServerStatus;"))
    public ServerStatus $handleStatusRequest(final ServerStatusPacketListenerImpl instance) {
        ServerStatus vanillaStatus = ServerMain.SERVER.getStatus();
        if (vanillaStatus == null) {
            log.warn("ServerStatus is null, use default.");
            return new ServerStatus(module.getRandomDescription(), Optional.empty(), Optional.empty(), module.getRandomIcon(), false);
        }
        return new ServerStatus(module.getRandomDescription(), vanillaStatus.players(), vanillaStatus.version(), module.getRandomIcon(), vanillaStatus.enforcesSecureChat());
    }
}
