package net.mehvahdjukaar.supplementaries.client.tooltip;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.supplementaries.common.items.tooltip_components.BannerPatternTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;

public class BannerPatternTooltipComponent implements ClientTooltipComponent {

    private static final int SIZE = 80;
    private final BannerPatternTooltip tooltip;

    public BannerPatternTooltipComponent(BannerPatternTooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public int getHeight() {
        return SIZE + 2;
    }

    @Override
    public int getWidth(Font pFont) {
        return SIZE;
    }

    @Override
    public void renderImage(Font pFont, int x, int y, PoseStack poseStack, ItemRenderer pItemRenderer, int pBlitOffset) {
        poseStack.pushPose();

        var mat = Registry.BANNER_PATTERN.getTag(tooltip.pattern())
                .map(ImmutableList::copyOf).flatMap(l -> l.get(0).unwrapKey()).map(Sheets::getBannerMaterial);
        if (mat.isPresent()) {
            var sprite = mat.get().sprite();
            RenderSystem.enableBlend();
            RenderUtil.blitSprite(poseStack, x, y, SIZE, SIZE, (16f) / sprite.getWidth(), (16f / sprite.getHeight()) * 12, (int) (20f / 64 * sprite.getWidth()), (int) (20f / 64 * sprite.getHeight()), sprite);
        }

        poseStack.popPose();
    }
}