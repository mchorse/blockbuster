package noname.blockbuster.client.gui;

import java.io.IOException;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.networking.CameraAttributesUpdate;

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
		camera = entity;
	}

	@Override
	public void initGui()
	{
		int x = width/2 - 150;
		
		speed = new GuiSlider(0, x, 50, 140, 20, "Speed: ", "", 0, 1, 0, true, true);
		accelerationRate = new GuiSlider(1, x, 80, 140, 20, "Acceleration rate: ", "", 0, 0.5, 0, true, true);
		accelerationMax = new GuiSlider(2, x + 160, 50, 140, 20, "Acceleration max: ", "", 0, 2, 0, true, true);
		
		speed.precision = accelerationMax.precision = 1;
		accelerationRate.precision = 3;
		
		speed.setValue(camera.speed);
		accelerationRate.setValue(camera.accelerationRate);
		accelerationMax.setValue(camera.accelerationMax);
		
		speed.updateSlider();
		accelerationRate.updateSlider();
		accelerationMax.updateSlider();
		
		buttonList.clear();
		buttonList.add(canFly = new GuiButton(3, x + 160, 80, 140, 20, "Can fly"));
		buttonList.add(done = new GuiButton(4, x, 150, 300, 20, "Done"));
		
		canFly.displayString = camera.canFly ? "Can fly" : "Can't fly";
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		switch (button.id)
		{
			case 3: updateFlyButton(); break;
			case 4: saveAndExit(); break;
		}
	}
	
	private void saveAndExit()
	{
		float cSpeed = (float)speed.getValue();
		float cRate = (float)accelerationRate.getValue();
		float cMax = (float)accelerationMax.getValue();
		boolean cCanFly = canFly.displayString == "Can fly";
		
		Blockbuster.channel.sendToServer(new CameraAttributesUpdate(camera.getEntityId(), cSpeed, cRate, cMax, cCanFly));
		
		mc.displayGuiScreen(null);
	}
	
	private void updateFlyButton()
	{
		if (canFly.displayString == "Can fly")
		{
			canFly.displayString = "Can't fly";
		}
		else 
		{
			canFly.displayString = "Can fly";
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		speed.mousePressed(mc, mouseX, mouseY);
		accelerationRate.mousePressed(mc, mouseX, mouseY);
		accelerationMax.mousePressed(mc, mouseX, mouseY);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
		
		speed.mouseReleased(mouseX, mouseY);
		accelerationRate.mouseReleased(mouseX, mouseY);
		accelerationMax.mouseReleased(mouseX, mouseY);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Camera's configuration", width/2, 25, 0xffffffff);
		
		speed.drawButton(mc, mouseX, mouseY);
		accelerationRate.drawButton(mc, mouseX, mouseY);
		accelerationMax.drawButton(mc, mouseX, mouseY);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
