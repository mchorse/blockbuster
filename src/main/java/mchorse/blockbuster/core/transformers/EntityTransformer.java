package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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
        String entity = CoreClassTransformer.get("vg", "net/minecraft/entity/Entity");
        String prevPosX = CoreClassTransformer.get("m", "prevPosX");
        String prevPosY = CoreClassTransformer.get("n", "prevPosY");
        String prevPosZ = CoreClassTransformer.get("o", "prevPosZ");

        buildFieldAssignmentInsn(list, entity, prevPosX, "prevPrevPosX");
        buildFieldAssignmentInsn(list, entity, prevPosY, "prevPrevPosY");
        buildFieldAssignmentInsn(list, entity, prevPosZ, "prevPrevPosZ");

        method.instructions.insert(this.getFirstLabel(method), list);

        System.out.println("BBCoreMod: successfully patched onEntityUpdate!");
    }

    public void buildFieldAssignmentInsn(InsnList list, String owner, String get, String put)
    {
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, owner, get, "D"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, owner, put, "D"));
    }
}