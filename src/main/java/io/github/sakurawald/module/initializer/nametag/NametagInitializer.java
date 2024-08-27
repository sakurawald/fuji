package io.github.sakurawald.module.initializer.nametag;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.nametag.job.UpdateNametagJob;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;


public class NametagInitializer extends ModuleInitializer {

    private static Map<ServerPlayerEntity, ElementHolder> player2holder;

    @Override
    public void onInitialize() {
        player2holder = new HashMap<>();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new UpdateNametagJob().schedule());
    }

    @Override
    public void onReload() {
        player2holder = new HashMap<>();
    }

    private static ElementHolder makeElementHolder(ServerPlayerEntity player) {
        ElementHolder holder = new ElementHolder();
        var element = new TextDisplayElement();
        holder.addElement(element);

        // bind the holder with given player
        EntityAttachment.of(holder, player);
        // let the element ride the player, so that the position of the element syncs with the movement of the player.
        VirtualEntityUtils.addVirtualPassenger(player, element.getEntityId());
        return holder;
    }

    private static void updateElement(VirtualElement virtualElement, ServerPlayerEntity player) {
        var config = Configs.configHandler.model().modules.nametag;

        if (virtualElement instanceof TextDisplayElement textDisplayElement) {
            // parse
            Text text = MessageHelper.ofText(player, false, config.style.text);
            textDisplayElement.setText(text);
            textDisplayElement.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
            textDisplayElement.setTranslation(new Vector3f(config.style.offset.x, config.style.offset.y, config.style.offset.z));

            textDisplayElement.setDisplaySize(config.style.size.width, config.style.size.height);

            textDisplayElement.setBackground(config.style.color.background);
            textDisplayElement.setTextOpacity(config.style.color.text_opacity);

            textDisplayElement.setScale(new Vector3f(config.style.scale.x, config.style.scale.y, config.style.scale.z));

            textDisplayElement.setShadow(config.style.shadow.shadow);
            textDisplayElement.setShadowRadius(config.style.shadow.shadow_radius);
            textDisplayElement.setShadowStrength(config.style.shadow.shadow_strength);

            textDisplayElement.setSeeThrough(config.render.see_through_blocks);
            textDisplayElement.setViewRange(config.render.view_range);

            if (config.style.brightness.override_brightness) {
                textDisplayElement.setBrightness(new Brightness(config.style.brightness.block, config.style.brightness.sky));
            }

            // tick() to send tracked data update packet.
            textDisplayElement.tick();
        }
    }

    public static void update() {
        // invalid keys
        player2holder.entrySet().removeIf(entry -> entry.getKey().isRemoved());

        ServerHelper.getPlayers().forEach(player -> {
            // make if not exists
            if (!player2holder.containsKey(player)) {
                player2holder.put(player, makeElementHolder(player));
            }

            // update
            ElementHolder holder = player2holder.get(player);
            holder.getElements().forEach(element -> updateElement(element, player));
        });
    }

}
