package noname.blockbuster.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraAttributes;

public class GuiCamera extends GuiScreen
{
    protected GuiSlider speed;
    protected GuiSlider accelerationRate;
    protected GuiSlider accelerationMax;
    protected GuiButton canFly;
    protected GuiButton done;

    private CameraEntity camera;

    public GuiCamera(CameraEntity entity)
    {
        this.camera = entity;
    }

    @Override
    public void initGui()
    {
        int x = this.width / 2 - 150;

        this.speed = new GuiSlider(0, x, 50, 140, 20, "Speed: ", "", 0, 1, 0, true, true);
        this.accelerationRate = new GuiSlider(1, x, 80, 140, 20, "Acceleration rate: ", "", 0, 0.5, 0, true, true);
        this.accelerationMax = new GuiSlider(2, x + 160, 50, 140, 20, "Acceleration max: ", "", 0, 2, 0, true, true);

        this.speed.precision = this.accelerationMax.precision = 1;
        this.accelerationRate.precision = 3;

        this.speed.setValue(this.camera.speed);
        this.accelerationRate.setValue(this.camera.accelerationRate);
        this.accelerationMax.setValue(this.camera.accelerationMax);

        this.speed.updateSlider();
        this.accelerationRate.updateSlider();
        this.accelerationMax.updateSlider();

        this.buttonList.clear();
        this.buttonList.add(this.canFly = new GuiButton(3, x + 160, 80, 140, 20, "Can fly"));
        this.buttonList.add(this.done = new GuiButton(4, x, 150, 300, 20, "Done"));

        this.canFly.displayString = this.camera.canFly ? "Can fly" : "Can't fly";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 3:
                this.updateFlyButton();
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
        boolean cCanFly = this.canFly.displayString.equals("Can fly");

        Dispatcher.getInstance().sendToServer(new PacketCameraAttributes(id, cSpeed, cRate, cMax, cCanFly));

        this.mc.displayGuiScreen(null);
    }

    private void updateFlyButton()
    {
        if (this.canFly.displayString == "Can fly")
        {
            this.canFly.displayString = "Can't fly";
        }
        else
        {
            this.canFly.displayString = "Can fly";
        }
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
        this.drawCenteredString(this.fontRendererObj, "Camera's configuration", this.width / 2, 25, 0xffffffff);

        this.speed.drawButton(this.mc, mouseX, mouseY);
        this.accelerationRate.drawButton(this.mc, mouseX, mouseY);
        this.accelerationMax.drawButton(this.mc, mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
