package mchorse.blockbuster.common;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.events.CameraEditorPlaybackStateEvent;
import mchorse.aperture.events.CameraEditorScrubbedEvent;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorGoto;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorPlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CameraHandler
{
    @Method(modid = "aperture")
    public static void register()
    {
        ClientProxy.EVENT_BUS.register(new CameraHandler());
    }

    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraScrub(CameraEditorScrubbedEvent event)
    {
        BlockPos pos = this.getDirectorPos();

        if (pos != null)
        {
            Dispatcher.sendToServer(new PacketDirectorGoto(pos, event.position));
        }
    }

    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraPlause(CameraEditorPlaybackStateEvent event)
    {
        BlockPos pos = this.getDirectorPos();

        if (pos != null)
        {
            Dispatcher.sendToServer(new PacketDirectorPlay(pos, event.play ? PacketDirectorPlay.PLAY : PacketDirectorPlay.PAUSE, event.position));
        }
    }

    private BlockPos getDirectorPos()
    {
        BlockPos pos = null;
        ItemStack left = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();

        if (left != null && left.getItem() instanceof ItemPlayback)
        {
            pos = ItemPlayback.getBlockPos("Dir", left);
        }

        return pos;
    }

    public static class CameraGUIHandler
    {
        @Method(modid = "aperture")
        @SubscribeEvent
        public void onGuiOpen(GuiOpenEvent event)
        {
            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            GuiScreen toOpen = event.getGui();

            if (current == null && toOpen instanceof GuiCameraEditor)
            {
                /* Camera editor opens */
            }
            else if (current instanceof GuiCameraEditor && toOpen == null)
            {
                /* Camera editor closes */
            }
        }
    }
}