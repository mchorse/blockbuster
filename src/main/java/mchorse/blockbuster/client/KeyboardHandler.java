package mchorse.blockbuster.client;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.client.gui.GuiGun;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketTickMarker;
import mchorse.blockbuster.network.common.director.PacketDirectorPlayback;
import mchorse.blockbuster.network.common.director.PacketDirectorRecord;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Separate event handler for keyboard events
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    /* Misc. */
    private KeyBinding dashboard;
    private KeyBinding modelEditor;
    private KeyBinding cameraMarker;
    private KeyBinding plauseDirector;
    private KeyBinding recordDirector;
    private KeyBinding openGun;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        /* Key categories */
        String category = "key.blockbuster.category";

        /* Misc */
        this.dashboard = new KeyBinding("key.blockbuster.dashboard", Keyboard.KEY_0, category);
        this.cameraMarker = new KeyBinding("key.blockbuster.marker", Keyboard.KEY_V, category);
        this.modelEditor = new KeyBinding("key.blockbuster.model_editor", Keyboard.KEY_NONE, category);
        this.plauseDirector = new KeyBinding("key.blockbuster.plause_director", Keyboard.KEY_NONE, category);
        this.recordDirector = new KeyBinding("key.blockbuster.record_director", Keyboard.KEY_NONE, category);
        this.openGun = new KeyBinding("key.blockbuster.open_gun", Keyboard.KEY_NONE, category);

        ClientRegistry.registerKeyBinding(this.dashboard);
        ClientRegistry.registerKeyBinding(this.cameraMarker);
        ClientRegistry.registerKeyBinding(this.modelEditor);
        ClientRegistry.registerKeyBinding(this.plauseDirector);
        ClientRegistry.registerKeyBinding(this.recordDirector);
        ClientRegistry.registerKeyBinding(this.openGun);
    }

    @SubscribeEvent
    public void onUserLogOut(ClientDisconnectionFromServerEvent event)
    {
        GuiDashboard.reset();
        ClientProxy.manager.reset();
        ClientProxy.recordingOverlay.setVisible(false);

        Minecraft.getMinecraft().addScheduledTask(() -> StructureMorph.cleanUp());

        if (CameraHandler.isApertureLoaded())
        {
            CameraHandler.server = false;
        }
    }

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        if (this.dashboard.isPressed())
        {
            ClientProxy.getDashboard(false).open().openPanel(null);
        }

        if (this.cameraMarker.isPressed())
        {
            Dispatcher.sendToServer(new PacketTickMarker());
        }

        if (this.modelEditor.isPressed())
        {
            GuiDashboard dashboard = ClientProxy.getDashboard(false);

            dashboard.open().openPanel(dashboard.modelEditorPanel);
        }

        if (this.plauseDirector.isPressed())
        {
            GuiDashboard dash = ClientProxy.dashboard;

            if (dash != null && dash.directorPanel != null)
            {
                BlockPos director = dash.directorPanel.getPos();

                if (director != null)
                {
                    Dispatcher.sendToServer(new PacketDirectorPlayback(director));
                }
            }
        }

        if (this.recordDirector.isPressed())
        {
            GuiDashboard dash = ClientProxy.dashboard;

            if (dash != null && dash.directorPanel != null)
            {
                BlockPos director = dash.directorPanel.getPos();
                Replay replay = dash.directorPanel.getReplay();

                if (director != null && replay != null && !replay.id.isEmpty())
                {
                    Dispatcher.sendToServer(new PacketDirectorRecord(director, replay.id));
                }
            }
        }

        if (this.openGun.isPressed())
        {
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack stack = mc.thePlayer.getHeldItemMainhand();

            if (stack != null && stack.getItem() == Blockbuster.gunItem)
            {
                mc.displayGuiScreen(new GuiGun(stack));
            }
        }
    }
}