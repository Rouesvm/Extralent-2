package com.extralent.common.block.ElectricFurnace;

import com.extralent.common.misc.ModMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class GuiElectricFurnace extends GuiContainer {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

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
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int energy = furnace.getClientEnergy();
        drawEnergyBar(energy);

        int progress = furnace.getClientProgress();
        if (progress > 0) {
            int arrowWidth = 26;
            int percentage = arrowWidth - progress * arrowWidth / TileElectricFurnace.MAX_PROGRESS;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(guiLeft + 78, guiTop + 25, 1, 153, percentage + 1, 17);
        }
    }

    private void drawEnergyBar(int energy) {
        drawRect(guiLeft + 184, guiTop + 12, guiLeft + 207, guiTop + 116, 0xff5f5f5f);
        int percentage = energy * 100 / TileElectricFurnace.MAX_POWER;
        for (int i = 0 ; i < percentage ; i++) {
            int color = i % 2 == 0 ? 0xffee1c00 : 0xff590a00;
            drawHorizontalLine(guiLeft + 186, guiLeft + 204, guiTop + 13 + 1 + i, color);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        if (isInRect(guiLeft + 184, guiTop + 12, 23, 104, mouseX, mouseY)) {
            drawHoveringText(Collections.singletonList("Energy: " + furnace.getClientEnergy()), mouseX, mouseY, fontRenderer);
        }
    }

    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY){
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }
}
