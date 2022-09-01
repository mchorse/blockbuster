package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

public class RenderGlobalTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            String methodName = this.checkName(method, "a", "(FI)V", "renderSky", "(FI)V");

            if (methodName != null)
            {
                this.processRenderSky(method);
            }

            methodName = this.checkName(method, "a", "(FIDDD)V", "renderClouds", "(FIDDD)V");

            if (methodName != null)
            {
                this.processRenderClouds(method);
            }

            methodName = this.checkName(method, "a", "(Lvg;Lbxy;F)V", "renderEntities", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V");

            if (methodName != null)
            {
                this.processRenderEntities(method);
            }
        }
    }

    private void processRenderSky(MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();

            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "isGreenSky", "()Z", false));
            list.add(new JumpInsnNode(Opcodes.IFEQ, label));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderGreenSky", "()V", false));
            list.add(new InsnNode(Opcodes.RETURN));

            method.instructions.insert(list);

            System.out.println("BBCoreMod: successfully patched renderSky!");
        }
    }

    private void processRenderClouds(MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();

            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "isGreenSky", "()Z", false));
            list.add(new JumpInsnNode(Opcodes.IFEQ, label));
            list.add(new InsnNode(Opcodes.RETURN));

            method.instructions.insert(list);

            System.out.println("BBCoreMod: successfully patched renderClouds!");
        }
    }

    private void processRenderEntities(MethodNode method)
    {
        LabelNode releaseLabel = null;
        LabelNode renderEntityLabel = null;
        LabelNode lastLabel = null;
        boolean captureNext = false;
        int renderCounter = 0;
        Iterator<AbstractInsnNode> it = method.instructions.iterator();
        boolean insertedRenderLastCall = false;
        boolean foundAllForAddEntitiesHook = false;

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (!foundAllForAddEntitiesHook && node instanceof LabelNode)
            {
                lastLabel = (LabelNode) node;

                if (captureNext && renderEntityLabel == null)
                {
                    renderEntityLabel = lastLabel;
                    captureNext = false;
                }
            }

            if (node instanceof MethodInsnNode)
            {
                MethodInsnNode methodInsnNode = (MethodInsnNode) node;

                if (!foundAllForAddEntitiesHook)
                {
                    if (CoreClassTransformer.checkName(methodInsnNode.owner, "bzf", "net/minecraft/client/renderer/entity/RenderManager")
                            && CoreClassTransformer.checkName(methodInsnNode.name, "a", "renderEntityStatic")
                            && CoreClassTransformer.checkName(methodInsnNode.desc, "(Lvg;FZ)V", "(Lnet/minecraft/entity/Entity;FZ)V"))
                    {
                        if (renderCounter == 1)
                        {
                            captureNext = true;
                        }

                        renderCounter += 1;
                    }

                    if (CoreClassTransformer.checkName(methodInsnNode.owner, "et$b", "net/minecraft/util/math/BlockPos$PooledMutableBlockPos")
                            && CoreClassTransformer.checkName(methodInsnNode.name, "t", "release")
                            && methodInsnNode.desc.equals("()V"))
                    {
                        releaseLabel = lastLabel;

                        foundAllForAddEntitiesHook = true;
                    }
                }

                /* this should indicate the end of the method renderEntities (postRenderDamagedBlocks is the 3rd method before end) */
                if (CoreClassTransformer.checkName(methodInsnNode.owner, "buy", "net/minecraft/client/renderer/RenderGlobal")
                    && CoreClassTransformer.checkName(methodInsnNode.name, "v", "postRenderDamagedBlocks")
                    && methodInsnNode.desc.equals("()V"))
                {
                    /* Render last entities after postRenderDamagedBlocks to avoid OpenGL states that were meant for damagedBlocks */
                    method.instructions.insert(methodInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderLastEntities", "()V", false));

                    insertedRenderLastCall = true;

                    break;
                }
            }
        }

        if (renderEntityLabel != null && releaseLabel != null)
        {
            /* In non-Optifine Minecraft, the index of the entity variable is 27,
             * however due to Optifine modifications, it's another index, but it
             * should be the last local variable... */
            final String entity = CoreClassTransformer.get("Lvg;", "Lnet/minecraft/entity/Entity;");
            int localIndex = 0;

            for (LocalVariableNode var : method.localVariables)
            {
                if (var.desc.equals(entity))
                {
                    localIndex = Math.max(localIndex, var.index);
                }
            }

            /* Add render entity */
            InsnList list = new InsnList();

            list.add(new VarInsnNode(Opcodes.ALOAD, localIndex));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "addRenderedEntity", "(" + entity + ")V", false));

            /* avoid memory leak - if renderLast method is not called, the list of rendered entities would not be cleared */
            if (insertedRenderLastCall)
            {
                method.instructions.insert(renderEntityLabel, list);

                System.out.println("BBCoreMod: successfully patched renderEntities!");
            }
        }
    }
}