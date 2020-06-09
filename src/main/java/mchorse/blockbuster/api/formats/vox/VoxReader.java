package mchorse.blockbuster.api.formats.vox;

import mchorse.blockbuster.api.formats.vox.data.Vox;
import mchorse.blockbuster.api.formats.vox.data.VoxChunk;
import mchorse.blockbuster.api.formats.vox.data.VoxGroup;
import mchorse.blockbuster.api.formats.vox.data.VoxLayer;
import mchorse.blockbuster.api.formats.vox.data.VoxShape;
import mchorse.blockbuster.api.formats.vox.data.VoxTransform;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MagicaVoxel *.vox reader
 *
 * This class reads the file and returns vox model
 *
 * @link https://github.com/ephtracy/voxel-model/blob/master/MagicaVoxel-file-format-vox.txt
 * @link https://github.com/ephtracy/voxel-model/blob/master/MagicaVoxel-file-format-vox-extension.txt
 */
public class VoxReader
{
    public static int[] DEFAULT_PALETTE = new int[] {0x00000000, 0xffffffff, 0xffccffff, 0xff99ffff, 0xff66ffff, 0xff33ffff, 0xff00ffff, 0xffffccff, 0xffccccff, 0xff99ccff, 0xff66ccff, 0xff33ccff, 0xff00ccff, 0xffff99ff, 0xffcc99ff, 0xff9999ff, 0xff6699ff, 0xff3399ff, 0xff0099ff, 0xffff66ff, 0xffcc66ff, 0xff9966ff, 0xff6666ff, 0xff3366ff, 0xff0066ff, 0xffff33ff, 0xffcc33ff, 0xff9933ff, 0xff6633ff, 0xff3333ff, 0xff0033ff, 0xffff00ff, 0xffcc00ff, 0xff9900ff, 0xff6600ff, 0xff3300ff, 0xff0000ff, 0xffffffcc, 0xffccffcc, 0xff99ffcc, 0xff66ffcc, 0xff33ffcc, 0xff00ffcc, 0xffffcccc, 0xffcccccc, 0xff99cccc, 0xff66cccc, 0xff33cccc, 0xff00cccc, 0xffff99cc, 0xffcc99cc, 0xff9999cc, 0xff6699cc, 0xff3399cc, 0xff0099cc, 0xffff66cc, 0xffcc66cc, 0xff9966cc, 0xff6666cc, 0xff3366cc, 0xff0066cc, 0xffff33cc, 0xffcc33cc, 0xff9933cc, 0xff6633cc, 0xff3333cc, 0xff0033cc, 0xffff00cc, 0xffcc00cc, 0xff9900cc, 0xff6600cc, 0xff3300cc, 0xff0000cc, 0xffffff99, 0xffccff99, 0xff99ff99, 0xff66ff99, 0xff33ff99, 0xff00ff99, 0xffffcc99, 0xffcccc99, 0xff99cc99, 0xff66cc99, 0xff33cc99, 0xff00cc99, 0xffff9999, 0xffcc9999, 0xff999999, 0xff669999, 0xff339999, 0xff009999, 0xffff6699, 0xffcc6699, 0xff996699, 0xff666699, 0xff336699, 0xff006699, 0xffff3399, 0xffcc3399, 0xff993399, 0xff663399, 0xff333399, 0xff003399, 0xffff0099, 0xffcc0099, 0xff990099, 0xff660099, 0xff330099, 0xff000099, 0xffffff66, 0xffccff66, 0xff99ff66, 0xff66ff66, 0xff33ff66, 0xff00ff66, 0xffffcc66, 0xffcccc66, 0xff99cc66, 0xff66cc66, 0xff33cc66, 0xff00cc66, 0xffff9966, 0xffcc9966, 0xff999966, 0xff669966, 0xff339966, 0xff009966, 0xffff6666, 0xffcc6666, 0xff996666, 0xff666666, 0xff336666, 0xff006666, 0xffff3366, 0xffcc3366, 0xff993366, 0xff663366, 0xff333366, 0xff003366, 0xffff0066, 0xffcc0066, 0xff990066, 0xff660066, 0xff330066, 0xff000066, 0xffffff33, 0xffccff33, 0xff99ff33, 0xff66ff33, 0xff33ff33, 0xff00ff33, 0xffffcc33, 0xffcccc33, 0xff99cc33, 0xff66cc33, 0xff33cc33, 0xff00cc33, 0xffff9933, 0xffcc9933, 0xff999933, 0xff669933, 0xff339933, 0xff009933, 0xffff6633, 0xffcc6633, 0xff996633, 0xff666633, 0xff336633, 0xff006633, 0xffff3333, 0xffcc3333, 0xff993333, 0xff663333, 0xff333333, 0xff003333, 0xffff0033, 0xffcc0033, 0xff990033, 0xff660033, 0xff330033, 0xff000033, 0xffffff00, 0xffccff00, 0xff99ff00, 0xff66ff00, 0xff33ff00, 0xff00ff00, 0xffffcc00, 0xffcccc00, 0xff99cc00, 0xff66cc00, 0xff33cc00, 0xff00cc00, 0xffff9900, 0xffcc9900, 0xff999900, 0xff669900, 0xff339900, 0xff009900, 0xffff6600, 0xffcc6600, 0xff996600, 0xff666600, 0xff336600, 0xff006600, 0xffff3300, 0xffcc3300, 0xff993300, 0xff663300, 0xff333300, 0xff003300, 0xffff0000, 0xffcc0000, 0xff990000, 0xff660000, 0xff330000, 0xff0000ee, 0xff0000dd, 0xff0000bb, 0xff0000aa, 0xff000088, 0xff000077, 0xff000055, 0xff000044, 0xff000022, 0xff000011, 0xff00ee00, 0xff00dd00, 0xff00bb00, 0xff00aa00, 0xff008800, 0xff007700, 0xff005500, 0xff004400, 0xff002200, 0xff001100, 0xffee0000, 0xffdd0000, 0xffbb0000, 0xffaa0000, 0xff880000, 0xff770000, 0xff550000, 0xff440000, 0xff220000, 0xff110000, 0xffeeeeee, 0xffdddddd, 0xffbbbbbb, 0xffaaaaaa, 0xff888888, 0xff777777, 0xff555555, 0xff444444, 0xff222222, 0xff111111};

