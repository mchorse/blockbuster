package mchorse.blockbuster.core;

import mchorse.blockbuster.core.transformers.*;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

public class BBCoreClassTransformer extends CoreClassTransformer
{
    private WorldTransformer world = new WorldTransformer();
    private RenderGlobalTransformer render = new RenderGlobalTransformer();
    private EntityRendererTransformer entityRenderer = new EntityRendererTransformer();
    private RenderPlayerTransformer playerTransformer = new RenderPlayerTransformer();
    private RenderItemTransformer renderItem = new RenderItemTransformer();
    private RenderEntityItemTransformer renderEntityItemTransformer = new RenderEntityItemTransformer();
    private EntityTransformer entity = new EntityTransformer();
    private EntityTransformationUtilsTransformer entityTransformationUtils = new EntityTransformationUtilsTransformer();
    private EntityItemTransformer entityItemTransformer = new EntityItemTransformer();
    private InventoryPlayerTransformer inventoryPlayerTransformer = new InventoryPlayerTransformer();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (checkName(name, "amu", "net.minecraft.world.World"))
        {
            System.out.println("BBCoreMod: Transforming World class (" + name + ")");

            return this.world.transform(name, basicClass);
        }
        else if (checkName(name, "cct", "net.minecraft.client.renderer.entity.RenderPlayer")){

            System.out.println("BBCoreMod: Transforming RenderPlayer class (" + name + ")");

            return this.playerTransformer.transform(name,basicClass);
        }
        else if (checkName(name, "buy", "net.minecraft.client.renderer.RenderGlobal"))
        {
            System.out.println("BBCoreMod: Transforming RenderGlobal class (" + name + ")");

            return this.render.transform(name, basicClass);
        }
        else if (checkName(name, "vg", "net.minecraft.entity.Entity"))
        {
            System.out.println("BBCoreMod: Transforming Entity class (" + name + ")");

            return this.entity.transform(name, basicClass);
        }
        else if (name.equals("mchorse.blockbuster.utils.EntityTransformationUtils"))
        {
            System.out.println("BBCoreMod: Transforming EntityTransformationUtils class (" + name + ")");

            return this.entityTransformationUtils.transform(name, basicClass);
        }
        else if (checkName(name, "buq", "net.minecraft.client.renderer.EntityRenderer"))
        {
            System.out.println("BBCoreMod: Transforming EntityRenderer class (" + name + ")");

            return this.entityRenderer.transform(name, basicClass);
        }
        else if (checkName(name, "bzw", "net.minecraft.client.renderer.RenderItem"))
        {
            System.out.println("BBCoreMod: Transforming RenderItem class (" + name + ")");

            return this.renderItem.transform(name, basicClass);
        }
        else if (checkName(name, "bzu", "net.minecraft.client.renderer.entity.RenderEntityItem"))
        {
            System.out.println("BBCoreMod: Transforming RenderItem class (" + name + ")");

            return this.renderEntityItemTransformer.transform(name, basicClass);
        }
        else if (checkName(name, "acl", "net.minecraft.entity.item.EntityItem"))
        {
            System.out.println("BBCoreMod: Transforming EntityItem class (" + name + ")");

            return this.entityItemTransformer.transform(name, basicClass);
        }
        else if (checkName(name, "aec", "net.minecraft.entity.player.InventoryPlayer"))
        {
            System.out.println("BBCoreMod: Transforming InventoryPlayer class (" + name + ")");

            return this.inventoryPlayerTransformer.transform(name, basicClass);
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
        else if (node instanceof VarInsnNode)
        {
            VarInsnNode var = (VarInsnNode) node;

            return "opcode " + var.getOpcode() + " var " + var.var;
        }
        else if (node instanceof LdcInsnNode)
        {
            LdcInsnNode ldc = (LdcInsnNode) node;

            return "LDC " + ldc.cst.toString();
        }

        return "opcode " + String.valueOf(node.getOpcode());
    }
}