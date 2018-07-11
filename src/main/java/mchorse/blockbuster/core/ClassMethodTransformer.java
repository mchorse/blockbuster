package mchorse.blockbuster.core;

import java.util.Iterator;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class ClassMethodTransformer extends ClassTransformer
{
    public String mcp = "";
    public String mcpSign = "";
    public String notch = "";
    public String notchSign = "";

    @Override
    public void process(String name, ClassNode node)
    {
        Iterator<MethodNode> methods = node.methods.iterator();

        while (methods.hasNext())
        {
            MethodNode method = methods.next();
            String methodName = this.checkName(method);

            if (methodName != null)
            {
                this.processMethod(methodName, method);
            }
        }
    }

    protected String checkName(MethodNode method)
    {
        return this.checkName(method, this.notch, this.notchSign, this.mcp, this.mcpSign);
    }

    public abstract void processMethod(String name, MethodNode method);
}