package mchorse.blockbuster.client.particles;

public enum BedrockCurveType
{
	LINEAR, HERMITE;

	public static BedrockCurveType fromString(String type)
	{
		if (type.equals("catmull_rom"))
		{
			return HERMITE;
		}

		return LINEAR;
	}
}