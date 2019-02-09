package mchorse.blockbuster.client.gui.elements.texture;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.utils.MultiResourceLocation;
import mchorse.blockbuster.utils.RLUtils;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Texture picker GUI
 * 
 * This bad boy allows picking a texture from the file browser, and also 
 * it allows creating multi-skins. See {@link MultiResourceLocation} for 
 * more information.
 */
public class GuiTexturePicker extends GuiElement
{
    public GuiTextElement text;
    public GuiButtonElement<GuiButton> pick;
    public GuiFolderEntryList picker;

    public GuiButtonElement<GuiButton> multi;
    public GuiButtonElement<GuiTextureButton> add;
    public GuiButtonElement<GuiTextureButton> remove;
    public GuiResourceLocationList multiList;

    public Consumer<ResourceLocation> callback;

    public MultiResourceLocation multiRL;
    public ResourceLocation current;

    public GuiTexturePicker(Minecraft mc, Consumer<ResourceLocation> callback)
    {
        super(mc);

        this.text = new GuiTextElement(mc, 1000, (str) -> this.selectCurrent(str.isEmpty() ? null : RLUtils.create(str)));
        this.pick = GuiButtonElement.button(mc, "X", (b) -> this.setVisible(false));
        this.picker = new GuiFolderEntryList(mc, (entry) ->
        {
            ResourceLocation rl = entry.resource;

            this.selectCurrent(rl);
            this.text.setText(rl == null ? "" : rl.toString());
        });

        this.multi = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.multi_skin"), (b) -> this.toggleMultiSkin());
        this.multiList = new GuiResourceLocationList(mc, (rl) -> this.displayCurrent(rl));
        this.add = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 32, 32, 32, 48, (b) -> this.addMultiSkin());
        this.remove = GuiButtonElement.icon(mc, GuiDashboard.ICONS, 64, 32, 64, 48, (b) -> this.removeMultiSkin());

        this.createChildren();
        this.text.resizer().set(115, 5, 0, 20).parent(this.area).w(1, -145);
        this.pick.resizer().set(0, 5, 20, 20).parent(this.area).x(1, -25);
        this.picker.resizer().set(115, 30, 0, 0).parent(this.area).w(1, -120).h(1, -30);

        this.multi.resizer().parent(this.area).set(5, 5, 100, 20);
        this.add.resizer().parent(this.area).set(67, 7, 16, 16);
        this.remove.resizer().relative(this.add.resizer()).set(20, 0, 16, 16);
        this.multiList.resizer().set(5, 35, 100, 0).parent(this.area).h(1, -40);

