package mchorse.blockbuster.client.particles.components.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleUpdate;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.math.Operation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Vector3d;
import java.util.List;

public class BedrockComponentMotionCollision extends BedrockComponentBase implements IComponentParticleUpdate
{
	public MolangExpression enabled = MolangParser.ONE;
	public float collissionDrag = 0;
	public float bounciness = 1;
	public float radius = 0.01F;
	public boolean expireOnImpact;

	/* Runtime options */
	public boolean json;
	private Vector3d previous = new Vector3d();
	private Vector3d current = new Vector3d();
	private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("enabled")) this.enabled = parser.parseJson(element.get("enabled"));
		if (element.has("collision_drag")) this.collissionDrag = element.get("collision_drag").getAsFloat();
		if (element.has("coefficient_of_restitution")) this.bounciness = element.get("coefficient_of_restitution").getAsFloat();
		if (element.has("collision_radius")) this.radius = element.get("collision_radius").getAsFloat();
		if (element.has("expire_on_contact")) this.expireOnImpact = element.get("expire_on_contact").getAsBoolean();

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();

		if (MolangExpression.isZero(this.enabled))
		{
			return object;
		}

		if (!MolangExpression.isOne(this.enabled)) object.add("enabled", this.enabled.toJson());
		if (this.collissionDrag != 0) object.addProperty("collision_drag", this.collissionDrag);
		if (this.bounciness != 1) object.addProperty("coefficient_of_restitution", this.bounciness);
		if (this.radius != 0.01F) object.addProperty("collision_radius", this.radius);
		if (this.expireOnImpact) object.addProperty("expire_on_contact", true);

		return object;
	}

	@Override
	public void update(BedrockEmitter emitter, BedrockParticle particle)
	{
		if (emitter.world == null)
		{
			return;
		}

		if (!particle.manual && !Operation.equals(this.enabled.get(), 0))
		{
			float r = this.radius;

			this.previous.set(particle.getGlobalPosition(emitter, particle.prevPosition));
			this.current.set(particle.getGlobalPosition(emitter));

			Vector3d prev = this.previous;
			Vector3d now = this.current;

			double x = now.x - prev.x;
			double y = now.y - prev.y;
			double z = now.z - prev.z;
			boolean veryBig = Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10;

			this.pos.setPos(now.x, now.y, now.z);

			if (veryBig || !emitter.world.isBlockLoaded(this.pos))
			{
				return;
			}

			AxisAlignedBB aabb = new AxisAlignedBB(prev.x - r, prev.y - r, prev.z - r, prev.x + r, prev.y + r, prev.z + r);

			double d0 = y;
			double origX = x;
			double origZ = z;

			List<AxisAlignedBB> list = emitter.world.getCollisionBoxes(null, aabb.expand(x, y, z));

			for (AxisAlignedBB axisalignedbb : list)
			{
				y = axisalignedbb.calculateYOffset(aabb, y);
			}

			aabb = aabb.offset(0.0D, y, 0.0D);

			for (AxisAlignedBB axisalignedbb1 : list)
			{
				x = axisalignedbb1.calculateXOffset(aabb, x);
			}

			aabb = aabb.offset(x, 0.0D, 0.0D);

			for (AxisAlignedBB axisalignedbb2 : list)
			{
				z = axisalignedbb2.calculateZOffset(aabb, z);
			}

			aabb = aabb.offset(0.0D, 0.0D, z);

			if (d0 != y || origX != x || origZ != z)
			{
				if (this.expireOnImpact)
				{
					particle.dead = true;

					return;
				}

				if (particle.relativePosition)
				{
					particle.relativePosition = false;
					particle.prevPosition.set(prev);
				}

				now.set(aabb.minX + r, aabb.minY + r, aabb.minZ + r);

				if (d0 != y)
				{
					particle.accelerationFactor.y *= -this.bounciness;
					now.y += d0 < y ? r : -r;
				}

				if (origX != x)
				{
					particle.accelerationFactor.x *= -this.bounciness;
					now.x += origX < x ? r : -r;
				}

				if (origZ != z)
				{
					particle.accelerationFactor.z *= -this.bounciness;
					now.z += origZ < z ? r : -r;
				}

				particle.position.set(now);
				particle.dragFactor += this.collissionDrag;
			}
		}
	}

	@Override
	public int getSortingIndex()
	{
		return 50;
	}
}