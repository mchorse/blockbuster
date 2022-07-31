package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassMethodTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

public class EntityItemTransformer extends ClassMethodTransformer
{
    public EntityItemTransformer()
    {
        /*
         * insert a hook right before item is added and forge's item pickup event is called
         * this is used to prevent item pick up during first person playback
         */
        this.setMcp("onCollideWithPlayer", "(Lnet/minecraft/entity/player/EntityPlayer;)V");
        this.setNotch("d", "(Laed;)V");
    }

    @Override
    public void processMethod(String name, MethodNode method)
    {
        InsnList list = method.instructions;
        Iterator<AbstractInsnNode> it = list.iterator();

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (node instanceof VarInsnNode)
            {
                VarInsnNode varInsnNode = (VarInsnNode) node;

                /*
                 * ItemStack clone = itemstack.copy(); is stored at place 6
                 * this is right before item is added to inventory in the if statement
                 */
                if (varInsnNode.getOpcode() == Opcodes.ASTORE && varInsnNode.var == 6)
                {
                    VarInsnNode loadEntityPlayerArg = new VarInsnNode(Opcodes.ALOAD, 1);

                    /* load itemStack variable (ItemStack itemStack = this.getItem();*/
                    VarInsnNode loadItemStack = new VarInsnNode(Opcodes.ALOAD, 2);
                    String itemStackClassName = CoreClassTransformer.get("Laed;Laip;", "Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;");

                    method.instructions.insert(varInsnNode, loadEntityPlayerArg);
                    method.instructions.insert(loadEntityPlayerArg, loadItemStack);
                    method.instructions.insert(loadItemStack, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/events/PlayerHandler", "beforePlayerItemPickUp", "(" + itemStackClassName + ")V", false));

                    System.out.println("BBCoreMod: successfully patched EntityItem!");

                    return;
                }
            }
        }
    }
}
