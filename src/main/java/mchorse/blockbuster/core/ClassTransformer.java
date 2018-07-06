package mchorse.blockbuster.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public abstract class ClassTransformer
{
    public byte[] transform(String name, byte[] bytes)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        this.process(name, classNode);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        classNode.accept(writer);

        return writer.toByteArray();
    }

    public abstract void process(String name, ClassNode node);
}