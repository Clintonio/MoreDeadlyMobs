package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;

public class EntityZombie extends EntityMob
{

    public EntityZombie(World world)
    {
        super(world);
        texture = "/mob/zombie.png";
        moveSpeed = 0.5F;
        attackStrength = 5;
		
		//===================
		// START DIGGING MOBS
		//===================
		// Items zombies can destroy
		int[] canDestroy = { 2,  3,  6, 12, 13, 17, 18, 19, 20, 35, 
							37, 38, 39, 40, 46, 50, 54, 55, 59, 60,
							64, 69, 70, 71, 72, 75, 76, 77, 78, 79,
							80, 81, 82, 83, 85, 86, 87, 88, 89, 91, 
							92};
		this.canDestroy = canDestroy;
		//===================
		// END DIGGING MOBS
		//===================
    }

    public void onLivingUpdate()
    {
        if(worldObj.isDaytime())
        {
            float f = getEntityBrightness(1.0F);
            if(f > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && rand.nextFloat() * 30F < (f - 0.4F) * 2.0F)
            {
                fire = 300;
            }
        }
        super.onLivingUpdate();
    }
	
	//===================
	// START DIGGING MOBS
	//===================
	/**
	* Dig to the current target entity
	*/
	protected void digToEntity(Entity digToEntity) {
		if(mod_DiggingMobs.zombiesDig.getValue()) {
			super.digToEntity(digToEntity, canDestroy);
		}
	}
	//===================
	// END DIGGING MOBS
	//===================

    protected String getLivingSound()
    {
        return "mob.zombie";
    }

    protected String getHurtSound()
    {
        return "mob.zombiehurt";
    }

    protected String getDeathSound()
    {
        return "mob.zombiedeath";
    }

    protected int getDropItemId()
    {
        return Item.feather.shiftedIndex;
    }
}
