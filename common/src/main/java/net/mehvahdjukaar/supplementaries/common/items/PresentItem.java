package net.mehvahdjukaar.supplementaries.common.items;

import net.mehvahdjukaar.moonlight.api.block.IColored;
import net.mehvahdjukaar.supplementaries.common.block.blocks.AbstractPresentBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.PresentBlock;
import net.mehvahdjukaar.supplementaries.common.block.tiles.PresentBlockTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PresentItem extends BlockItem implements IColored {

    public PresentItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, components, tooltipFlag);
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            CompoundTag t = tag.getCompound("BlockEntityTag");
            if (!t.isEmpty()) {
                boolean isPacked = false;
                if (t.contains("Sender")) {
                    var c = PresentBlockTile.getSenderMessage(t.getString("Sender"));
                    if (c != null) components.add(c);
                    isPacked = true;
                }
                if (t.contains("Recipient")) {
                    var c = PresentBlockTile.getRecipientMessage(t.getString("Recipient"));
                    if (c != null) components.add(c);
                    isPacked = true;
                }
                if (!isPacked && t.contains("Items")) {
                    components.add(Component.translatable("message.supplementaries.present.public"));
                }
            }
        }
    }

    @Override
    public DyeColor getColor() {
        return ((AbstractPresentBlock) this.getBlock()).getColor();
    }

    @Nullable
    @Override
    public Item changeItemColor(@Nullable DyeColor color) {
        return ((AbstractPresentBlock) this.getBlock()).changeItemColor(color);
    }

    @Override
    public boolean supportsBlankColor() {
        return true;
    }
}
