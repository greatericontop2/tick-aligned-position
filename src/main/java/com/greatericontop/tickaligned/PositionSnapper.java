package com.greatericontop.tickaligned;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * How vanilla rendering works: every frame, the player (and the camera) is drawn at
 *     prev + (current - prev) * partialTicks
 * where "current" is the position computed by the last physics tick, "prev" is the position one
 * tick before that, and partialTicks (0..1) is how far we are between ticks. That blend is what
 * makes movement look smooth at frame rates above 20 FPS.
 *
 * This class makes prev == current for the duration of each frame, so the blend always comes out to
 * exactly the current tick position. The real values are put back once the frame is drawn, so
 * physics and anything else that reads them during the tick never sees the change.
 *
 * Mouse look is unaffected: in 1.8.9 the player's rotation is updated every frame (not every tick),
 * and we only touch position fields. Nothing about frame rate or the game timer is changed.
 */
public class PositionSnapper {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final KeyBinding toggleKey;

    private boolean enabled = false;

    // The player we modified this frame and its original values, or null if we didn't modify anything.
    private EntityPlayerSP snappedPlayer = null;
    private double savedPrevX, savedPrevY, savedPrevZ;
    private double savedLastTickX, savedLastTickY, savedLastTickZ;

    public PositionSnapper(KeyBinding toggleKey) {
        this.toggleKey = toggleKey;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // isPressed() consumes one queued press per call, so a loop handles fast repeated presses correctly.
        while (toggleKey.isPressed()) {
            enabled = !enabled;
            if (mc.thePlayer != null) {
                String state = enabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                mc.thePlayer.addChatMessage(new ChatComponentText("Tick-aligned position: " + state));
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        // RenderTickEvent fires once per frame: START right before the world is drawn, END right after.
        // All of this frame's physics ticks have already run before START, so posX/Y/Z are up to date here.
        if (event.phase == TickEvent.Phase.START) {
            snap();
        } else {
            restore();
        }
    }

    private void snap() {
        EntityPlayerSP player = mc.thePlayer;
        if (!enabled || player == null) {
            return;
        }
        snappedPlayer = player;

        // Two different "previous position" fields both get interpolated with partialTicks:
        //   prevPos*     - used for the first-person camera (EntityRenderer.orientCamera)
        //   lastTickPos* - used for drawing the entity itself (player model in third person,
        //                  shadow, name tag) and for the render origin of the world
        // Both need to equal the current position for the result to come out un-interpolated.
        savedPrevX = player.prevPosX;
        savedPrevY = player.prevPosY;
        savedPrevZ = player.prevPosZ;
        savedLastTickX = player.lastTickPosX;
        savedLastTickY = player.lastTickPosY;
        savedLastTickZ = player.lastTickPosZ;

        player.prevPosX = player.lastTickPosX = player.posX;
        player.prevPosY = player.lastTickPosY = player.posY;
        player.prevPosZ = player.lastTickPosZ = player.posZ;
    }

    private void restore() {
        // Restore whatever we snapped, even if the toggle was switched off mid-frame,
        // so the original values are never lost.
        if (snappedPlayer == null) {
            return;
        }
        snappedPlayer.prevPosX = savedPrevX;
        snappedPlayer.prevPosY = savedPrevY;
        snappedPlayer.prevPosZ = savedPrevZ;
        snappedPlayer.lastTickPosX = savedLastTickX;
        snappedPlayer.lastTickPosY = savedLastTickY;
        snappedPlayer.lastTickPosZ = savedLastTickZ;
        snappedPlayer = null;
    }
}
