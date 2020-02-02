package mchorse.blockbuster.client.particles;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceBillboard;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceLighting;
import mchorse.blockbuster.client.particles.components.appearance.BedrockComponentAppearanceTinting;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireInBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentExpireNotInBlocks;
import mchorse.blockbuster.client.particles.components.expiration.BedrockComponentLifetime;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeExpression;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeLooping;
import mchorse.blockbuster.client.particles.components.lifetime.BedrockComponentLifetimeOnce;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentInitialization;
import mchorse.blockbuster.client.particles.components.meta.BedrockComponentLocalSpace;
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
import mchorse.blockbuster.client.particles.molang.MolangException;
import mchorse.blockbuster.client.particles.molang.MolangParser;
import mchorse.blockbuster.client.particles.molang.expressions.MolangExpression;
import mchorse.mclib.utils.resources.RLUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BedrockSchemeJsonAdapter implements JsonDeserializer<BedrockScheme>
{
	public Map<String, IComponentParser> components = new HashMap<String, IComponentParser>();

	public BedrockSchemeJsonAdapter()
	{
		/* Meta components */
		this.components.put("minecraft:emitter_local_space", (element, parser) -> new BedrockComponentLocalSpace().fromJson(element, parser));
		this.components.put("minecraft:emitter_initialization", (element, parser) -> new BedrockComponentInitialization().fromJson(element, parser));

		/* Rate */
		this.components.put("minecraft:emitter_rate_instant", (element, parser) -> new BedrockComponentRateInstant().fromJson(element, parser));
		this.components.put("minecraft:emitter_rate_steady", (element, parser) -> new BedrockComponentRateSteady().fromJson(element, parser));

		/* Lifetime emitter */
		this.components.put("minecraft:emitter_lifetime_looping", (element, parser) -> new BedrockComponentLifetimeLooping().fromJson(element, parser));
		this.components.put("minecraft:emitter_lifetime_once", (element, parser) -> new BedrockComponentLifetimeOnce().fromJson(element, parser));
		this.components.put("minecraft:emitter_lifetime_expression", (element, parser) -> new BedrockComponentLifetimeExpression().fromJson(element, parser));

		/* Shapes */
		this.components.put("minecraft:emitter_shape_disc", (element, parser) -> new BedrockComponentShapeDisc().fromJson(element, parser));
		this.components.put("minecraft:emitter_shape_box", (element, parser) -> new BedrockComponentShapeBox().fromJson(element, parser));
		this.components.put("minecraft:emitter_shape_entity_aabb", (element, parser) -> new BedrockComponentShapeEntityAABB().fromJson(element, parser));
		this.components.put("minecraft:emitter_shape_point", (element, parser) -> new BedrockComponentShapePoint().fromJson(element, parser));
		this.components.put("minecraft:emitter_shape_sphere", (element, parser) -> new BedrockComponentShapeSphere().fromJson(element, parser));

		/* Lifetime particle */
		this.components.put("minecraft:particle_lifetime_expression", (element, parser) -> new BedrockComponentLifetime().fromJson(element, parser));
		this.components.put("minecraft:particle_expire_if_in_blocks", (element, parser) -> new BedrockComponentExpireInBlocks().fromJson(element, parser));
		this.components.put("minecraft:particle_expire_if_not_in_blocks", (element, parser) -> new BedrockComponentExpireNotInBlocks().fromJson(element, parser));

		/* Appearance */
		this.components.put("minecraft:particle_appearance_billboard", (element, parser) -> new BedrockComponentAppearanceBillboard().fromJson(element, parser));
		this.components.put("minecraft:particle_appearance_lighting", (element, parser) -> new BedrockComponentAppearanceLighting());
		this.components.put("minecraft:particle_appearance_tinting", (element, parser) -> new BedrockComponentAppearanceTinting().fromJson(element, parser));

		/* Motion & Rotation */
		this.components.put("minecraft:particle_initial_speed", (element, parser) -> new BedrockComponentInitialSpeed().fromJson(element, parser));
		this.components.put("minecraft:particle_initial_spin", (element, parser) -> new BedrockComponentInitialSpin().fromJson(element, parser));
		this.components.put("minecraft:particle_motion_collision", (element, parser) -> new BedrockComponentMotionCollision().fromJson(element, parser));
		this.components.put("minecraft:particle_motion_dynamic", (element, parser) -> new BedrockComponentMotionDynamic().fromJson(element, parser));

		/* TODO:
		 * 8. minecraft:particle_motion_parametric
		 */
	}

	@Override
	public BedrockScheme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		BedrockScheme particle = new BedrockScheme();

		if (!json.isJsonObject())
		{
			throw new JsonParseException("The root element of Bedrock particle should be an object!");
		}

		/* Skip format_version check to avoid breaking semi-compatible particles */
		JsonObject root = json.getAsJsonObject();

		try
		{
			this.parseEffect(particle, this.getObject(root, "particle_effect", "No particle_effect was found..."));
		}
		catch (MolangException e)
		{
			throw new JsonParseException("Couldn't parse some MoLang expression!", e);
		}

		return particle;
	}

	private void parseEffect(BedrockScheme scheme, JsonObject effect) throws JsonParseException, MolangException
	{
		this.parseDescription(scheme, this.getObject(effect, "description", "No particle_effect.description was found..."));

		if (effect.has("curves"))
		{
			JsonElement curves = effect.get("curves");

			if (curves.isJsonObject())
			{
				this.parseCurves(scheme, curves.getAsJsonObject());
			}
		}

		this.parseComponents(scheme, this.getObject(effect, "components", "No particle_effect.components was found..."));
	}

	/**
	 * Parse description object (which contains ID of the particle, material type and texture)
	 */
	private void parseDescription(BedrockScheme scheme, JsonObject description) throws JsonParseException
	{
		if (description.has("identifier"))
		{
			scheme.identifier = description.get("identifier").getAsString();
		}

		JsonObject parameters = this.getObject(description, "basic_render_parameters", "No particle_effect.basic_render_parameters was found...");

		if (parameters.has("material"))
		{
			scheme.material = BedrockMaterial.fromString(parameters.get("material").getAsString());
		}

		if (parameters.has("texture"))
		{
			String texture = parameters.get("texture").getAsString();

			if (!texture.equals("textures/particle/particles"))
			{
				scheme.texture = RLUtils.create(texture);
			}
		}
	}

	/**
	 * Parse curves object
	 */
	private void parseCurves(BedrockScheme scheme, JsonObject curves) throws MolangException
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
					curve.input = scheme.parser.parseJson(object.get("input"));
				}

				if (object.has("horizontal_range"))
				{
					curve.range = scheme.parser.parseJson(object.get("horizontal_range"));
				}

				if (object.has("nodes"))
				{
					JsonArray nodes = object.getAsJsonArray("nodes");
					MolangExpression[] result = new MolangExpression[nodes.size()];

					for (int i = 0, c = result.length; i < c; i ++)
					{
						result[i] = scheme.parser.parseJson(nodes.get(i));
					}

					curve.nodes = result;
				}

				scheme.curves.put(entry.getKey(), curve);
			}
		}
	}

	private void parseComponents(BedrockScheme scheme, JsonObject components) throws MolangException
	{
		for (Map.Entry<String, JsonElement> entry : components.entrySet())
		{
			String key = entry.getKey();

			if (this.components.containsKey(key))
			{
				scheme.components.add(this.components.get(key).parse(entry.getValue(), scheme.parser));
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

	public static interface IComponentParser
	{
		public BedrockComponentBase parse(JsonElement element, MolangParser parser) throws MolangException;
	}
}