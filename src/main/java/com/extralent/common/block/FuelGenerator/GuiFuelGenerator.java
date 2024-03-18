package com.extralent.common.block.FuelGenerator;

import akka.io.SelectionHandlerSettings;
import com.extralent.common.config.FuseMachineConfig;
import com.extralent.common.misc.ModMisc;
import com.extralent.common.block.BlockTileEntities.TileFuelGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class GuiFuelGenerator extends GuiContainer {

    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

    public int progressHeight = 13;
    public int arrowLength = progressHeight + 1;

    private static final ResourceLocation TEXTURES = new ResourceLocation(ModMisc.MODID, "textures/gui/fuel_generator_gui.png");
    private final TileFuelGenerator furnace;

    public GuiFuelGenerator(TileFuelGenerator tileEntity, ContainerFuelGenerator container) {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        furnace = tileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURES);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int energy = furnace.getClientEnergy();
        int progress = furnace.getClientProgress();
        drawEnergyBar(energy);
        drawProgressArrow(progress);
    }

    private void drawProgressArrow(int progress) {
        int arrowX = 41;
        int arrowY = 18;

        int yOffset = (progressHeight * progress) / 10;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (yOffset > 0) {
            drawTexturedModalRect(guiLeft + arrowX, guiTop + arrowY + yOffset, 181, 1 + yOffset, 13, progressHeight - yOffset);
        } else {
            drawTexturedModalRect(guiLeft + arrowX, guiTop + arrowY, 181, 1, 13, 0);
        }
    }

    private void drawEnergyBar(int energy) {
        int percentage = energy * 57 / FuseMachineConfig.MAX_POWER;
        for (int i = 0; i < percentage; i++) {
            int color = i % 2 == 0 ? 0xffee1c00 : 0xffbd1600;
            drawHorizontalLine(guiLeft + 10, guiLeft + 18, (guiTop + 63 - 1) - i, color);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        if (isInRect(guiLeft + 9, guiTop + 5, 11, 59, mouseX, mouseY)) {
            drawHoveringText(Collections.singletonList("Energy: " + furnace.getClientEnergy()), mouseX, mouseY, fontRenderer);
        }
    }

    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }
}
