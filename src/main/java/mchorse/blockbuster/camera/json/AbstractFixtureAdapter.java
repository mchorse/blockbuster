package mchorse.blockbuster.camera.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.camera.fixtures.CircularFixture;
import mchorse.blockbuster.camera.fixtures.FollowFixture;
import mchorse.blockbuster.camera.fixtures.IdleFixture;
import mchorse.blockbuster.camera.fixtures.LookFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;

/**
 * This class is responsible for serializing and deserializing an abstract
 * camera fixtures types.
 */
public class AbstractFixtureAdapter implements JsonSerializer<AbstractFixture>, JsonDeserializer<AbstractFixture>
{
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public AbstractFixture deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject map = json.getAsJsonObject();
        String type = map.get("type").getAsString();

        try
        {
            AbstractFixture fixture = null;

            if (type.equals("idle"))
            {
                fixture = this.gson.fromJson(json, IdleFixture.class);
            }
            else if (type.equals("path"))
            {
                fixture = this.gson.fromJson(json, PathFixture.class);
            }
            else if (type.equals("look"))
            {
                fixture = this.gson.fromJson(json, LookFixture.class);
            }
            else if (type.equals("follow"))
            {
                fixture = this.gson.fromJson(json, FollowFixture.class);
            }
            else if (type.equals("circular"))
            {
                fixture = this.gson.fromJson(json, CircularFixture.class);
            }

            if (type.equals("follow") || type.equals("look"))
            {
                ((LookFixture) fixture).setTarget(map.get("target").getAsString());
            }

            return fixture;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public JsonElement serialize(AbstractFixture src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject object = (JsonObject) this.gson.toJsonTree(src);

        if (src instanceof LookFixture)
        {
            object.addProperty("target", ((LookFixture) src).getTarget().getUniqueID().toString());
        }

        object.addProperty("type", getKeyByValue(AbstractFixture.STRING_TO_TYPE, src.getType()));

        return object;
    }

    /**
     * From StackOverflow
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value)
    {
        for (Entry<T, E> entry : map.entrySet())
        {
            if (Objects.equals(value, entry.getValue()))
            {
                return entry.getKey();
            }
        }
        return null;
    }
}