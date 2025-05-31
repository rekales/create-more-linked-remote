package com.krei.cmlinkedremote;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class LinkedRemoteScreen extends AbstractSimiContainerScreen<LinkedRemoteMenu>{

	protected GuiTexture background;
	private List<Rect2i> extraAreas = Collections.emptyList();

	private IconButton resetButton;
	private IconButton confirmButton;

    public LinkedRemoteScreen(LinkedRemoteMenu container, Inventory inv, Component title) {
        super(container, inv, title);
		this.background = new GuiTexture(LinkedRemote.MODID, "linked_remote_menu", 101, 101);
    }

	@Override
	protected void init() {
		setWindowSize(background.getWidth(), background.getHeight() + 4 + PLAYER_INVENTORY.getHeight());
		setWindowOffset(1, 0);
		super.init();

		int x = leftPos;
		int y = topPos;

		resetButton = new IconButton(x + background.getWidth() - 62, y + background.getHeight() - 24, AllIcons.I_TRASH);
		resetButton.withCallback(() -> {
			menu.clearContents();
			menu.sendClearPacket();
		});
		confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
			minecraft.player.closeContainer();
		});

		addRenderableWidget(resetButton);
		addRenderableWidget(confirmButton);

		extraAreas = ImmutableList.of(new Rect2i(x + background.getWidth() + 4, y + background.getHeight() - 44, 64, 56));
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
		int invY = topPos + background.getHeight() + 4;
		renderPlayerInventory(graphics, invX, invY);

		int x = leftPos;
		int y = topPos;

		background.render(graphics, x, y);
		graphics.drawString(font, title, x + 8, y + 4, 0x592424, false);

		GuiGameElement.of(menu.contentHolder).<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth() - 2, y + background.getHeight() - 60, -200)
			.scale(5)
			.render(graphics);
	}

	@Override
	protected void containerTick() {
		if (!ItemStack.matches(menu.player.getMainHandItem(), menu.contentHolder))
			menu.player.closeContainer();
		super.containerTick();
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}


	private static class GuiTexture implements ScreenElement, TextureSheetSegment {
		private final ResourceLocation location;
		private final int width;
		private final int height;
		
		GuiTexture(String namespace, String location, int width, int height) {
			this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
			this.width = width;
			this.height = height;
		}

		@Override
		public @NotNull ResourceLocation getLocation() {
			return location;
		}

		@Override
		public int getStartX() {
			return 0;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public int getStartY() {
			return 0;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void render(GuiGraphics graphics, int x, int y) {
			graphics.blit(location, x, y, 0, 0, width, height);
		}

		@OnlyIn(Dist.CLIENT)
		public void render(GuiGraphics graphics, int x, int y, Color c) {
			bind();
			UIRenderHelper.drawColoredTexture(graphics, c, x, y, 0, 0, width, height);
		}
	}
}
