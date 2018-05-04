package mchorse.blockbuster.client.model.parsing.obj;

/**
 * Index group class
 * 
 * This class represents an index group. Used in {@link OBJFace} class to 
 * represent an index group for looking up vertices from the collected 
 * arrays. 
 */
class OBJIndexGroup
{
    public static final int NO_VALUE = -1;

    public int idxPos = NO_VALUE;
    public int idxTextCoord = NO_VALUE;
    public int idxVecNormal = NO_VALUE;
}