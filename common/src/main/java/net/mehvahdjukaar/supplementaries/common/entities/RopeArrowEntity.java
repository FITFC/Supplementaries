package net.mehvahdjukaar.supplementaries.common.entities;

import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.supplementaries.common.block.ModBlockProperties;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RopeBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RopeKnotBlock;
import net.mehvahdjukaar.supplementaries.common.utils.ItemsUtil;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.mehvahdjukaar.supplementaries.reg.ModEntities;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class RopeArrowEntity extends AbstractArrow {
    private int charges = 0;
    private BlockPos prevPlacedPos = null;

    public RopeArrowEntity(EntityType<? extends RopeArrowEntity> type, Level world) {
        super(type, world);
    }

    public RopeArrowEntity(Level worldIn, LivingEntity throwerIn, int charges) {
        super(ModEntities.ROPE_ARROW.get(), throwerIn, worldIn);
        this.charges = charges;
    }

    public RopeArrowEntity(Level worldIn, double x, double y, double z, int charges) {
        super(ModEntities.ROPE_ARROW.get(), x, y, z, worldIn);
        this.charges = charges;
    }

    public RopeArrowEntity(Level worldIn, double x, double y, double z) {
        super(ModEntities.ROPE_ARROW.get(), x, y, z, worldIn);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return PlatformHelper.getEntitySpawnPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Ropes", this.charges);
        if (this.prevPlacedPos != null) {
            compound.put("PrevPlacedPos", NbtUtils.writeBlockPos(this.prevPlacedPos));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.charges = compound.getInt("Ropes");
        if (compound.contains("PrevPlacedPos")) {
            this.prevPlacedPos = NbtUtils.readBlockPos(compound.getCompound("PrevPlacedPos"));
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        if (this.charges != 0) {
            ItemStack stack = new ItemStack(ModRegistry.ROPE_ARROW_ITEM.get());
            stack.setDamageValue(stack.getMaxDamage() - this.charges);
            return stack;
        }
        return new ItemStack(Items.ARROW);
    }

    //TODO: add sound
    //on block hit.
    @Override
    protected void onHitBlock(BlockHitResult rayTraceResult) {
        super.onHitBlock(rayTraceResult);

        Block ropeBlock = CommonConfigs.getSelectedRope();
        if(ropeBlock==null)return;

        if (this.charges <= 0) return;
        if (!this.level.isClientSide) {
            this.prevPlacedPos = null;
            Entity entity = this.getOwner();
            Player player = null;
            if (!(entity instanceof Mob) || PlatformHelper.isMobGriefingOn(this.level, this)) {
                if (entity instanceof Player pl && pl.mayBuild()) {
                    //TODO: i might just give null here since player isn't actually placing these blocks
                    player = pl;
                }
                //Ugly but works
                //try finding existing ropes
                BlockPos hitPos = rayTraceResult.getBlockPos();
                BlockState hitState = this.level.getBlockState(hitPos);
                Block hitBlock = hitState.getBlock();

                //knot blocks
                if (ropeBlock == ModRegistry.ROPE.get()) {
                    ModBlockProperties.PostType knotType = ModBlockProperties.PostType.get(hitState);
                    if (knotType != null) {
                        BlockState knotState = RopeKnotBlock.convertToRopeKnot(knotType, hitState, this.level, hitPos);
                        if (knotState != null) {
                            if (knotState.getValue(RopeKnotBlock.AXIS).isVertical()) {
                                this.prevPlacedPos = hitPos.relative(rayTraceResult.getDirection()).above();
                            } else {
                                this.prevPlacedPos = hitPos;
                            }
                            this.removeCharge();
                            return;
                        }
                    }
                }


                //try adding rope down
                if (hitBlock == ropeBlock && RopeBlock.addRope(hitPos, level, player, InteractionHand.MAIN_HAND, ropeBlock)) {
                    this.prevPlacedPos = hitPos;
                    this.removeCharge();
                    return;
                }
                hitPos = hitPos.relative(rayTraceResult.getDirection());
                hitBlock = this.level.getBlockState(hitPos).getBlock();
                //rope to the side
                if (hitBlock == ropeBlock && RopeBlock.addRope(hitPos, level, player, InteractionHand.MAIN_HAND, ropeBlock)) {
                    this.prevPlacedPos = hitPos;
                    this.removeCharge();
                    return;
                }

                //try placing it normally
                ItemStack ropes = new ItemStack(ropeBlock);
                BlockPlaceContext context = new BlockPlaceContext(this.level, player, InteractionHand.MAIN_HAND, ropes, rayTraceResult);
                if (context.canPlace()) {
                    BlockState state = ItemsUtil.getPlacementState(context, ropeBlock);
                    if (state != null) {
                        this.level.setBlock(context.getClickedPos(), state, 11);
                        this.prevPlacedPos = context.getClickedPos();
                        this.removeCharge();
                    }
                }
            }
        }
    }

    private void removeCharge() {
        this.charges = Math.max(0, this.charges - 1);
        this.level.playSound(null, this.prevPlacedPos, SoundEvents.LEASH_KNOT_PLACE, SoundSource.BLOCKS, 0.2f, 1.7f);
    }

    private void continueUnwindingRope() {
        Block ropeBlock = CommonConfigs.getSelectedRope();
        if(ropeBlock==null)return;
        //no need to do other checks since this only happens after a onBlockCollision()
        Player player = null;
        Entity entity = this.getOwner();
        if (entity instanceof Player player1 && player1.mayBuild()) {
            player = player1;
        }
        BlockPos hitPos = this.prevPlacedPos;
        //Block hitBlock = this.level.getBlockState(hitPos).getBlock();
        //try adding rope down
        //hitBlock == ropeBlock &&
        if (RopeBlock.addRope(hitPos.below(), level, player, InteractionHand.MAIN_HAND, ropeBlock)) {
            this.prevPlacedPos = hitPos.below();
            this.removeCharge();
        } else {
            this.prevPlacedPos = null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.charges != 0 && this.prevPlacedPos != null) {
                this.continueUnwindingRope();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && entity.getType() != EntityType.ENDERMAN) {
            entity.setSecondsOnFire(5);
        }
        entity.setRemainingFireTicks(k);

        this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
        this.setYRot(this.getYRot() + 180.0F);
        this.yRotO += 180.0F;
        if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == Pickup.ALLOWED) {
                this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.discard();
        }
    }

    @Override
    public void playerTouch(Player entityIn) {
        if (!this.level.isClientSide) {
            if (entityIn.getInventory().add(this.getPickupItem())) {
                entityIn.take(this, 1);
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }
}
