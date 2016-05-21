package com.noname.blockbuster;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Blockbuster's main entry
 * 
 * This mod allows you to create machinimas in minecraft. Blockbuster provides you
 * with the most needed tools to create machinimas alone (with bunch of complaining
 * actors).
 */
@Mod(modid = BlockbusterMod.MODID, name=BlockbusterMod.MODNAME, version = BlockbusterMod.VERSION)
public class BlockbusterMod
{
    public static final String MODID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "1.0";
    
    public static Item camera;
    
    public static final CreativeTabs busterTab = new CreativeTabs("blockbusterTab") 
	{
		@Override
		public Item getTabIconItem() 
		{
			return BlockbusterMod.camera;
		} 
	};
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	int ID = 0;
    	
    	// Register camera, actors and props eggs
    	camera = new CameraItem().setUnlocalizedName("cameraItem")
    							 .setRegistryName("cameraItem")
    							 .setCreativeTab(busterTab);
    	
    	GameRegistry.register(camera);
    	ModelLoader.setCustomModelResourceLocation(camera, 0, new ModelResourceLocation("blockbuster:cameraItem", "inventory"));
    	
    	registerEntity(CameraEntity.class, "Camera", ID++);
    	RenderingRegistry.registerEntityRenderingHandler(CameraEntity.class, new CameraRender.CameraFactory());
    }
    
    /**
     * Thanks to animal bikes mod for this wonderful example!
     * Kids, wanna learn how to mod minecraft? That's simple. Find mods for specific minecraft version
     * and decompile the .jar files with JD-GUI. Isn't that simple?
     */
    private void registerEntity(Class entity, String name, int id)
    {
    	EntityList.classToStringMapping.put(entity, name);
    	EntityRegistry.registerModEntity(entity, name, id, this, 40, 1, false);
    }
}