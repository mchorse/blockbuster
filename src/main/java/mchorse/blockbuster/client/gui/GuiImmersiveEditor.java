package mchorse.blockbuster.client.gui;

import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.utils.EntityUtils;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Immersive Editor
 * 
 * This editor allows you to edit morphs in the game world
 */
@SideOnly(Side.CLIENT)
public class GuiImmersiveEditor extends GuiBase
{
    public GuiImmersiveMorphMenu morphs;

    public GuiScreen lastScreen;
    public int lastTPS;
    public GameType lastMode;

    public boolean resetViewport;
    public double lastPosX;
    public double lastPosY;
    public double lastPosZ;
    public float lastRotPitch;
    public float lastRotYaw;

    public Consumer<AbstractMorph> callback;

    public GuiImmersiveEditor(Minecraft mc)
    {
        this.mc = mc;

        this.morphs = new GuiImmersiveMorphMenu(mc, (morph) -> 
        {
            if (this.callback != null)
            {
                this.callback.accept(MorphUtils.copy(morph));
            }
        });
        this.morphs.flex().relative(this.viewport).xy(0F, 0F).wh(1F, 1F);

        this.root.add(morphs);
    }

    public void show()
    {
        this.lastScreen = this.mc.currentScreen;
        this.lastTPS = this.mc.gameSettings.thirdPersonView;
        this.lastMode = EntityUtils.getGameMode();

        this.resetViewport = true;
        this.lastPosX = this.mc.player.posX;
        this.lastPosY = this.mc.player.posY;
        this.lastPosZ = this.mc.player.posZ;
        this.lastRotPitch = this.mc.player.rotationPitch;
        this.lastRotYaw = this.mc.player.rotationYaw;

        this.mc.currentScreen = this;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        this.setWorldAndResolution(this.mc, i, j);

        this.morphs.reload();
        this.morphs.resize();

        this.mc.gameSettings.thirdPersonView = 0;
        this.mc.setRenderViewEntity(this.mc.player);

        if (this.lastMode != GameType.SPECTATOR)
        {
            this.mc.player.sendChatMessage("/gamemode 3");
        }

        MinecraftForge.EVENT_BUS.register(this.morphs);

        if (this.callback != null)
        {
            this.callback.accept(MorphUtils.copy(this.morphs.getSelected()));
        }
    }

    public void keepViewport()
    {
        this.resetViewport = false;

        this.morphs.keepViewport();
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        this.closeScreen();

        this.lastScreen.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void closeScreen()
    {
        if (this.lastScreen == null)
        {
            return;
        }

        this.morphs.finish();

        this.mc.currentScreen = this.lastScreen;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        this.lastScreen.setWorldAndResolution(this.mc, i, j);

        this.mc.gameSettings.thirdPersonView = this.lastTPS;
        this.mc.player.sendChatMessage("/gamemode " + this.lastMode.getID());

        if (this.resetViewport)
        {
            this.mc.player.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.lastRotYaw, this.lastRotPitch);
        }

        this.lastScreen = null;
        MinecraftForge.EVENT_BUS.unregister(this.morphs);

        if (this.callback != null)
        {
            this.callback.accept(this.morphs.getSelected());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (!this.morphs.isImmersionMode())
        {
            GuiDraw.drawCustomBackground(0, 0, this.width, this.height);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
