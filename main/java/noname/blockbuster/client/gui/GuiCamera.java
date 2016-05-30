package noname.blockbuster.client.gui;

import java.io.IOException;

import com.google.common.base.Predicate;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import noname.blockbuster.entity.CameraEntity;

public class GuiCamera extends GuiScreen
{
	private static final Predicate<String> validator = new Predicate<String>()
    {
        public boolean apply(String value)
        {
        	try 
        	{
        		Double.parseDouble(value); 
        	}
        	catch (Exception e) 
        	{
        		return false;
        	}
        	
        	return true;
        }
    };
	
	protected GuiTextField speed;
	protected GuiTextField accelerationRate;
	protected GuiTextField accelerationMax;
	protected GuiButton canFly;
	protected GuiButton done;
	
	@Override
	public void initGui()
	{
		int x = width/2 - 150;
		
		speed = new GuiTextField(0, fontRendererObj, x, 50, 300, 20);
		// speed.setText(Double.toString(camera.speed));
		speed.setValidator(validator);
		
		accelerationRate = new GuiTextField(1, fontRendererObj, x, 90, 300, 20);
		// accelerationRate.setText(Double.toString(camera.accelerationRate));
		accelerationRate.setValidator(validator);
		
		accelerationMax = new GuiTextField(2, fontRendererObj, x, 130, 300, 20);
		// accelerationMax.setText(Double.toString(camera.accelerationMax));
		accelerationMax.setValidator(validator);
		
		buttonList.clear();
		buttonList.add(canFly = new GuiButton(3, x, 180, 160, 20, "Can fly"));
		buttonList.add(done = new GuiButton(4, x + 180, 160, 140, 20, "Done"));
		
		// canFly.displayString = camera.canFly ? "Can fly" : "Can't fly";
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		switch (button.id)
		{
			case 3:
				updateFlyButton();
			break;
			
			case 4:
				mc.displayGuiScreen(null);
			break;
		}
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
		speed.mouseClicked(mouseX, mouseY, mouseButton);
		accelerationRate.mouseClicked(mouseX, mouseY, mouseButton);
		accelerationMax.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		speed.textboxKeyTyped(typedChar, keyCode);
		accelerationRate.textboxKeyTyped(typedChar, keyCode);
		accelerationMax.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		
		int font = fontRendererObj.FONT_HEIGHT;
		int x = width/2 - 150;
		
		drawCenteredString(fontRendererObj, "Camera's configuration", width/2, 25, 0xffffffff);
		
		drawString(fontRendererObj, "Camera speed", x, 50 - 4 - font, 0xffffffff);
		speed.drawTextBox();
		
		drawString(fontRendererObj, "Camera acceleration rate", x, 90 - 4 - font, 0xffffffff);
		accelerationRate.drawTextBox();
		
		drawString(fontRendererObj, "Camera max acceleration", x, 130 - 4 - font, 0xffffffff);
		accelerationMax.drawTextBox();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
