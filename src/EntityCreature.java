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
			//=======================
			// START MORE DEADLY MOBS
			//=======================
			// Unecessary, handled by more deadly mobs
            /*if(d3 > 0.0D)
            {
                isJumping = true;
            }*/
			//=======================
			// END MORE DEADLY MOBS
			//=======================
        }
		//=======================
		// START MORE DEADLY MOBS
		//=======================
        if(playerToAttack != null)  {
            faceEntity(playerToAttack, 30F, 30F);
			if(!worldObj.multiplayerWorld) {
				digToEntity(playerToAttack);
			}
        } else {
			// Reset to all previous values as this is a new block to dig
			mod_DiggingMobs.setRenderer();
			mod_DiggingMobs.renderer.removeDamagedBlock(lastBlockX, lastBlockY, lastBlockZ);
			curBlockDamage = 0.0F;
			soundTimer = 0;
		}
		// When the mob isn't chasing a player
        if(isCollidedHorizontally && !hasPath())  {
            isJumping = true;
        }
		// Entity is in water.
		if(rand.nextFloat() < 0.8F && (flag1 || flag2))
        {
            isJumping = true;
        }
		//=======================
		// END MORE DEADLY MOBS
		//=======================
    }
	
	//===================
	// START MORE DEADLY MOBS
	//===================
	/**
	* Block IDs this class of mob can destroy
	*/
	public int[] canDestroy = new int[0];
	
	// Last coordinates dug
	private int lastBlockX = 0;
	private int lastBlockY = 0;
	private int lastBlockZ = 0;
	/**
	* A sound timer
	*/
	private int soundTimer = 0;
	/**
	* A 5 tick delay for digging
	*/
	private int digDelay 	= 5;
	/**
	* Current block damage for a block
	*/
	private float curBlockDamage = 0.0F;
	
	/**
	* Dig to the current target entity
	*/
	protected void digToEntity(Entity digToEntity) {
		digToEntity(digToEntity, canDestroy);
	}
	
	/**
	* Determine how to dig to the entity given.
	* This algorithm will pick the quickest direct
	* route to the entity with no work-around routes
	*/
	protected void digToEntity(Entity digToEntity, int[] canDestroy) {
		// Radians for the X/Z plane
		float radsXZ = (float) (rotationYaw * Math.PI)/180F;
		// Radians for the X/Y plane
		float radsXY = (float) (rotationPitch * Math.PI)/180F;
		
		// The angle theta is relative to the X axis, around the origin
		double inFrontX = posX - 1 * Math.sin(radsXZ);
		double inFrontZ = posZ + 1 * Math.cos(radsXZ);
		double inFrontY = posY - 1 * Math.sin(radsXY);
		
		// Position of most optimal block to dig
		int i = MathHelper.floor_double(inFrontX);
		int j = (int) Math.round(inFrontY);
		int k = MathHelper.floor_double(inFrontZ);
		// Current Positions
		int curI = MathHelper.floor_double(posX);
		int curJ = MathHelper.floor_double(posY);
		int curK = MathHelper.floor_double(posZ);
		
		//System.out.println("Adding: " + (double) Math.sin(rads) + " to X: " + posZ + ": " + idTop);
		//System.out.println("Adding: " + (double) Math.cos(rads) + " to Z: " + posX + ": " + idTop);
		//System.out.println("Adding: " + (double) Math.sin(radsXY) + " to Y: " + posY);
		
		// ID of block at the mob's feet in front of them
		int beloID = worldObj.getBlockId(i, curJ, k);
		// Dig up, diagonal up and in front
		if(j > curJ) {
			// X = To Dig, O = Non dug blocks, | = mob / = Player
			if(attemptDig(curI, curJ + 2, curK)) {
				// X X /
			} else if(attemptDig(i, curJ + 2, k)) {
				// | X /
			} else if(attemptDig(i, curJ + 1, k)) {
				// | O O
			} else if(isOpaqueCube(beloID)) {
				// Make the zombie jump onto the ledge
				isJumping = true;
			}
		// Dig down, diagonal down and in front
		} else if(j < curJ) {
			// X = To Dig, O = Non dug blocks, | = mob / = Player
			if(attemptDig(i, curJ, k)) { 		
				// | O O
			} else if(attemptDig(i, curJ - 1, k)) {
				// | X /
			} else if(attemptDig(curI, curJ - 1, curK)) {
				// X X /
			}
			
		// Dig in front
		} else {
			int fronID = worldObj.getBlockId(i, curJ + 2, k);
			int abovID = worldObj.getBlockId(i, curJ + 1, k);
			// Ensure that if there is a place to jump to, the
			// zombie will try that first
			if((abovID != 0) || (fronID != 0)) {
				// X = To Dig, O = Non dug blocks, | = mob / = Player
				if(attemptDig(i, curJ + 1, k)) {
					// | X /
				} else if(attemptDig(i, curJ, k)) {
					// | X /
				}
			} else if(isOpaqueCube(beloID)) {
				// Make the zombie jump onto the ledge
				isJumping = true;
			} else {
				// Reset to all previous values as this is a new block to dig
				mod_DiggingMobs.setRenderer();
				mod_DiggingMobs.renderer.removeDamagedBlock(lastBlockX, lastBlockY, lastBlockZ);
				curBlockDamage = 0.0F;
				soundTimer = 0;
			}
		}
	}
	
	/**
	* Check if a block is an opaque cube
	*
	* @param	id		ID of block
	*/
	private boolean isOpaqueCube(int id) {
		if((id <= 0) || (id > Block.blocksList.length) || (Block.blocksList[id] == null)) {
			return false;
		} else {
			return Block.blocksList[id].isOpaqueCube();
		}
	}
	
	/**
	* Destroy a given block
	* 
	* @return	True if this block exists and was attempted to dig
	*/
	protected boolean attemptDig(int i, int j, int k) {
		int id = worldObj.getBlockId(i, j, k);
		// Remove the top block
		if(id != 0) {
			boolean delete = false;
			for(int allowBlock : canDestroy) {
				if(id == allowBlock) {
					delete = true;
				}
			}
			
			// Random chance to destroy
			if(delete) {
				if(digThroughBlock(id, i, j, k)) {
					//System.out.println("I AM DIGGING AT YOUUUUU");
				} else {
					//System.out.println("I FAILED TO DIG AT YOUUU");
				}
			} else {
				//System.out.println("Number of destroys: " + canDestroy.length + " for class "
				//	+ this.getClass().getName());
			}
			return true;
		} else {
			//System.out.println("At (" + i + "," + j + "," + k + "): " + id);
			//System.out.println("I AMM RUNNING AT YOUUU");
			return false;
		}
		
	}
	
	/**
	* The action an entity does to dig through a block
	*
	* @param	id		ID of block being destroyed
	* @return	Success of digging
	*/
	protected boolean digThroughBlock(int id, int i, int j, int k) {
		boolean flag = false;
		int metadata = worldObj.getBlockMetadata(i, j, k);
		Block block = Block.blocksList[id];
		if((block != null) && (!partialDig(block, i, j, k))) {
			// Play breaking sound
			flag = worldObj.setBlockWithNotify(i, j, k, 0);
			if(flag) {
				Minecraft mc = ModLoader.getMinecraftInstance();
				// Huge ugly method call to play the block removed sound
				mc.sndManager.playSound(block.stepSound.func_1146_a(), (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, (block.stepSound.func_1147_b() + 1.0F) / 2.0F, block.stepSound.func_1144_c() * 0.8F);
				//block.onBlockDestroyedByPlayer(world, i, j, k, i1);
				
				int drop = block.quantityDropped(rand);
				// Unset the top block and drop as an item
				if(drop > 0) {
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
			}
		}
		
		return flag;
	}
	
	/**
	* Check if a dig is a partial dig or not
	*
	* @param	block	Block attempting to dig
	*/
	protected boolean partialDig(Block block, int i, int j, int k) {
		Minecraft mc = ModLoader.getMinecraftInstance();
		// Add damage effects
		mc.effectRenderer.addBlockHitEffects(i, j, k, getSideHit(i, j, k));
		// Whether we have fully dug the block or not
		boolean dug = false;
		
		// Check if we should delay the dig
        if(digDelay > 0) {
            digDelay--;
		// Check if we're still digging the previous block
        } else if((i == lastBlockX) && (j == lastBlockY) && (k == lastBlockZ))  {
			curBlockDamage += getBlockDamage(block);
			// Play the sound
			if(soundTimer % 4 == 0) {
				mc.sndManager.playSound(block.stepSound.func_1145_d(), 
										(float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, 
										(block.stepSound.func_1147_b() + 1.0F) / 8F, 
										block.stepSound.func_1144_c() * 0.5F);
			}
			
			soundTimer++;
			// Set that we have removed the block and reset data values
			if(curBlockDamage >= 1.0F) {
				dug = true;
				curBlockDamage = 0.0F;
				soundTimer = 0;
				digDelay = 5;
				mod_DiggingMobs.renderer.removeDamagedBlock(i, j, k);
			} else {
				mod_DiggingMobs.setRenderer();
				mod_DiggingMobs.renderer.addDamagedBlock(this, block, curBlockDamage, i, j, k);
			}
        } else {
			// Reset to all previous values as this is a new block to dig
			mod_DiggingMobs.setRenderer();
			mod_DiggingMobs.renderer.removeDamagedBlock(lastBlockX, lastBlockY, lastBlockZ);
            curBlockDamage = 0.0F;
            soundTimer = 0;
            lastBlockX = i;
            lastBlockY = j;
            lastBlockZ = k;
		}
		
		return !dug;
	}
	
	/**
	* Get the side the mob hit
	* Side numbers are based off of those
	* in EffectRenderer
	*
	* @return	An int depicting the side
	*/
	private int getSideHit(int i, int j, int k) {
		int curI = MathHelper.floor_double(posX);
		int curJ = MathHelper.floor_double(posY);
		int curK = MathHelper.floor_double(posZ);
		
		if(j < curJ) {
			return 0;
		} else if(j > curJ) {
			return 1;
		} else if(k < curK) {
			return 2;
		} else if(k > curK) {
			return 3;
		} else if(i < curI) {
			return 4;
		} else {
			return 5;
		}
	}
	
	/**
	* Get the block damage of this mob
	* against a given block
	*
	* @param	block	Block being attacked
	* @return	Damage counter
	*/
	public float getBlockDamage(Block block) {
		float blockHardness = block.blockHardness;
		float f = (1F  / blockHardness) / 100F;
		// A little bit of a hack to avoid editing
		// entity mob
		if(this instanceof EntityMob) {
			f = (((EntityMob) this).attackStrength / blockHardness) / 100F;
			if(inWater) {
				f /= 5F;
			}
			if(!onGround) {
				f /= 5F;
			}
		}
		
		return f;
	}
	
	//===================
	// END MORE DEADLY MOBS
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