        this.children.add(this.text, this.pick, this.picker, this.multi, this.multiList, this.add, this.remove);
        this.callback = callback;
    }

    public void fill(ResourceLocation skin)
    {
        this.setMultiSkin(skin, false);
    }

    /**
     * Add a {@link ResourceLocation} to the multiRL 
     */
    private void addMultiSkin()
    {
        ResourceLocation rl = this.current;

        if (rl == null && !this.text.field.getText().isEmpty())
        {
            rl = RLUtils.create(this.text.field.getText());
        }

        this.multiList.add(rl);
        this.multiList.current = this.multiList.getList().indexOf(rl);

        if (this.multiList.current >= 0)
        {
            this.displayCurrent(this.multiList.getCurrent());
        }
    }

    /**
     * Remove currently selected {@link ResourceLocation} from multiRL 
     */
    private void removeMultiSkin()
    {
        if (this.multiList.current >= 0 && this.multiList.getList().size() > 1)
        {
            this.multiList.getList().remove(this.multiList.current);
            this.multiList.update();
            this.multiList.current--;

            if (this.multiList.current >= 0)
            {
                this.displayCurrent(this.multiList.getCurrent());
            }
        }
    }

    /**
     * Display current resource location (it's just for visual, not 
     * logic)
     */
    protected void displayCurrent(ResourceLocation rl)
    {
        this.current = rl;
        this.text.setText(rl == null ? "" : rl.toString());
        this.text.field.setCursorPositionZero();
    }

    /**
     * Select current resource location
     */
    protected void selectCurrent(ResourceLocation rl)
    {
        this.current = rl;

        if (this.multiRL != null)
        {
            if (this.multiList.current != -1 && rl != null)
            {
                this.multiList.getList().set(this.multiList.current, rl);
            }

            rl = this.multiRL;
        }

        if (this.callback != null)
        {
            this.callback.accept(rl);
        }
    }

    protected void toggleMultiSkin()
    {
        if (this.multiRL != null)
        {
            this.setMultiSkin(this.multiRL.children.get(0), true);
        }
        else if (this.current != null)
        {
            this.setMultiSkin(new MultiResourceLocation(this.current.toString()), true);
        }
        else
        {
            ResourceLocation rl = this.picker.getCurrentResource();

            if (rl != null)
            {
                this.setMultiSkin(rl, true);
            }
        }
    }

    protected void setMultiSkin(ResourceLocation skin, boolean notify)
    {
        boolean show = skin instanceof MultiResourceLocation;

        if (show)
        {
            this.multiRL = (MultiResourceLocation) skin;
            this.current = this.multiRL.children.get(0);

            this.multiList.current = this.multiRL.children.isEmpty() ? -1 : 0;
            this.multiList.setList(this.multiRL.children);
            this.multiList.update();

            this.picker.resizer().set(115, 30, 0, 0).parent(this.area).w(1, -120).h(1, -30);
            this.multi.resizer().set(5, 5, 60, 20).parent(this.area);
        }
        else
        {
            this.multiRL = null;

            this.picker.resizer().set(5, 30, 0, 0).parent(this.area).w(1, -10).h(1, -30);
            this.multi.resizer().set(5, 5, 100, 20).parent(this.area);

            this.displayCurrent(skin);
        }

        if (notify)
        {
            this.selectCurrent(skin);
        }

        this.multiList.setVisible(show);
        this.add.setVisible(show);
        this.remove.setVisible(show);

        GuiScreen screen = this.mc.currentScreen;

        this.picker.resize(screen.width, screen.height);
        this.multi.resize(screen.width, screen.height);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        /* Necessary measure to avoid triggering buttons when you press 
         * on a text field, for example */
        return super.mouseClicked(mouseX, mouseY, mouseButton) || (this.isVisible() && this.area.isInside(mouseX, mouseY));
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        drawGradientRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000, 0xff000000);

        if (this.multiList.isVisible())
        {
            this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
            GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 32, 110, this.area.h, 32, 32, 0, 0);
            drawRect(this.area.x, this.area.y, this.area.x + 110, this.area.y + 30, 0x44000000);
            drawGradientRect(this.area.x, this.area.getY(1) - 20, this.area.x + 110, this.area.getY(1), 0x00, 0x44000000);
        }

        if (this.picker.getList().isEmpty())
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.no_data"), this.area.getX(0.5F), this.area.getY(0.5F), 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        ResourceLocation loc = this.current;

        /* Draw preview */
        if (loc != null)
        {
            this.mc.renderEngine.bindTexture(loc);

            int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

            int x = this.area.getX(1);
            int y = this.area.getY(1);
            int fw = w;
            int fh = h;

            if (fw > 128 || fh > 128)
            {
                fw = fh = 128;

                if (w > h)
                {
                    fh = (int) ((h / (float) w) * fw);
                }
                else if (h > w)
                {
                    fw = (int) ((w / (float) h) * fh);
                }
            }

            x -= fw + 10;
            y -= fh + 10;

            this.mc.renderEngine.bindTexture(GuiDashboard.ICONS);
            GuiUtils.drawContinuousTexturedBox(x, y, 0, 96, fw, fh, 32, 32, 0, 0);
            this.mc.renderEngine.bindTexture(loc);

            GlStateManager.enableAlpha();
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(x, y + fh, 0.0D).tex(0, 1).endVertex();
            vertexbuffer.pos(x + fw, y + fh, 0.0D).tex(1, 1).endVertex();
            vertexbuffer.pos(x + fw, y, 0.0D).tex(1, 0).endVertex();
            vertexbuffer.pos(x, y, 0.0D).tex(0, 0).endVertex();
            tessellator.draw();
        }
    }
}