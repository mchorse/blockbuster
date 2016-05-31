package noname.blockbuster;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import noname.blockbuster.block.DirectorBlock;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.item.CameraConfigItem;
import noname.blockbuster.item.CameraItem;
import noname.blockbuster.item.RecordItem;
import noname.blockbuster.item.RegisterItem;
import noname.blockbuster.recording.MocapEventHandler;

public class CommonProxy implements IGuiHandler
{
	protected static int ID = 0;
	
	public void preLoad() 
	{
    	registerItem(Blockbuster.cameraItem = new CameraItem());
    	registerItem(Blockbuster.cameraConfigItem = new CameraConfigItem());
    	registerItem(Blockbuster.registerItem = new RegisterItem());
    	registerItem(Blockbuster.recordItem = new RecordItem());
    	
    	registerBlock(Blockbuster.directorBlock = new DirectorBlock());
    	
    	registerEntity(CameraEntity.class, "Camera");
    	registerEntity(ActorEntity.class, "Actor");
	}
	
	public void load()
	{
		MinecraftForge.EVENT_BUS.register(new MocapEventHandler());
	}
	
	/**
     * Register an item with Forge's game registry
     */
    protected void registerItem(Item item)
    {
    	GameRegistry.register(item);
    }
    
    /**
     * Register block (and also add register an item for the block)
     */
    protected void registerBlock(Block block)
    {
    	GameRegistry.register(block);
    	GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }
    
    /**
     * Thanks to animal bikes mod for this wonderful example!
     * Kids, wanna learn how to mod minecraft with forge? That's simple. Find mods for specific minecraft version
     * and decompile the .jar files with JD-GUI. Isn't that simple?
     */
    protected void registerEntity(Class entity, String name)
    {
    	EntityRegistry.registerModEntity(entity, name, ID++, Blockbuster.instance, 40, 4, true);
    }
    
    /* IGuiHandler implementation */
    
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}
