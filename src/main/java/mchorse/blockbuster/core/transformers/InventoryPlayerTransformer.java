package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassMethodTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.Opcodes;

import java.util.Iterator;

public class InventoryPlayerTransformer extends ClassMethodTransformer
{
    public InventoryPlayerTransformer()
    {
        /* insert a hook before and after something is added to inventory */
        this.setMcp("addItemStackToInventory", "(Lnet/minecraft/item/ItemStack;)Z");
        this.setNotch("e", "(Laip;)Z");
    }

    @Override
    public void processMethod(String name, MethodNode method)
    {
        InsnList list = method.instructions;
        Iterator<AbstractInsnNode> it = list.iterator();
        String inventoryPlayerClassName = CoreClassTransformer.get("Laec;", "Lnet/minecraft/entity/player/InventoryPlayer;");

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            /* only call in this method is this.add(...) -> search for ALOAD of this*/
            if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) node).var == 0)
            {
                MethodInsnNode beforeEvent = new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/events/PlayerHandler", "beforeItemStackAdd", "(" + inventoryPlayerClassName + ")V", false);
                MethodInsnNode afterEvent = new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/events/PlayerHandler", "afterItemStackAdd", "(" + inventoryPlayerClassName + ")V", false);

                InsnList preInstructions = new InsnList();
                InsnList postInstructions = new InsnList();

                preInstructions.add(beforeEvent);
                preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

                postInstructions.add(new VarInsnNode(Opcodes.ISTORE, 2));
                postInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                postInstructions.add(afterEvent);
                postInstructions.add(new VarInsnNode(Opcodes.ILOAD, 2));

                method.instructions.insert(node, preInstructions);

                /* go to method call of this.add() */
                while (it.hasNext())
                {
                    AbstractInsnNode node2 = it.next();

                    if (node2 instanceof MethodInsnNode && node2.getOpcode() == Opcodes.INVOKEVIRTUAL)
                    {
                        method.instructions.insert(node2, postInstructions);

                        System.out.println("BBCoreMod: successfully patched InventoryPlayer!");

                        return;
                    }
                }
            }
        }
    }
}
