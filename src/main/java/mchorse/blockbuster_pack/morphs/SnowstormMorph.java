package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.particles.BedrockLibrary;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.math.IValue;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SnowstormMorph extends AbstractMorph
{
	@SideOnly(Side.CLIENT)
	private static Matrix4f matrix;

	@SideOnly(Side.CLIENT)
	private static Vector4f vector;

	public String scheme = "";
	public Map<String, String> variables = new HashMap<String, String>();

	private BedrockEmitter emitter;
	public List<BedrockEmitter> lastEmitters;

	private long lastUpdate;

	public static Matrix4f getMatrix()
	{
		if (matrix == null)
		{
			matrix = new Matrix4f();
		}

		return matrix;
	}

	public static Vector4f getVector()
	{
		if (vector == null)
		{
			vector = new Vector4f();
		}

		return vector;
	}

	@SideOnly(Side.CLIENT)
	public static Vector4f calculateGlobal(Matrix4f matrix, EntityLivingBase entity, float x, float y, float z, float partial)
	{
		Vector4f vector4f = getVector();

		vector4f.set(x, y, z, 1);
		matrix.transform(vector4f);
		vector4f.add(new Vector4f(
			(float) Interpolations.lerp(entity.prevPosX, entity.posX, partial),
			(float) Interpolations.lerp(entity.prevPosY, entity.posY, partial),
			(float) Interpolations.lerp(entity.prevPosZ, entity.posZ, partial),
			(float) 0
		));

		return vector4f;
	}

	public SnowstormMorph()
	{
		super();
		this.name = "snowstorm";
	}

	public void replaceVariable(String name, String expression)
	{
		this.variables.put(name, expression);
		this.emitter.parseVariable(name, expression);
	}

	public BedrockEmitter getEmitter()
	{
		if (this.emitter == null)
		{
			this.setClientScheme(this.scheme);
		}

		return this.emitter;
	}

	public List<BedrockEmitter> getLastEmitters()
	{
		if (this.lastEmitters == null)
		{
			this.lastEmitters = new ArrayList<BedrockEmitter>();
		}

		return this.lastEmitters;
	}

	public void setScheme(String key)
	{
		this.scheme = key;

		if (this.emitter != null)
		{
			this.setClientScheme(key);
		}
	}

	private void setClientScheme(String key)
	{
		if (this.emitter != null)
		{
			this.getEmitter().running = false;
			this.getLastEmitters().add(this.getEmitter());
		}

		this.emitter = new BedrockEmitter();
		this.emitter.setScheme(this.getScheme(key));
		this.emitter.parseVariables(this.variables);
	}

	private BedrockScheme getScheme(String key)
	{
		return Blockbuster.proxy.particles.presets.get(key);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getSubclassDisplayName()
	{
		return this.getEmitter().scheme != null ? this.getEmitter().scheme.identifier : this.name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderOnScreen(EntityPlayer entityPlayer, int x, int y, float scale, float alpha)
	{
		this.getEmitter().renderOnScreen(x, y, scale);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(EntityLivingBase target, double x, double y, double z, float yaw, float partialTicks)
	{
		if (GuiModelRenderer.isRendering())
		{
			return;
		}

		if (this.emitter != null && this.emitter.scheme != null && this.lastUpdate < BedrockLibrary.lastUpdate)
		{
			this.lastUpdate = BedrockLibrary.lastUpdate;

			if (this.emitter.scheme != this.getScheme(this.scheme))
			{
				this.setClientScheme(this.scheme);
			}
		}

		BedrockEmitter emitter = this.getEmitter();

		if (MatrixUtils.matrix != null)
		{
			Matrix4f parent = new Matrix4f(MatrixUtils.matrix);
			Matrix4f matrix4f = MatrixUtils.readModelView(getMatrix());

			parent.invert();
			parent.mul(matrix4f);

			Vector4f zero = calculateGlobal(parent, target, 0, 0, 0, partialTicks);

			emitter.lastGlobal.x = zero.x;
			emitter.lastGlobal.y = zero.y;
			emitter.lastGlobal.z = zero.z;
			emitter.rotation.setIdentity();

			Vector3f ax = new Vector3f(parent.m00, parent.m01, parent.m02);
			Vector3f ay = new Vector3f(parent.m10, parent.m11, parent.m12);
			Vector3f az = new Vector3f(parent.m20, parent.m21, parent.m22);

			ax.normalize();
			ay.normalize();
			az.normalize();

			emitter.rotation.setRow(0, ax);
			emitter.rotation.setRow(1, ay);
			emitter.rotation.setRow(2, az);

			Iterator<BedrockEmitter> it = this.getLastEmitters().iterator();

			while (it.hasNext())
			{
				BedrockEmitter last = it.next();

				if (!last.added)
				{
					it.remove();

					continue;
				}

				last.lastGlobal.set(emitter.lastGlobal);
				last.rotation.set(emitter.rotation);
			}
		}
		else
		{
			emitter.lastGlobal.x = Interpolations.lerp(target.prevPosX, target.posX, partialTicks);
			emitter.lastGlobal.y = Interpolations.lerp(target.prevPosY, target.posY, partialTicks);
			emitter.lastGlobal.z = Interpolations.lerp(target.prevPosZ, target.posZ, partialTicks);
			emitter.rotation.setIdentity();

			Iterator<BedrockEmitter> it = this.getLastEmitters().iterator();

			while (it.hasNext())
			{
				BedrockEmitter last = it.next();

				if (!last.added)
				{
					it.remove();

					continue;
				}

				last.lastGlobal.set(emitter.lastGlobal);
				last.rotation.set(emitter.rotation);
			}
		}

		RenderingHandler.addEmitter(emitter, target);
	}

	@Override
	public void update(EntityLivingBase target)
	{
		super.update(target);

		if (target.world.isRemote)
		{
			this.updateClient();
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateClient()
	{
		this.getEmitter().sanityTicks = 0;

		for (BedrockEmitter emitter : this.getLastEmitters())
		{
			emitter.sanityTicks = 0;
		}
	}

	@Override
	public AbstractMorph create()
	{
		return new SnowstormMorph();
	}

	@Override
	public void copy(AbstractMorph from)
	{
		super.copy(from);

		if (from instanceof SnowstormMorph)
		{
			SnowstormMorph morph = (SnowstormMorph) from;

			this.setScheme(morph.scheme);
			this.variables.putAll(morph.variables);
		}
	}

	@Override
	public float getWidth(EntityLivingBase entityLivingBase)
	{
		return 0.6F;
	}

	@Override
	public float getHeight(EntityLivingBase entityLivingBase)
	{
		return 1.8F;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean result = super.equals(obj);

		if (obj instanceof SnowstormMorph)
		{
			SnowstormMorph morph = (SnowstormMorph) obj;

			result = result && Objects.equals(this.scheme, morph.scheme);
			result = result && Objects.equals(this.variables, morph.variables);
		}

		return result;
	}

	@Override
	public boolean canMerge(AbstractMorph morph)
	{
		if (morph instanceof SnowstormMorph)
		{
			SnowstormMorph snow = (SnowstormMorph) morph;

			if (!this.scheme.equals(snow.scheme) && this.emitter != null)
			{
				this.setScheme(snow.scheme);
			}

			return true;
		}

		return super.canMerge(morph);
	}

	@Override
	public void reset()
	{
		super.reset();

		this.scheme = "";
		this.variables.clear();
	}

	@Override
	public void fromNBT(NBTTagCompound tag)
	{
		super.fromNBT(tag);

		if (tag.hasKey("Scheme"))
		{
			this.setScheme(tag.getString("Scheme"));
		}

		if (tag.hasKey("Vars"))
		{
			NBTTagCompound vars = tag.getCompoundTag("Vars");

			for (String key : vars.getKeySet())
			{
				this.variables.put(key, vars.getString(key));
			}
		}
	}

	@Override
	public void toNBT(NBTTagCompound tag)
	{
		super.toNBT(tag);

		tag.setString("Scheme", this.scheme);

		if (!this.variables.isEmpty())
		{
			NBTTagCompound vars = new NBTTagCompound();

			for (Map.Entry<String, String> entry : this.variables.entrySet())
			{
				vars.setString(entry.getKey(), entry.getValue());
			}

			tag.setTag("Vars", vars);
		}
	}
}