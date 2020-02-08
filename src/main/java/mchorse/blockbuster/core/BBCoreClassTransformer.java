package mchorse.blockbuster.core;

import java.util.Iterator;

import mchorse.blockbuster.core.transformers.EntityRendererTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;

import mchorse.blockbuster.core.transformers.RenderGlobalTransformer;
import mchorse.blockbuster.core.transformers.WorldTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;

public class BBCoreClassTransformer extends CoreClassTransformer
{
    public static boolean obfuscated = false;

    private WorldTransformer world = new WorldTransformer();
    private RenderGlobalTransformer render = new RenderGlobalTransformer();
    private EntityRendererTransformer entityRenderer = new EntityRendererTransformer();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (checkName(name, "amu", "net.minecraft.world.World"))
        {
            System.out.println("BBCoreMod: Transforming World class (" + name + ")");

            return this.world.transform(name, basicClass);
        }
        else if (checkName(name, "buy", "net.minecraft.client.renderer.RenderGlobal"))
        {
            System.out.println("BBCoreMod: Transforming RenderGlobal class (" + name + ")");

            return this.render.transform(name, basicClass);
        }
        else if (checkName(name, "buq", "net.minecraft.client.renderer.EntityRenderer"))
        {
            System.out.println("BBCoreMod: Transforming EntityRenderer class (" + name + ")");

            return this.entityRenderer.transform(name, basicClass);
        }

        return basicClass;
    }

    public static void debugInstructions(InsnList list)
    {
        debugInstructions(list, Integer.MAX_VALUE);
    }

    public static void debugInstructions(InsnList list, int max)
    {
        Iterator<AbstractInsnNode> nodes = list.iterator();

        int i = 0;

        while (nodes.hasNext())
        {
            AbstractInsnNode node = nodes.next();

            System.out.println("Offset: " + i + " " + node.getClass().getSimpleName() + " " + debugNode(node));

            if (i >= max)
            {
                break;
            }

            i++;
        }
    }

    public static String debugNode(AbstractInsnNode node)
    {
        if (node instanceof LabelNode)
        {
            return "label " + ((LabelNode) node).getLabel().toString();
        }
        else if (node instanceof LineNumberNode)
        {
            return "line " + String.valueOf(((LineNumberNode) node).line);
        }
        else if (node instanceof MethodInsnNode)
        {
            MethodInsnNode method = (MethodInsnNode) node;

            return method.getOpcode() + " " + method.owner + "." + method.name + ":" + method.desc;
        }
        else if (node instanceof FieldInsnNode)
        {
            FieldInsnNode field = (FieldInsnNode) node;

            return field.getOpcode() + " " + field.owner + "." + field.name + ":" + field.desc;
        }
        else if (node instanceof LdcInsnNode)
        {
            LdcInsnNode ldc = (LdcInsnNode) node;

            return "LDC " + ldc.cst.toString();
        }

        return "opcode " + String.valueOf(node.getOpcode());
    }
}