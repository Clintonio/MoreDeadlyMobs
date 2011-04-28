package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;

public class EntityCreeper extends EntityMob
{

    public EntityCreeper(World world)
    {
        super(world);
        texture = "/mob/creeper.png";
    }

    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte)-1));
        dataWatcher.addObject(17, Byte.valueOf((byte)0));
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        if(dataWatcher.getWatchableObjectByte(17) == 1)
        {
            nbttagcompound.setBoolean("powered", true);
        }
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        dataWatcher.updateObject(17, Byte.valueOf((byte)(nbttagcompound.getBoolean("powered") ? 1 : 0)));
    }

    public void onUpdate()
    {
        lastActiveTime = timeSinceIgnited;
        if(worldObj.multiplayerWorld)
        {
            int i = func_21091_q();
            if(i > 0 && timeSinceIgnited == 0)
            {
                worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
            }
            timeSinceIgnited += i;
            if(timeSinceIgnited < 0)
            {
                timeSinceIgnited = 0;
            }
            if(timeSinceIgnited >= 30)
            {
                timeSinceIgnited = 30;
            }
        }
        super.onUpdate();
    }

    protected String getHurtSound()
    {
        return "mob.creeper";
    }

    protected String getDeathSound()
    {
        return "mob.creeperdeath";
    }

    public void onDeath(Entity entity)
    {
        super.onDeath(entity);
        if(entity instanceof EntitySkeleton)
        {
            dropItem(Item.record13.shiftedIndex + rand.nextInt(2), 1);
        }
    }
	//===================
	// START DIGGING MOBS
	//===================
	/**
	* Block IDs this class of mob can destroy
	*/
	public int[] canDestroy = {1, 2, 3, 4, 5, 12, 13, 17, 18, 20, 24, 35, 64, 71, 87};
	
	/**
	* Dig to the current target entity
	*/
	protected void digToEntity(Entity digToEntity) {
		if(mod_DiggingMobs.creepersDig.getValue()) {
			digToEntity(digToEntity, canDestroy);
		}
	}
	
	/**
	* The action an entity does to dig through a block
	*
	* @param	id	ID of block to dig
	*/
	protected void digThroughBlock(int id, int i, int j, int k) {
		// Make the creeper explode at the player
		if(timeSinceIgnited == 0) {
			worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
		}
		func_21090_e(1);
		timeSinceIgnited++;
		if(timeSinceIgnited >= 30) {
			worldObj.createExplosion(this, i, j, k, 3F);
			setEntityDead();
		}
		hasAttacked = true;
	}
	//===================
	// END DIGGING MOBS
	//===================
	

    protected void attackEntity(Entity entity, float f)
    {
        int i = func_21091_q();
        if(i <= 0 && f < 3F || i > 0 && f < 7F)
        {
            if(timeSinceIgnited == 0)
            {
                worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
            }
            func_21090_e(1);
            timeSinceIgnited++;
            if(timeSinceIgnited >= 30)
            {
                if(func_27022_s())
                {
                    worldObj.createExplosion(this, posX, posY, posZ, 6F);
                } else
                {
                    worldObj.createExplosion(this, posX, posY, posZ, 3F);
                }
                setEntityDead();
            }
            hasAttacked = true;
        } else
        {
            func_21090_e(-1);
            timeSinceIgnited--;
            if(timeSinceIgnited < 0)
            {
                timeSinceIgnited = 0;
            }
        }
    }

    public boolean func_27022_s()
    {
        return dataWatcher.getWatchableObjectByte(17) == 1;
    }

    public float setCreeperFlashTime(float f)
    {
        return ((float)lastActiveTime + (float)(timeSinceIgnited - lastActiveTime) * f) / 28F;
    }

    protected int getDropItemId()
    {
        return Item.gunpowder.shiftedIndex;
    }

    private int func_21091_q()
    {
        return dataWatcher.getWatchableObjectByte(16);
    }

    private void func_21090_e(int i)
    {
        dataWatcher.updateObject(16, Byte.valueOf((byte)i));
    }

    public void func_27014_a(EntityLightningBolt entitylightningbolt)
    {
        super.func_27014_a(entitylightningbolt);
        dataWatcher.updateObject(17, Byte.valueOf((byte)1));
    }

    int timeSinceIgnited;
    int lastActiveTime;
}
