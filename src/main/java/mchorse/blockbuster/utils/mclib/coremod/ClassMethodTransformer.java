package mchorse.blockbuster.utils.mclib.coremod;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public abstract class ClassMethodTransformer extends ClassTransformer
{
    public String mcp = "";
    public String mcpSign = "";
    public String notch = "";
    public String notchSign = "";

    public ClassMethodTransformer setMcp(String name, String signature)
    {
        this.mcp = name;
        this.mcpSign = signature;

        return this;
    }

    public ClassMethodTransformer setNotch(String name, String signature)
    {
        this.notch = name;
        this.notchSign = signature;

        return this;
    }

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