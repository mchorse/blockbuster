package mchorse.blockbuster.common.entity;

import mchorse.mclib.utils.DummyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ExpirableDummyEntity extends DummyEntity
{
    private int lifetime;
    private int age;

    /**
     * Sets the height and width to 0
     * @param worldIn
     * @param lifetime
     */
    public ExpirableDummyEntity(World worldIn, int lifetime)
    {
        this(worldIn, lifetime, 0, 0);
    }

    public ExpirableDummyEntity(World worldIn, int lifetime, float height, float width)
    {
        super(worldIn);

        this.lifetime = lifetime;
        this.height = height;
        this.width = width;
    }

    public void setLifetime(int lifetime)
    {
        this.lifetime = lifetime;
    }

    public int getLifetime()
    {
        return this.lifetime;
    }

    public int getAge()
    {
        return this.age;
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        if (this.age >= this.lifetime)
        {
            this.setDead();
        }

        this.age++;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {}

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entityIn)
    {

    }
}