    public byte[] buf = new byte[4];

    public VoxDocument read(InputStream stream) throws Exception
    {
        if (this.readInt(stream) != this.fourChars("VOX "))
        {
            throw new Exception("Not a 'VOX ' file!");
        }

        if (this.readInt(stream) != 150)
        {
            throw new Exception("Version doesn't match!");
        }

        VoxChunk main = this.readChunk(stream);

        if (main.id != this.fourChars("MAIN"))
        {
            throw new Exception("The first chunk isn't main!");
        }

        VoxDocument document = new VoxDocument();
        Vox vox = null;

        while (true)
        {
            VoxChunk chunk;

            try
            {
                chunk = this.readChunk(stream);
            }
            catch (Exception e)
            {
                break;
            }

            /* System.out.println(chunk.toString() + " " + chunk.size + " " + chunk.chunks); */

            if (chunk.id == this.fourChars("SIZE"))
            {
                vox = new Vox();
                vox.x = this.readInt(stream);
                vox.z = this.readInt(stream);
                vox.y = this.readInt(stream);
            }
            else if (chunk.id == this.fourChars("XYZI"))
            {
                int voxels = this.readInt(stream);

                vox.voxels = new int[vox.x * vox.z * vox.y];

                while (voxels > 0)
                {
                    stream.read(this.buf);

                    int x = this.buf[0];
                    int y = this.buf[2];
                    int z = this.buf[1];
                    int block = this.buf[3];

                    vox.set(x >= 0 ? x : 256 + x, y >= 0 ? y : 256 + y, z >= 0 ? z : 256 + z, block >= 0 ? block : 256 + block);
                    voxels--;
                }

                document.chunks.add(vox);
            }
            else if (chunk.id == this.fourChars("nTRN"))
            {
                document.nodes.add(new VoxTransform(stream, this));
            }
            else if (chunk.id == this.fourChars("nGRP"))
            {
                document.nodes.add(new VoxGroup(stream, this));
            }
            else if (chunk.id == this.fourChars("nSHP"))
            {
                document.nodes.add(new VoxShape(stream, this));
            }
            else if (chunk.id == this.fourChars("LAYR"))
            {
                document.layers.add(new VoxLayer(stream, this));
            }
            else if (chunk.id == this.fourChars("RGBA"))
            {
                document.palette = new int[256];

                for (int i = 0; i <= 254; i++)
                {
                    int color = this.readInt(stream);

                    int newColor = (((color >> 24) & 0xff) << 24);
                    newColor += (((color >> 0) & 0xff) << 16);
                    newColor += (((color >> 8) & 0xff) << 8);
                    newColor += (((color >> 16) & 0xff) << 0);

                    document.palette[i + 1] = newColor;
                }
            }
            else
            {
                stream.skip(chunk.size);
            }
        }

        stream.close();

        return document;
    }

