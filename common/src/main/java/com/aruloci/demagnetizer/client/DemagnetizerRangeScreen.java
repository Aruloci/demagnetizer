package com.aruloci.demagnetizer.client;

import com.aruloci.demagnetizer.block.DemagnetizerBlock;
import com.aruloci.demagnetizer.block.DemagnetizerBlockEntity;
import com.aruloci.demagnetizer.network.SetRangePayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import static com.aruloci.demagnetizer.block.DemagnetizerBlockEntity.MAX_RANGE;
import static com.aruloci.demagnetizer.block.DemagnetizerBlockEntity.MIN_RANGE;

@Environment(EnvType.CLIENT)
public class DemagnetizerRangeScreen extends Screen {
    private static final int WIDGET_WIDTH = 200;
    private static final int WIDGET_HEIGHT = 20;
    private static final int ROW_SPACING = 24;
    private static final int POWERED_TEXT_COLOR = 0xFF5555;

    private final DemagnetizerBlockEntity demagnetizer;
    private int range;
    private int lastSentRange;

    public DemagnetizerRangeScreen(DemagnetizerBlockEntity demagnetizer) {
        super(Component.translatable("screen.demagnetizer.title"));
        this.demagnetizer = demagnetizer;
        this.range = demagnetizer.getRange();
        this.lastSentRange = range;
    }

    @Override
    protected void init() {
        int x = (width - WIDGET_WIDTH) / 2;
        int y = height / 2 - 30;
        addRenderableWidget(new RangeSlider(x, y));
        addRenderableWidget(Button.builder(showRangeLabel(), button -> {
            ZoneCubeRenderer.toggle(demagnetizer.getLevel(), demagnetizer.getBlockPos());
            button.setMessage(showRangeLabel());
        }).bounds(x, y + ROW_SPACING, WIDGET_WIDTH, WIDGET_HEIGHT).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(x, y + 2 * ROW_SPACING, WIDGET_WIDTH, WIDGET_HEIGHT).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, height / 2 - 50, 0xFFFFFF);
        if (!DemagnetizerBlock.isActive(demagnetizer.getBlockState())) {
            guiGraphics.drawCenteredString(font, Component.translatable("screen.demagnetizer.powered"),
                    width / 2, height / 2 - 30 + 3 * ROW_SPACING + 4, POWERED_TEXT_COLOR);
        }
    }

    @Override
    public void onClose() {
        sendIfChanged();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private Component showRangeLabel() {
        boolean shown = ZoneCubeRenderer.isShown(demagnetizer.getLevel(), demagnetizer.getBlockPos());
        return Component.translatable(shown ? "screen.demagnetizer.show_range.on" : "screen.demagnetizer.show_range.off");
    }

    private void sendIfChanged() {
        if (range != lastSentRange) {
            lastSentRange = range;
            ClientHooks.sendToServer(new SetRangePayload(demagnetizer.getBlockPos(), range));
        }
    }

    private class RangeSlider extends AbstractSliderButton {
        RangeSlider(int x, int y) {
            super(x, y, WIDGET_WIDTH, WIDGET_HEIGHT, Component.empty(), (double) (range - MIN_RANGE) / (MAX_RANGE - MIN_RANGE));
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("screen.demagnetizer.range", range));
        }

        @Override
        protected void applyValue() {
            range = Mth.clamp((int) Math.round(MIN_RANGE + value * (MAX_RANGE - MIN_RANGE)), MIN_RANGE, MAX_RANGE);
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);
            sendIfChanged();
        }
    }
}
