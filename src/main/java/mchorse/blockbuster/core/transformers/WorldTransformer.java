package mchorse.blockbuster.core.transformers;

import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mchorse.mclib.utils.coremod.ClassMethodTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;

public class WorldTransformer extends ClassMethodTransformer
{
    public WorldTransformer()
    {
        this.mcp = "setBlockState";
        this.mcpSign = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z";
        this.notch = "a";
        this.notchSign = "(Lco;Latl;I)Z";
    }

    @Override
    public void processMethod(String name, MethodNode method)
    {
        InsnList list = method.instructions;
        Iterator<AbstractInsnNode> it = list.iterator();
        int i = 0;

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (node.getOpcode() == Opcodes.IRETURN)
            {
                i++;

                continue;
            }

            if (i == 2)
            {
                InsnList newList = new InsnList();
                String desc = CoreClassTransformer.get("(Lajs;Lco;Latl;I)V", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)V");

                newList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                newList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                newList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/recording/WorldEventListener", "setBlockState", desc, false));
                list.insert(node, newList);

                System.out.println("BBCoreMod: successfully patched setBlockState!");

                break;
            }
        }
    }
}
