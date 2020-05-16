package mchorse.blockbuster.client.particles.components.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class BedrockComponentShapeDisc extends BedrockComponentShapeSphere
{
	public MolangExpression[] normal = {MolangParser.ZERO, MolangParser.ONE, MolangParser.ZERO};

	@Override
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("plane_normal"))
		{
			JsonArray array = element.getAsJsonArray("plane_normal");

			if (array.size() >= 3)
			{
				this.normal[0] = parser.parseJson(array.get(0));
				this.normal[1] = parser.parseJson(array.get(1));
				this.normal[2] = parser.parseJson(array.get(2));
			}
		}

		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = (JsonObject) super.toJson();
		JsonArray array = new JsonArray();

		for (MolangExpression expression : this.normal)
		{
			array.add(expression.toJson());
		}

		object.add("plane_normal", array);

		return object;
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		float centerX = (float) this.offset[0].get();
		float centerY = (float) this.offset[1].get();
		float centerZ = (float) this.offset[2].get();

		Vector3f normal = new Vector3f((float) this.normal[0].get(), (float) this.normal[1].get(), (float) this.normal[2].get());

		normal.normalize();

		Quat4f quaternion = new Quat4f(normal.x, normal.y, normal.z, 1);
		Matrix4f rotation = new Matrix4f();
		rotation.set(quaternion);

		Vector4f position = new Vector4f((float) Math.random() - 0.5F, 0, (float) Math.random() - 0.5F, 0);
		position.normalize();
		rotation.transform(position);

		position.scale((float) (this.radius.get() * (this.surface ? 1 : Math.random())));
		position.add(new Vector4f(centerX, centerY, centerZ, 0));

		particle.position.x += position.x;
		particle.position.y += position.y;
		particle.position.z += position.z;

		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}