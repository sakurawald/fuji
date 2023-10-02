package fun.sakurawald.module.display.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;

public abstract class DisplayGuiBuilder {

    protected static final int LINE_SIZE = 9;

    public abstract SimpleGui build(ServerPlayer player);

}
