package mchorse.blockbuster.client.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;

public class BedrockComponentLocalSpace extends BedrockComponentBase implements IComponentParticleInitialize
{
	public boolean position;
	public boolean rotation;
	public boolean direction;
	public boolean acceleration;
	public boolean gravity;
	
	public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
	{
		if (!elem.isJsonObject()) return super.fromJson(elem, parser);

		JsonObject element = elem.getAsJsonObject();

		if (element.has("position")) this.position = element.get("position").getAsBoolean();
		if (element.has("rotation")) this.rotation = element.get("rotation").getAsBoolean();
		if (element.has("direction")) this.direction = element.get("direction").getAsBoolean();
		if (element.has("acceleration")) this.acceleration = element.get("acceleration").getAsBoolean();
		if (element.has("gravity")) this.gravity = element.get("gravity").getAsBoolean();
		
		return super.fromJson(element, parser);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject object = new JsonObject();

		if (this.position) object.addProperty("position", true);
		if (this.rotation) object.addProperty("rotation", true);
		if (this.direction) object.addProperty("direction", true);
		if (this.acceleration) object.addProperty("acceleration", true);
		if (this.gravity) object.addProperty("gravity", true);
		
		return object;
	}

	@Override
	public void apply(BedrockEmitter emitter, BedrockParticle particle)
	{
		particle.relativePosition = this.position;
		particle.relativeRotation = this.rotation;
		particle.relativeDirection = this.direction;
		particle.relativeAcceleration = this.acceleration;
		particle.gravity = this.gravity;
		
		particle.setupMatrix(emitter);
	}

	@Override
	public int getSortingIndex()
	{
		return 1000;
	}
}
