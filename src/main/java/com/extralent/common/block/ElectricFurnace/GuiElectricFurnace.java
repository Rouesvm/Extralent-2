package com.extralent.common.block.ElectricFurnace;

import com.extralent.common.config.ElectricFurnaceConfig;
import com.extralent.common.misc.ModMisc;
import com.extralent.common.tile.TileElectricFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class GuiElectricFurnace extends GuiContainer {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

    public static final int arrowWidth = 26 + 1;

    private static final ResourceLocation TEXTURES = new ResourceLocation(ModMisc.MODID, "textures/gui/electric_furnace_gui.png");
    private final TileElectricFurnace furnace;

    public GuiElectricFurnace(TileElectricFurnace tileEntity, ContainerElectricFurnace container) {
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
        int arrowX = 71;
        int arrowY = 25;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (progress < arrowWidth) {
            drawTexturedModalRect(guiLeft + arrowX, guiTop + arrowY, 1, 153, progress + 1, 17);
        } else {
            drawTexturedModalRect(guiLeft + arrowX, guiTop + arrowY, 1, 153, 0, 17);
        }
    }

    private void drawEnergyBar(int energy) {
        //drawRect(guiLeft + 9, guiTop + 5, guiLeft + 20, guiTop + 63, 0xffffffff);
        int percentage = energy * 57 / ElectricFurnaceConfig.MAX_POWER;
        for (int i = 0 ; i < percentage ; i++) {
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

    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY){
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }
}
