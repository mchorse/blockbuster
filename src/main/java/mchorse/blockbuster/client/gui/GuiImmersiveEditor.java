package mchorse.blockbuster.client.gui;

import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.ImmutableList;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
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
    public static final IKey CATEGORY = IKey.lang("blockbuster.gui.immersive_editor.keys.category");

    public GuiImmersiveMorphMenu morphs;
    public GuiOuterScreen outerPanel;

    public GuiScreen lastScreen;
    public int lastTPS;
    public GameType lastMode;

    public double lastPosX;
    public double lastPosY;
    public double lastPosZ;
    public float lastRotPitch;
    public float lastRotYaw;

    public Consumer<GuiImmersiveEditor> onClose;

    public GuiImmersiveEditor(Minecraft mc)
    {
        this.mc = mc;

        this.morphs = new GuiImmersiveMorphMenu(mc);
        this.morphs.flex().relative(this.viewport).xy(0F, 0F).wh(1F, 1F);

        this.outerPanel = new GuiOuterScreen(mc);
        this.outerPanel.flex().relative(this.viewport).xy(0F, 0F).wh(1F, 1F);

        this.root.add(morphs, this.outerPanel);

        this.root.keys().register(IKey.lang("blockbuster.gui.immersive_editor.keys.toggle_outer_panel"), Keyboard.KEY_F1, () -> this.outerPanel.toggleVisible())
            .category(CATEGORY).active(() -> !this.outerPanel.getChildren().isEmpty());
    }

    public void show()
    {
        this.lastScreen = this.mc.currentScreen;
        this.lastTPS = this.mc.gameSettings.thirdPersonView;
        this.lastMode = EntityUtils.getGameMode();

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
        this.morphs.setVisible(true);
        this.outerPanel.setVisible(false);

        this.mc.gameSettings.thirdPersonView = 0;
        this.mc.setRenderViewEntity(this.mc.player);

        if (this.lastMode != GameType.SPECTATOR)
        {
            this.mc.player.sendChatMessage("/gamemode 3");
        }

        MinecraftForge.EVENT_BUS.register(this.morphs);
    }

    @Override
    public void onGuiClosed()
    {
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

        this.mc.player.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.lastRotYaw, this.lastRotPitch);

        this.lastScreen = null;
        MinecraftForge.EVENT_BUS.unregister(this.morphs);

        if (this.onClose != null)
        {
            this.onClose.accept(this);
            this.onClose = null;
        }

        this.morphs.setVisible(false);
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

    public static class GuiOuterScreen extends GuiElement
    {
        public GuiOuterScreen(Minecraft mc)
        {
            super(mc);
        }

        @Override
        public boolean mouseClicked(GuiContext context)
        {
            List<IGuiElement> list = ImmutableList.copyOf(this.getChildren());

            for (int i = list.size() - 1; i >= 0; i--)
            {
                IGuiElement element = list.get(i);

                if (element.isEnabled() && element.mouseClicked(context))
                {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean mouseScrolled(GuiContext context)
        {
            List<IGuiElement> list = ImmutableList.copyOf(this.getChildren());

            for (int i = list.size() - 1; i >= 0; i--)
            {
                IGuiElement element = list.get(i);

                if (element.isEnabled() && element.mouseScrolled(context))
                {
                    return true;
                }
            }

            return false;
        }

        @Override
        public void mouseReleased(GuiContext context)
        {
            List<IGuiElement> list = ImmutableList.copyOf(this.getChildren());

            for (int i = list.size() - 1; i >= 0; i--)
            {
                IGuiElement element = list.get(i);

                if (element.isEnabled())
                {
                    element.mouseReleased(context);
                }
            }
        }

        @Override
        public void draw(GuiContext context)
        {
            GuiDraw.drawCustomBackground(this.area.x, this.area.y, this.area.ex(), this.area.ey());

            super.draw(context);

            GuiDraw.drawTextBackground(this.font, IKey.lang("blockbuster.gui.immersive_editor.hide_outer_panel").get(), this.area.x + 5, this.area.y + 5, 0xFFFFFF, 0);
        }
    }
}
