package net.alerex.novacoins.compat;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.alerex.novacoins.NovaCoins;
import net.alerex.novacoins.block.ModBlocks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class CoinFurnaceCategory implements DisplayCategory<CoinFurnaceDisplay> {
	public static final Identifier TEXTURE = new Identifier(NovaCoins.MOD_ID, "textures/gui/coin_furnace.png");
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
		final Point startPoint = new Point(bounds.getCenterX() -87, bounds.getCenterY() -35);
		List<Widget> widgets = new LinkedList<>();
		widgets.add(Widgets.createRecipeBase(bounds));

		widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 175, 82)));

		widgets.add(Widgets.createSlot(new Point(startPoint.x +80, startPoint.y +11))
				.entries(display.getInputEntries().get(0))
				.markInput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x +80, startPoint.y +59))
				.entries(display.getOutputEntries().get(0))
				.markOutput());

		return widgets;
	}

//	@Override
//	public int getDisplayHeight() {
//		return 90;
//	}
}
