package net.alerex.novacoins.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CoinFurnaceScreen extends HandledScreen<CoinFurnaceScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("novacoins", "textures/gui/coin_furnace.png");

	public CoinFurnaceScreen(CoinFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

		renderProgressArrow(context, x, y);
		renderFlame(context, x, y);
	}

	private void renderProgressArrow(DrawContext context, int x, int y) {
		if (handler.isCrafting()) {
			context.drawTexture(TEXTURE, x+79, y+35, 176, 14, handler.getScaledProgress(), 16);
		}
	}

	private void renderFlame(DrawContext context, int x, int y) {
		if (handler.isBurning()) {
			context.drawTexture(TEXTURE, x+57, y+37 + (13-handler.getScaledBurning()), 176, 13-handler.getScaledBurning(), 13, handler.getScaledBurning());
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}
}
