package mchorse.blockbuster.client.particles.components.expiration;

import com.google.gson.JsonElement;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

public abstract class BedrockComponentExpireBlocks extends BedrockComponentBase
{
	public List<Block> blocks = new ArrayList<Block>();

	private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

	@Override
	public BedrockComponentBase fromJson(JsonElement element, MolangParser parser) throws MolangException
	{
		if (element.isJsonArray())
		{
			for (JsonElement value : element.getAsJsonArray())
			{
				ResourceLocation location = new ResourceLocation(value.getAsString());
				Block block = ForgeRegistries.BLOCKS.getValue(location);

				if (block != null)
				{
					this.blocks.add(block);
				}
			}
		}

		return super.fromJson(element, parser);
	}

	public Block getBlock(BedrockEmitter emitter, BedrockParticle particle)
	{
		Vector3d position = particle.getGlobalPosition(emitter);

		this.pos.setPos(position.getX(), position.getY(), position.getZ());

		return emitter.world.getBlockState(this.pos).getBlock();
	}
}