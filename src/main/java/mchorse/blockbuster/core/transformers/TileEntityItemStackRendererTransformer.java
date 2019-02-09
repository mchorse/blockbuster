package mchorse.blockbuster.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mchorse.mclib.utils.coremod.ClassMethodTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;

public class TileEntityItemStackRendererTransformer extends ClassMethodTransformer
{
    public TileEntityItemStackRendererTransformer()
    {
        this.mcp = "renderByItem";
        this.mcpSign = "(Lnet/minecraft/item/ItemStack;)V";
        this.notch = "a";
        this.notchSign = "(Ladz;)V";
    }

    @Override
    public void processMethod(String name, MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();
            String desc = CoreClassTransformer.get("(Ladz;)Z", "(Lnet/minecraft/item/ItemStack;)Z");

            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderItemStack", desc, false));
            list.add(new JumpInsnNode(Opcodes.IFEQ, label));
            list.add(new InsnNode(Opcodes.RETURN));

            method.instructions.insert(list);
        }
    }
}