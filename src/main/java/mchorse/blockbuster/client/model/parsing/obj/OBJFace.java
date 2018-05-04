package mchorse.blockbuster.client.model.parsing.obj;

/**
 * Face that holds indices for loading data
 */
class OBJFace
{
    /**
     * List of idxGroup groups for a face triangle (3 vertices per face).
     */
    public OBJIndexGroup[] idxGroups = new OBJIndexGroup[3];

    public OBJFace(String[] lines)
    {
        for (int i = 0; i < 3; i++)
        {
            this.idxGroups[i] = this.parseLine(lines[i]);
        }
    }

    /**
     * Parse index group from a string in format of "1/2/3". It can be also 
     * "1//3" if the model doesn't provides texture coordinates.
     */
    private OBJIndexGroup parseLine(String line)
    {
        OBJIndexGroup idxGroup = new OBJIndexGroup();
        String[] lineTokens = line.split("/");
        int length = lineTokens.length;

        idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;

        if (length > 1)
        {
            /* It can be empty if the obj does not define text coords */
            String textCoord = lineTokens[1];

            if (!textCoord.isEmpty())
            {
                idxGroup.idxTextCoord = Integer.parseInt(textCoord) - 1;
            }

            if (length > 2)
            {
                idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
            }
        }

        return idxGroup;
    }
}