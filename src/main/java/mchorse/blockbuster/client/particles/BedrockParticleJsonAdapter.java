package mchorse.blockbuster.client.particles;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireInBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireNotInBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentLifetime;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceBillboard;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceTinting;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceLighting;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeExpression;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeLooping;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeOnce;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentInitialization;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentLocalSpace;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentInitialSpeed;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentInitialSpin;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentMotionCollision;
import mchorse.blockbuster.client.particles.components.motion.BedrockComponentMotionDynamic;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRateInstant;
import mchorse.blockbuster.client.particles.components.rate.BedrockComponentRateSteady;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeBox;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeDisc;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeEntityAABB;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapePoint;
import mchorse.blockbuster.client.particles.components.shape.BedrockComponentShapeSphere;
import mchorse.blockbuster.client.particles.molang.Molang;
import mchorse.blockbuster.client.particles.molang.MolangExpression;
import mchorse.mclib.utils.resources.RLUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BedrockParticleJsonAdapter implements JsonDeserializer<BedrockParticle>
{
	public Map<String, Function<JsonElement, BedrockComponentBase>> components = new HashMap<String, Function<JsonElement, BedrockComponentBase>>();

	public BedrockParticleJsonAdapter()
	{
		/* Meta components */
		this.components.put("minecraft:emitter_local_space", (element) -> new BedrockComponentLocalSpace().fromJson(element));
		this.components.put("minecraft:emitter_initialization", (element) -> new BedrockComponentInitialization().fromJson(element));

		/* Rate */
		this.components.put("minecraft:emitter_rate_instant", (element) -> new BedrockComponentRateInstant().fromJson(element));
		this.components.put("minecraft:emitter_rate_steady", (element) -> new BedrockComponentRateSteady().fromJson(element));

		/* Lifetime emitter */
		this.components.put("minecraft:emitter_lifetime_looping", (element) -> new BedrockComponentLifetimeLooping().fromJson(element));
		this.components.put("minecraft:emitter_lifetime_once", (element) -> new BedrockComponentLifetimeOnce().fromJson(element));
		this.components.put("minecraft:emitter_lifetime_expression", (element) -> new BedrockComponentLifetimeExpression().fromJson(element));

		/* Shapes */
		this.components.put("minecraft:emitter_shape_disc", (element) -> new BedrockComponentShapeDisc().fromJson(element));
		this.components.put("minecraft:emitter_shape_box", (element) -> new BedrockComponentShapeBox().fromJson(element));
		this.components.put("minecraft:emitter_shape_entity_aabb", (element) -> new BedrockComponentShapeEntityAABB().fromJson(element));
		this.components.put("minecraft:emitter_shape_point", (element) -> new BedrockComponentShapePoint().fromJson(element));
		this.components.put("minecraft:emitter_shape_sphere", (element) -> new BedrockComponentShapeSphere().fromJson(element));

		/* Lifetime particle */
		this.components.put("minecraft:particle_lifetime_expression", (element) -> new BedrockComponentLifetime().fromJson(element));
		this.components.put("minecraft:particle_expire_if_in_blocks", (element) -> new BedrockComponentExpireInBlocks().fromJson(element));
		this.components.put("minecraft:particle_expire_if_not_in_blocks", (element) -> new BedrockComponentExpireNotInBlocks().fromJson(element));

		/* Appearance */
		this.components.put("minecraft:particle_appearance_billboard", (element) -> new BedrockComponentAppearanceBillboard().fromJson(element));
		this.components.put("minecraft:particle_appearance_lighting", (element) -> new BedrockComponentAppearanceLighting());
		this.components.put("minecraft:particle_appearance_tinting", (element) -> new BedrockComponentAppearanceTinting().fromJson(element));

		/* Motion & Rotation */
		this.components.put("minecraft:particle_initial_speed", (element) -> new BedrockComponentInitialSpeed().fromJson(element));
		this.components.put("minecraft:particle_initial_spin", (element) -> new BedrockComponentInitialSpin().fromJson(element));
		this.components.put("minecraft:particle_motion_collision", (element) -> new BedrockComponentMotionCollision().fromJson(element));
		this.components.put("minecraft:particle_motion_dynamic", (element) -> new BedrockComponentMotionDynamic().fromJson(element));

		/* TODO:
		 * 8. minecraft:particle_motion_parametric
		 */
	}

	@Override
	public BedrockParticle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		BedrockParticle particle = new BedrockParticle();

		if (!json.isJsonObject())
		{
			throw new JsonParseException("The root element of Bedrock particle should be an object!");
		}

		/* Skip format_version check to avoid breaking semi-compatible particles */
		JsonObject root = json.getAsJsonObject();

		this.parseEffect(particle, this.getObject(root, "particle_effect", "No particle_effect was found..."));

		return particle;
	}

	private void parseEffect(BedrockParticle particle, JsonObject effect) throws JsonParseException
	{
		this.parseDescription(particle, this.getObject(effect, "description", "No particle_effect.description was found..."));

		if (effect.has("curves"))
		{
			JsonElement curves = effect.get("curves");

			if (curves.isJsonObject())
			{
				this.parseCurves(particle, curves.getAsJsonObject());
			}
		}

		this.parseComponents(particle, this.getObject(effect, "components", "No particle_effect.components was found..."));
	}

	/**
	 * Parse description object (which contains ID of the particle, material type and texture)
	 */
	private void parseDescription(BedrockParticle particle, JsonObject description) throws JsonParseException
	{
		if (description.has("identifier"))
		{
			particle.identifier = description.get("identifier").getAsString();
		}

		JsonObject parameters = this.getObject(description, "basic_render_parameters", "No particle_effect.basic_render_parameters was found...");

		if (parameters.has("material"))
		{
			particle.material = BedrockMaterial.fromString(parameters.get("material").getAsString());
		}

		if (parameters.has("texture"))
		{
			String texture = parameters.get("texture").getAsString();

			if (!texture.equals("textures/particle/particles"))
			{
				particle.texture = RLUtils.create(texture);
			}
		}
	}

	/**
	 * Parse curves object
	 */
	private void parseCurves(BedrockParticle particle, JsonObject curves)
	{
		for (Map.Entry<String, JsonElement> entry : curves.entrySet())
		{
			JsonElement element = entry.getValue();

			if (element.isJsonObject())
			{
				BedrockCurve curve = new BedrockCurve();
				JsonObject object = element.getAsJsonObject();

				if (object.has("type"))
				{
					curve.type = BedrockCurveType.fromString(object.get("type").getAsString());
				}

				if (object.has("input"))
				{
					curve.input = Molang.parse(object.get("input"));
				}

				if (object.has("horizontal_range"))
				{
					curve.range = Molang.parse(object.get("horizontal_range"));
				}

				if (object.has("nodes"))
				{
					JsonArray nodes = object.getAsJsonArray("nodes");
					MolangExpression[] result = new MolangExpression[nodes.size()];

					for (int i = 0, c = result.length; i < c; i ++)
					{
						result[i] = Molang.parse(nodes.get(i));
					}

					curve.nodes = result;
				}

				particle.curves.put(entry.getKey(), curve);
			}
		}
	}

	private void parseComponents(BedrockParticle particle, JsonObject components)
	{
		for (Map.Entry<String, JsonElement> entry : components.entrySet())
		{
			String key = entry.getKey();

			if (this.components.containsKey(key))
			{
				particle.components.add(this.components.get(key).apply(entry.getValue()));
			}
		}
	}

	private JsonObject getObject(JsonObject object, String key, String message) throws JsonParseException
	{
		/* Skip format_version check to avoid breaking semi-compatible particles */
		if (!object.has(key) && !object.get(key).isJsonObject())
		{
			throw new JsonParseException(message);
		}

		return object.get(key).getAsJsonObject();
	}
}