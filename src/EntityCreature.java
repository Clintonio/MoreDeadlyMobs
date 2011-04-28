package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;
	
//===================
// START DIGGING MOBS
//===================
import net.minecraft.client.Minecraft;
//===================
// END DIGGING MOBS
//===================

public class EntityCreature extends EntityLiving
{

    public EntityCreature(World world)
    {
        super(world);
        hasAttacked = false;
    }

    protected boolean func_25028_d_()
    {
        return false;
    }

    protected void updatePlayerActionState()
    {
        hasAttacked = func_25028_d_();
        float f = 16F;
        if(playerToAttack == null)
        {
            playerToAttack = findPlayerToAttack();
            if(playerToAttack != null)
            {
                pathToEntity = worldObj.getPathToEntity(this, playerToAttack, f);
            }
        } else
        if(!playerToAttack.isEntityAlive())
        {
            playerToAttack = null;
        } else
        {
            float f1 = playerToAttack.getDistanceToEntity(this);
            if(canEntityBeSeen(playerToAttack))
            {
                attackEntity(playerToAttack, f1);
            }
        }
        if(!hasAttacked && playerToAttack != null && (pathToEntity == null || rand.nextInt(20) == 0))
        {
            pathToEntity = worldObj.getPathToEntity(this, playerToAttack, f);
        } else
        if(!hasAttacked && (pathToEntity == null && rand.nextInt(80) == 0 || rand.nextInt(80) == 0))
        {
            boolean flag = false;
            int j = -1;
            int k = -1;
            int l = -1;
            float f2 = -99999F;
            for(int i1 = 0; i1 < 10; i1++)
            {
                int j1 = MathHelper.floor_double((posX + (double)rand.nextInt(13)) - 6D);
                int k1 = MathHelper.floor_double((posY + (double)rand.nextInt(7)) - 3D);
                int l1 = MathHelper.floor_double((posZ + (double)rand.nextInt(13)) - 6D);
                float f3 = getBlockPathWeight(j1, k1, l1);
                if(f3 > f2)
                {
                    f2 = f3;
                    j = j1;
                    k = k1;
                    l = l1;
                    flag = true;
                }
            }

            if(flag)
            {
                pathToEntity = worldObj.getEntityPathToXYZ(this, j, k, l, 10F);
            }
        }
        int i = MathHelper.floor_double(boundingBox.minY + 0.5D);
        boolean flag1 = func_27013_ag();
        boolean flag2 = handleLavaMovement();
        rotationPitch = 0.0F;
        if(pathToEntity == null || rand.nextInt(100) == 0)
        {
            super.updatePlayerActionState();
            pathToEntity = null;
            return;
        }
        Vec3D vec3d = pathToEntity.getPosition(this);
        for(double d = width * 2.0F; vec3d != null && vec3d.squareDistanceTo(posX, vec3d.yCoord, posZ) < d * d;)
        {
            pathToEntity.incrementPathIndex();
            if(pathToEntity.isFinished())
            {
                vec3d = null;
                pathToEntity = null;
            } else
            {
                vec3d = pathToEntity.getPosition(this);
            }
        }

        isJumping = false;
        if(vec3d != null)
        {
            double d1 = vec3d.xCoord - posX;
            double d2 = vec3d.zCoord - posZ;
            double d3 = vec3d.yCoord - (double)i;
            float f4 = (float)((Math.atan2(d2, d1) * 180D) / 3.1415927410125732D) - 90F;
            float f5 = f4 - rotationYaw;
            moveForward = moveSpeed;
            for(; f5 < -180F; f5 += 360F) { }
            for(; f5 >= 180F; f5 -= 360F) { }
            if(f5 > 30F)
            {
                f5 = 30F;
            }
            if(f5 < -30F)
            {
                f5 = -30F;
            }
            rotationYaw += f5;
            if(hasAttacked && playerToAttack != null)
            {
                double d4 = playerToAttack.posX - posX;
                double d5 = playerToAttack.posZ - posZ;
                float f7 = rotationYaw;
                rotationYaw = (float)((Math.atan2(d5, d4) * 180D) / 3.1415927410125732D) - 90F;
                float f6 = (((f7 - rotationYaw) + 90F) * 3.141593F) / 180F;
                moveStrafing = -MathHelper.sin(f6) * moveForward * 1.0F;
                moveForward = MathHelper.cos(f6) * moveForward * 1.0F;
            }
            if(d3 > 0.0D)
            {
                isJumping = true;
            }
        }
        if(playerToAttack != null)
        {
            faceEntity(playerToAttack, 30F, 30F);
			//===================
			// START DIGGING MOBS
			//===================
			if(!worldObj.multiplayerWorld) {
				digToEntity(playerToAttack);
			}
			//===================
			// END DIGGING MOBS
			//===================
        }
        if(isCollidedHorizontally && !hasPath())
        {
            isJumping = true;
        }
        if(rand.nextFloat() < 0.8F && (flag1 || flag2))
        {
            isJumping = true;
        }
    }
	
