package net.mehvahdjukaar.supplementaries.common.utils.fabric;

import net.mehvahdjukaar.supplementaries.common.block.tiles.KeyLockableTile;
import net.mehvahdjukaar.supplementaries.common.items.SackItem;
import net.mehvahdjukaar.supplementaries.integration.CompatHandler;
import net.mehvahdjukaar.supplementaries.integration.CuriosCompat;
import net.mehvahdjukaar.supplementaries.integration.QuarkCompat;
import net.mehvahdjukaar.supplementaries.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ItemsUtilImpl {
    public static boolean extractFromContainerItemIntoSlot(Player player, ItemStack containerStack, Slot slot) {
        return false;
    }

    public static boolean addToContainerItem(Player player, ItemStack containerStack, ItemStack stack, Slot slot, boolean simulate, boolean inSlot) {
        return false;
    }

    public static int getAllSacksInInventory(ItemStack stack, ServerPlayer player, int amount) {
        var inventory = player.getInventory();

        for (int idx = 0; idx < inventory.getContainerSize(); idx++) {
            ItemStack slotItem = inventory.getItem(idx);
            if (slotItem.getItem() instanceof SackItem) {
                CompoundTag tag = slotItem.getTag();
                if (tag != null && tag.contains("BlockEntityTag")) {
                    amount++;
                }
            }
        }
        if (CompatHandler.QUARK) {
            ItemStack backpack = player.getItemBySlot(EquipmentSlot.CHEST);
            amount += QuarkCompat.getSacksInBackpack(backpack);
        }
        return amount;
    }

    public static KeyLockableTile.KeyStatus hasKeyInInventory(Player player, String key) {
        if (key == null) return KeyLockableTile.KeyStatus.CORRECT_KEY;
        KeyLockableTile.KeyStatus found = KeyLockableTile.KeyStatus.NO_KEY;
        if (CompatHandler.CURIOS) {
            found = CuriosCompat.isKeyInCurio(player, key);
            if (found == KeyLockableTile.KeyStatus.CORRECT_KEY) return found;
        }
        var inventory = player.getInventory();
        for (int idx = 0; idx < inventory.getContainerSize(); idx++) {
            ItemStack stack = inventory.getItem(idx);
            if (stack.is(ModTags.KEY)) {
                found = KeyLockableTile.KeyStatus.INCORRECT_KEY;
                if (KeyLockableTile.isCorrectKey(stack, key)) return KeyLockableTile.KeyStatus.CORRECT_KEY;
            }
        }

        return found;
    }

    public static boolean faucetSpillItems(Level level, BlockPos pos, Direction dir, BlockEntity tile) {
        if (tile instanceof Container container) {
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack itemstack = container.getItem(slot);
                if (!itemstack.isEmpty()) {
                    ItemStack extracted = container.removeItem(slot, 1);
                    //empty stack means it can't extract from inventory
                    if (!extracted.isEmpty()) {
                        tile.setChanged();
                        ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, extracted);
                        drop.setDeltaMovement(new Vec3(0, 0, 0));
                        level.addFreshEntity(drop);
                        float f = (level.random.nextFloat() - 0.5f) / 4f;
                        level.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.3F, 0.5f + f);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
