package net.mehvahdjukaar.supplementaries.common.items;

import net.mehvahdjukaar.moonlight.api.block.IColored;
import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.supplementaries.client.renderers.items.FlagItemRenderer;
import net.mehvahdjukaar.supplementaries.common.block.blocks.FlagBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class FlagItem extends WoodBasedBlockItem implements IColored, ICustomItemRendererProvider {

    public FlagItem(Block block, Properties properties) {
        super(block, properties, 300);
    }

    @Override
    public DyeColor getColor() {
        return ((FlagBlock) this.getBlock()).getColor();
    }

    @Nullable
    @Override
    public Item changeItemColor(@Nullable DyeColor color) {
        return ((FlagBlock) this.getBlock()).changeItemColor(color);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
    }

    @Override
    public Supplier<ItemStackRenderer> getRendererFactory() {
        return FlagItemRenderer::new;
    }
}
