package com.greatericontop.tickaligned;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "tickaligned", name = "Tick-Aligned Position", version = "1.0.0",
        clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class TickAlignedMod {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Default key is H; it can be rebound in Options > Controls.
        // The names shown there come from assets/tickaligned/lang/en_US.lang.
        KeyBinding toggleKey = new KeyBinding("key.tickaligned.toggle", Keyboard.KEY_H, "key.categories.tickaligned");
        ClientRegistry.registerKeyBinding(toggleKey);

        MinecraftForge.EVENT_BUS.register(new PositionSnapper(toggleKey));
    }
}
