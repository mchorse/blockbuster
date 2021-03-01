package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassMethodTransformer;
import mchorse.blockbuster.utils.mclib.coremod.ClassTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

/**
 * This patches the entity to save the position before the previous position.
 * The reason for this is to be able to calculate acceleration - the difference of previous velocity and current velocity
 * @author Christian F. (known as Chryfi)
 */

public class EntityTransformer extends ClassTransformer
{

    @Override
    public void process(String s, ClassNode node)
    {
        FieldNode prevPrevPosX = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, "prevPrevPosX", "D", null, null);
        FieldNode prevPrevPosY = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, "prevPrevPosY", "D", null, null);
        FieldNode prevPrevPosZ = new FieldNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, "prevPrevPosZ", "D", null, null);
        
        node.fields.add(prevPrevPosX);
        node.fields.add(prevPrevPosY);
        node.fields.add(prevPrevPosZ);

        for (MethodNode method : node.methods)
        {
            String methodName = this.checkName(method, "Y", "()V", "onEntityUpdate", "()V");

            if (methodName != null)
            {
                this.processOnEntityUpdate(method);

                break;
            }
        }
        
        System.out.println("BBCoreMod: successfully patched Entities!");
    }

    public void processOnEntityUpdate(MethodNode method)
    {
        InsnList list = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "prevPosX", "D"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/Entity", "prevPrevPosX", "D"));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "prevPosY", "D"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/Entity", "prevPrevPosY", "D"));

        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "prevPosZ", "D"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/Entity", "prevPrevPosZ", "D"));

        method.instructions.insert(this.getFirstLabel(method), list);

        System.out.println("BBCoreMod: successfully patched onEntityUpdate!");
    }
}