package mchorse.blockbuster.client;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.GuiGun;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Separate event handler for keyboard events
 */
@SideOnly(Side.CLIENT)
public class KeyboardHandler
{
    /* Misc. */
    private KeyBinding plause;
    private KeyBinding record;
    private KeyBinding pause;
    private KeyBinding openGun;

    /**
     * Create and register key bindings for mod
     */
    public KeyboardHandler()
    {
        /* Key categories */
        String category = "key.blockbuster.category";

        /* Misc */
        this.plause = new KeyBinding("key.blockbuster.plause_director", Keyboard.KEY_RCONTROL, category);
        this.record = new KeyBinding("key.blockbuster.record_director", Keyboard.KEY_RMENU, category);
        this.pause = new KeyBinding("key.blockbuster.pause_director", Keyboard.KEY_RSHIFT, category);
        this.openGun = new KeyBinding("key.blockbuster.open_gun", Keyboard.KEY_END  , category);

        ClientRegistry.registerKeyBinding(this.plause);
        ClientRegistry.registerKeyBinding(this.record);
        ClientRegistry.registerKeyBinding(this.pause);
        ClientRegistry.registerKeyBinding(this.openGun);
    }

    @SubscribeEvent
    public void onUserLogOut(ClientDisconnectionFromServerEvent event)
    {
        ClientProxy.manager.reset();
        ClientProxy.recordingOverlay.setVisible(false);
        RenderingHandler.resetEmitters();

        Minecraft.getMinecraft().addScheduledTask(StructureMorph::cleanUp);
    }

    /**
     * Handle keys
     */
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event)
    {
        if (this.plause.isPressed())
        {
            if (ClientProxy.panels.scenePanel != null)
            {
                ClientProxy.panels.scenePanel.plause();
            }
        }

        if (this.record.isPressed())
        {
            if (ClientProxy.panels.scenePanel != null)
            {
                ClientProxy.panels.scenePanel.record();
            }
        }

        if (this.pause.isPressed())
        {
            if (ClientProxy.panels.scenePanel != null)
            {
                ClientProxy.panels.scenePanel.pause();
            }
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (this.openGun.isPressed() && mc.player.capabilities.isCreativeMode && OpHelper.isPlayerOp())
        {
            ItemStack stack = mc.player.getHeldItemMainhand();

            if (stack.getItem() == Blockbuster.gunItem)
            {
                mc.displayGuiScreen(new GuiGun(stack));
            }
        }
    }
}