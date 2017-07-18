package mchorse.blockbuster.aperture;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.events.CameraEditorPlaybackStateEvent;
import mchorse.aperture.events.CameraEditorScrubbedEvent;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.aperture.network.client.ClientHandlerCameraProfileList;
import mchorse.blockbuster.aperture.network.client.ClientHandlerSceneLength;
import mchorse.blockbuster.aperture.network.common.PacketPlaybackButton;
import mchorse.blockbuster.aperture.network.common.PacketRequestLength;
import mchorse.blockbuster.aperture.network.common.PacketRequestProfiles;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.blockbuster.aperture.network.server.ServerHandlerRequestProfiles;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorGoto;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorPlay;
import mchorse.blockbuster.network.server.ServerHandlerPlaybackButton;
import mchorse.blockbuster.network.server.ServerHandlerRequestLength;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Camera handler
 *
 * This is the event listener for soft dependency integration with Aperture mod.
 * Basically what it does is hooks up these event listeners to Aperture and GUI
 * events and control the director block playback based on these events.
 */
public class CameraHandler
{
    /**
     * Tick which is used to set the value of the camera editor scrub back
     */
    public static int tick = 0;

    /**
     * Check whether Aperture is loaded
     */
    public static boolean isApertureLoaded()
    {
        return Loader.isModLoaded("aperture");
    }

    @Method(modid = "aperture")
    public static void register()
    {
        ClientProxy.EVENT_BUS.register(new CameraHandler());
        MinecraftForge.EVENT_BUS.register(new CameraGUIHandler());
    }

    @Method(modid = "aperture")
    public static void registerMessages()
    {
        /* Camera management */
        Dispatcher.register(PacketPlaybackButton.class, ServerHandlerPlaybackButton.class, Side.SERVER);
        Dispatcher.register(PacketRequestProfiles.class, ServerHandlerRequestProfiles.class, Side.SERVER);
        Dispatcher.register(PacketCameraProfileList.class, ClientHandlerCameraProfileList.class, Side.CLIENT);

        Dispatcher.register(PacketRequestLength.class, ServerHandlerRequestLength.class, Side.SERVER);
        Dispatcher.register(PacketSceneLength.class, ClientHandlerSceneLength.class, Side.CLIENT);
    }

    @Method(modid = "aperture")
    public static void handlePlaybackItem(EntityPlayer player, NBTTagCompound tag)
    {
        /* To allow actors using playback item without a crash */
        if (player instanceof EntityPlayerMP)
        {
            if (tag.hasKey("CameraPlay"))
            {
                CameraAPI.playCurrentProfile((EntityPlayerMP) player);
            }
            else if (tag.hasKey("CameraProfile"))
            {
                CameraAPI.playCameraProfile((EntityPlayerMP) player, new ResourceLocation(tag.getString("CameraProfile")));
            }
        }
    }

    /* Event listeners */

    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraScrub(CameraEditorScrubbedEvent event)
    {
        BlockPos pos = getDirectorPos();

        if (pos != null && !ClientProxy.runner.isRunning())
        {
            Dispatcher.sendToServer(new PacketDirectorGoto(pos, event.position));
        }
    }

    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraPlause(CameraEditorPlaybackStateEvent event)
    {
        BlockPos pos = getDirectorPos();

        if (pos != null)
        {
            Dispatcher.sendToServer(new PacketDirectorPlay(pos, event.play ? PacketDirectorPlay.PLAY : PacketDirectorPlay.PAUSE, event.position));
        }
    }

    /**
     * Get director block position from player's playback button
     */
    public static BlockPos getDirectorPos()
    {
        BlockPos pos = null;
        ItemStack left = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();

        if (left != null && left.getItem() instanceof ItemPlayback)
        {
            pos = ItemPlayback.getBlockPos("Dir", left);
        }

        return pos;
    }

    /**
     * Camera editor GUI handler
     *
     * This is also the part of the whole camera editor thing, but for
     * exception it only spawns actors when the camera editor GUI is getting
     * opened.
     */
    public static class CameraGUIHandler
    {
        @Method(modid = "aperture")
        @SubscribeEvent
        public void onGuiOpen(GuiOpenEvent event)
        {
            if (Minecraft.getMinecraft().thePlayer == null)
            {
                return;
            }

            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            GuiScreen toOpen = event.getGui();
            BlockPos pos = getDirectorPos();

            if (pos != null)
            {
                int tick = ClientProxy.cameraEditor.scrub.value;

                if (current != ClientProxy.cameraEditor && toOpen instanceof GuiCameraEditor)
                {
                    /* Camera editor opens */
                    CameraHandler.tick = tick;

                    Dispatcher.sendToServer(new PacketDirectorPlay(pos, PacketDirectorPlay.START, tick));
                    Dispatcher.sendToServer(new PacketRequestLength(pos));
                }
            }
        }
    }
}