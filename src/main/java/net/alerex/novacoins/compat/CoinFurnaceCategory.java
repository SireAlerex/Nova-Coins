package net.alerex.novacoins.compat;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.alerex.novacoins.NovaCoins;
import net.alerex.novacoins.block.ModBlocks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class CoinFurnaceCategory implements DisplayCategory<CoinFurnaceDisplay> {
	public static final Identifier TEXTURE = new Identifier(NovaCoins.MOD_ID, "textures/gui/coin_furnace_rei.png");
	public static final CategoryIdentifier<CoinFurnaceDisplay> COIN_FURNACE = CategoryIdentifier.of(NovaCoins.MOD_ID, "coin_furnace");

	@Override
	public CategoryIdentifier<? extends CoinFurnaceDisplay> getCategoryIdentifier() {
		return COIN_FURNACE;
	}

	@Override
	public Text getTitle() {
		return Text.translatable("block.novacoins.coin_furnace");
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(ModBlocks.COIN_FURNACE);
	}

	@Override
	public List<Widget> setupDisplay(CoinFurnaceDisplay display, Rectangle bounds) {
		final Point startPoint = new Point(bounds.getCenterX() -(175/2), bounds.getCenterY() -(24));
		List<Widget> widgets = new LinkedList<>();
		widgets.add(Widgets.createRecipeBase(bounds));

		widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 176, 49)));

		widgets.add(Widgets.createBurningFire(new Point(startPoint.x	 + 56, startPoint.y + 30)).animationDurationMS(10000));

		widgets.add(Widgets.createArrow(new Point(startPoint.x +79, startPoint.y +19)).animationDurationMS(5000));

		widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5),
				Text.translatable("label.novacoins.coin_furnace_rei"))
					.noShadow()
					.rightAligned()
					.color(0xFF404040, 0xFFBBBBBB));

		widgets.add(Widgets.createSlot(new Point(startPoint.x +56, startPoint.y +11))
				.entries(display.getOutputEntries().get(0))
				.markInput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x +116, startPoint.y +19))
				.entries(display.getOutputEntries().get(1))
				.markOutput()
				.disableBackground());

		return widgets;
	}

	@Override
	public int getDisplayHeight() {
		return 49;
	}
}
