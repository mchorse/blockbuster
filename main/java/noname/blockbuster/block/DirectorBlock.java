package noname.blockbuster.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import noname.blockbuster.Blockbuster;

public class DirectorBlock extends Block
{
	public DirectorBlock()
	{
		super(Material.rock);
		setCreativeTab(Blockbuster.busterTab);
		setRegistryName("directorBlock");
		setUnlocalizedName("directorBlock");
	}
}
