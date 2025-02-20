package net.mehvahdjukaar.supplementaries.configs.forge;

import com.simibubi.create.content.contraptions.components.actors.DrillBlock;
import net.mehvahdjukaar.supplementaries.integration.CompatHandler;
import net.mehvahdjukaar.supplementaries.integration.forge.configured.ModConfigSelectScreen;
import net.minecraft.client.Minecraft;
import vazkii.quark.content.automation.module.IronRodModule;

public class ConfigUtilsImpl {
    public static void openModConfigs() {

        if (CompatHandler.CONFIGURED) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new ModConfigSelectScreen(Minecraft.getInstance().screen));
        }
    }
}
