package noname.blockbuster.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiSlider;
import noname.blockbuster.client.gui.elements.GuiToggle;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraAttributes;

/**
 * Camera configuration GUI
 *
 * This GUI is invoked player.openGui and has an id of 0. The code below is
 * pretty easy to understand, so no comments are needed.
 *
 * For more information, see vanilla GUI screens.
 */
public class GuiCamera extends GuiScreen
{
    private String title = I18n.format("blockbuster.gui.camera.title");

    protected GuiSlider speed;
    protected GuiSlider accelerationRate;
    protected GuiSlider accelerationMax;
    protected GuiToggle canFly;
    protected GuiButton done;

    private EntityCamera camera;

    public GuiCamera(EntityCamera entity)
    {
        this.camera = entity;
    }

    @Override
    public void initGui()
    {
        int w = 200;
        int x = this.width / 2 - w / 2;

        this.speed = new GuiSlider(0, x, 50, w, 20, I18n.format("blockbuster.gui.camera.speed"), "", 0, 1, 0, true, true);
        this.accelerationRate = new GuiSlider(1, x, 80, w, 20, I18n.format("blockbuster.gui.camera.rate"), "", 0, 0.5, 0, true, true);
        this.accelerationMax = new GuiSlider(2, x, 110, w, 20, I18n.format("blockbuster.gui.camera.max"), "", 0, 2, 0, true, true);

        this.speed.precision = this.accelerationMax.precision = 1;
        this.accelerationRate.precision = 3;

        this.speed.setValue(this.camera.speed);
        this.accelerationRate.setValue(this.camera.accelerationRate);
        this.accelerationMax.setValue(this.camera.accelerationMax);

        this.speed.updateSlider();
        this.accelerationRate.updateSlider();
        this.accelerationMax.updateSlider();

        this.buttonList.clear();
        this.buttonList.add(this.canFly = new GuiToggle(3, x, 140, w, 20, I18n.format("blockbuster.gui.camera.canFly"), I18n.format("blockbuster.gui.camera.cantFly")));
        this.buttonList.add(this.done = new GuiButton(4, x, 205, w, 20, I18n.format("blockbuster.gui.done")));

        this.canFly.setValue(this.camera.canFly);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 3:
                this.canFly.toggle();
                break;
            case 4:
                this.saveAndExit();
                break;
        }
    }

    private void saveAndExit()
    {
        int id = this.camera.getEntityId();
        float cSpeed = (float) this.speed.getValue();
        float cRate = (float) this.accelerationRate.getValue();
        float cMax = (float) this.accelerationMax.getValue();
        boolean cCanFly = this.canFly.getValue();

        Dispatcher.getInstance().sendToServer(new PacketCameraAttributes(id, cSpeed, cRate, cMax, cCanFly));

        this.mc.displayGuiScreen(null);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.speed.mousePressed(this.mc, mouseX, mouseY);
        this.accelerationRate.mousePressed(this.mc, mouseX, mouseY);
        this.accelerationMax.mousePressed(this.mc, mouseX, mouseY);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        this.speed.mouseReleased(mouseX, mouseY);
        this.accelerationRate.mouseReleased(mouseX, mouseY);
        this.accelerationMax.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 25, 0xffffffff);

        this.speed.drawButton(this.mc, mouseX, mouseY);
        this.accelerationRate.drawButton(this.mc, mouseX, mouseY);
        this.accelerationMax.drawButton(this.mc, mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
