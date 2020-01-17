package mchorse.blockbuster.aperture.gui;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.client.gui.config.GuiAbstractConfigOptions;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.sync.PacketScenePlay;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDirectorConfigOptions extends GuiAbstractConfigOptions
{
    private String title = I18n.format("blockbuster.gui.aperture.config.title");

    public GuiButtonElement<GuiCheckBox> actions;
    public GuiButtonElement<GuiCheckBox> reload;
    public GuiButtonElement<GuiButton> reloadScene;

    public int max;
    public int x;
    public int y;

    public GuiDirectorConfigOptions(Minecraft mc, GuiCameraEditor editor)
    {
        super(mc, editor);

        this.reload = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.aperture.config.reload"), CameraHandler.reload, (b) ->
        {
            CameraHandler.reload = b.button.isChecked();
        });

        this.actions = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.aperture.config.actions"), CameraHandler.actions, (b) ->
        {
            CameraHandler.actions = b.button.isChecked();
        });

        this.reloadScene = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.aperture.config.reload_scene"), (b) ->
        {
            BlockPos pos = CameraHandler.getDirectorPos();

            Dispatcher.sendToServer(new PacketScenePlay(pos, PacketScenePlay.RESTART, ClientProxy.getCameraEditor().scrub.value));
        });

        this.reloadScene.button.width = 100;
        this.children.add(this.reload, this.actions, this.reloadScene);

        int i = 0;

        for (IGuiElement element : this.children.elements)
        {
            if (element instanceof GuiButtonElement)
            {
                GuiButtonElement button = (GuiButtonElement) element;

                button.resizer().parent(this.area).set(4, 4 + i * 18 + 20, button.button.width, button.button.height);
                this.max = Math.max(this.max, button.button.width);

                i++;
            }
        }
    }

    @Override
    public void update()
    {
        this.reload.button.setIsChecked(CameraHandler.reload);
        this.actions.button.setIsChecked(CameraHandler.actions);
    }

    @Override
    public int getWidth()
    {
        return Math.max(this.max + 8, Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.title) + 8);
    }

    @Override
    public int getHeight()
    {
        return this.children.elements.size() * 18 + 30;
    }

    @Override
    public boolean isActive()
    {
        return CameraHandler.getDirectorPos() != null;
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.y + 20, 0x88000000);
        this.font.drawString(this.title, this.area.x + 6, this.area.y + 7, 0xffffff, true);

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}