package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class EntityTransformationUtilsTransformer extends ClassTransformer
{
    @Override
    public void process(String s, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            String entityDesc = "Lnet/minecraft/entity/Entity;";

            if (method.name.equals("getPrevPrevPosX") && method.desc.equals("("+entityDesc+")D"))
            {
                this.processGetPrevPrevPos(method, "X");
            }
            else if (method.name.equals("getPrevPrevPosY") && method.desc.equals("("+entityDesc+")D"))
            {
                this.processGetPrevPrevPos(method, "Y");
            }
            else if (method.name.equals("getPrevPrevPosZ") && method.desc.equals("("+entityDesc+")D"))
            {
                this.processGetPrevPrevPos(method, "Z");
            }
        }

        System.out.println("BBCoreMod: successfully patched EntityTransformationUtils!");
    }

    public void processGetPrevPrevPos(MethodNode method, String axis)
    {
        Iterator<AbstractInsnNode> it = method.instructions.iterator();
        AbstractInsnNode target = null;
        int index = -1;

        while (it.hasNext())
        {
            index++;
            AbstractInsnNode node = it.next();

            if (node instanceof InsnNode)
            {
                if (node.getOpcode() == Opcodes.DCONST_0)
                {
                    target = node;

                    break;
                }
            }
        }

        method.instructions.remove(target);

        target =  method.instructions.get(index-1);
        InsnList list = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, CoreClassTransformer.get("vg", "net/minecraft/entity/Entity"), "prevPrevPos"+axis, "D"));
        method.instructions.insert(target, list);

        System.out.println("BBCoreMod: successfully patched getPrevPrevPos"+axis+"!");
    }
}