    public VoxChunk readChunk(InputStream stream) throws Exception
    {
        return new VoxChunk(this.readInt(stream), this.readInt(stream), this.readInt(stream));
    }

    public int fourChars(char c0, char c1, char c2, char c3)
    {
        return ((c3 << 24) & 0xff000000) | ((c2 << 16) & 0x00ff0000) | ((c1 << 8) & 0x0000ff00) | (c0 & 0x000000ff);
    }

    public int fourChars(String string) throws Exception
    {
        char[] chars = string.toCharArray();

        if (chars.length != 4)
        {
            throw new Exception("Given string '" + string + "'");
        }

        return this.fourChars(chars[0], chars[1], chars[2], chars[3]);
    }

    public int readInt(InputStream stream) throws Exception
    {
        if (stream.read(this.buf) < 4)
        {
            throw new IOException();
        }

        return (buf[0] & 0xff) | ((buf[1] & 0xff) << 8) | ((buf[2] & 0xff) << 16) | ((buf[3] & 0xff) << 24);
    }

    public String readString(InputStream stream) throws Exception
    {
        int size = this.readInt(stream);
        byte[] bytes = new byte[size];

        if (stream.read(bytes) == size)
        {
            char[] chars = new char[size];

            for (int i = 0; i < size; i ++)
            {
                chars[i] = (char) bytes[i];
            }

            return new String(chars);
        }

        throw new IOException("Not enough bytes for the string!");
    }

    public Map<String, String> readDictionary(InputStream stream) throws Exception
    {
        Map<String, String> dict = new HashMap<String, String>();

        int keys = this.readInt(stream);

        for (int i = 0; i < keys; i ++)
        {
            dict.put(this.readString(stream), this.readString(stream));
        }

        return dict;
    }

    public Matrix3f readRotation(int rotation)
    {
        Matrix3f matrix = new Matrix3f();

        int firstIndex  = (rotation & 0b0011);
        int secondIndex = (rotation & 0b1100) >> 2;
        int[] array = {-1, -1, -1};
        int index = 0;

        array[firstIndex] = 0;
        array[secondIndex] = 0;

        for (int i = 0; i < array.length; i ++)
        {
            if (array[i] == -1)
            {
                index = i;

                break;
            }
        }

        int thirdIndex = index;

        boolean negativeFirst  = ((rotation & 0b0010000) >> 4) == 1;
        boolean negativeSecond = ((rotation & 0b0100000) >> 5) == 1;
        boolean negativeThird  = ((rotation & 0b1000000) >> 6) == 1;

        matrix.setElement(0, firstIndex, negativeFirst ? -1 : 1);
        matrix.setElement(1, secondIndex, negativeSecond ? -1 : 1);
        matrix.setElement(2, thirdIndex, negativeThird ? -1 : 1);

        return matrix;
    }
}