	//===================
	// START DIGGING MOBS
	//===================
	/**
	* Block IDs this class of mob can destroy
	*/
	public int[] canDestroy = new int[0];
	
	/**
	* Dig to the current target entity
	*/
	protected void digToEntity(Entity digToEntity) {
		digToEntity(digToEntity, canDestroy);
	}
	
	/**
	* Dig to the current target entity
	*/
	protected void digToEntity(Entity digToEntity, int[] canDestroy) {
		if(canDestroy.length > 0) {
			// Radians
			float rads = (float) (rotationYaw * Math.PI)/180F;
			
			// The angle theta is relative to the X axis, around the origin
			double inFrontX = posX - 1 * Math.sin(rads);
			double inFrontZ = posZ + 1 * Math.cos(rads);
			
			int i = MathHelper.floor_double(inFrontX);
			int j = MathHelper.floor_double(boundingBox.minY);
			int k = MathHelper.floor_double(inFrontZ);
			
			// ID of block in front of face
			int idTop = worldObj.getBlockId(i, j + 1, k);
			int idBot = worldObj.getBlockId(i, j, k);
			//System.out.println("Adding: " + (double) Math.sin(rads) + " to X: " + posZ + ": " + idTop);
			//System.out.println("Adding: " + (double) Math.cos(rads) + " to Z: " + posX + ": " + idTop);
			
			// Remove the top block
			if(idTop != 0) {
				boolean delete = false;
				for(int allowBlock : canDestroy) {
					if(idTop == allowBlock) {
						delete = true;
					}
				}
				
				// Random chance to destroy
				if(delete) {
					digThroughBlock(idTop, i, j + 1, k);
					//System.out.println("I AM DIGGING AT YOUUUUU");
				} else {
					//System.out.println("Number of destroys: " + canDestroy.length + " for class "
					//	+ this.getClass().getName());
				}
			} else {
				//System.out.println("At (" + i + "," + j + "," + k + "): " + idTop + "^" + idBot);
				//System.out.println("I AMM RUNNING AT YOUUU");
			}
		} 
	}
	
	/**
	* The action an entity does to dig through a block
	*
	* @param	id	ID of block to dig
	*/
	protected void digThroughBlock(int id, int i, int j, int k) {
		Block block = Block.blocksList[id];
		if((block != null) && (rand.nextInt(5) == 1)) {
			int drop = block.quantityDropped(rand);
			// Unset the top block and drop as an item
			if(drop > 0) {
				int metadata = worldObj.getBlockMetadata(i, j, k);
				int dropID = block.idDropped(metadata, rand);
				// Avoid rendering null pointer errors
				if((dropID > 0) && (dropID < Item.itemsList.length) 
						&& (Item.itemsList[dropID] != null)) {
					ItemStack is = new ItemStack(dropID, drop, block.damageDropped(metadata));
					EntityItem entityitem = new EntityItem(worldObj, i, j, k, is);
					entityitem.delayBeforeCanPickup = 10;
					worldObj.entityJoinedWorld(entityitem);
				}
			}
			// Play breaking sound
			boolean flag = worldObj.setBlockWithNotify(i, j, k, 0);
			if(flag) {
				Minecraft mc = ModLoader.getMinecraftInstance();
				// Huge ugly method call
				mc.sndManager.playSound(block.stepSound.func_1146_a(), (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, (block.stepSound.func_1147_b() + 1.0F) / 2.0F, block.stepSound.func_1144_c() * 0.8F);
				//block.onBlockDestroyedByPlayer(world, i, j, k, i1);
			}
		}
	}
	
	//===================
	// END DIGGING MOBS
	//===================
    protected void attackEntity(Entity entity, float f)
    {
    }

    protected float getBlockPathWeight(int i, int j, int k)
    {
        return 0.0F;
    }

    protected Entity findPlayerToAttack()
    {
        return null;
    }

    public boolean getCanSpawnHere()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);
        return super.getCanSpawnHere() && getBlockPathWeight(i, j, k) >= 0.0F;
    }

    public boolean hasPath()
    {
        return pathToEntity != null;
    }

    public void setPathToEntity(PathEntity pathentity)
    {
        pathToEntity = pathentity;
    }

    public Entity getTarget()
    {
        return playerToAttack;
    }

    public void setTarget(Entity entity)
    {
        playerToAttack = entity;
    }

    private PathEntity pathToEntity;
    protected Entity playerToAttack;
    protected boolean hasAttacked;
}
