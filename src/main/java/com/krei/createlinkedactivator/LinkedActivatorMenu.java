package com.krei.createlinkedactivator;

import static com.krei.createlinkedactivator.LinkedActivator.*;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LinkedActivatorMenu extends GhostItemMenu<ItemStack> {

    public LinkedActivatorMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public LinkedActivatorMenu(MenuType<?> type, int id, Inventory inv, ItemStack filterItem) {
		super(type, id, inv, filterItem);
	}

	public static LinkedActivatorMenu create(int id, Inventory inv, ItemStack filterItem) {
		return new LinkedActivatorMenu(LINKED_ACTIVATOR_MENU.get(), id, inv, filterItem);
	}

	@Override
	protected ItemStack createOnClient(RegistryFriendlyByteBuf extraData) {
		return ItemStack.STREAM_CODEC.decode(extraData);
	}

	@Override
	protected ItemStackHandler createGhostInventory() {
		return LinkedActivatorItem.getFrequencyItems(contentHolder);
	}

	@Override
	protected void addSlots() {
		addPlayerSlots(8, 131);
        addSlot(new SlotItemHandler(ghostInventory, 0, 12, 34));
        addSlot(new SlotItemHandler(ghostInventory, 1, 12, 52));
	}

	@Override
	protected void saveData(ItemStack contentHolder) {
		LinkedActivatorItem.setFrequencyItems(contentHolder, ghostInventory);
	}

	@Override
	protected boolean allowRepeats() {
		return true;
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (slotId == playerInventory.selected && clickTypeIn != ClickType.THROW)
			return;
		super.clicked(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return playerInventory.getSelected() == contentHolder;
	}

    
}
