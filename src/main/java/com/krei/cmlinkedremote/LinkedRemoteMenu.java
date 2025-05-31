package com.krei.cmlinkedremote;

import com.simibubi.create.foundation.gui.menu.GhostItemMenu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LinkedRemoteMenu extends GhostItemMenu<ItemStack> {

	public LinkedRemoteMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public LinkedRemoteMenu(int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
		this(LinkedRemote.MENU.get(), id, inv, extraData);
	}

	public LinkedRemoteMenu(MenuType<?> type, int id, Inventory inv, ItemStack filterItem) {
		super(type, id, inv, filterItem);
	}

	public static LinkedRemoteMenu create(int id, Inventory inv, ItemStack filterItem) {
		return new LinkedRemoteMenu(LinkedRemote.MENU.get(), id, inv, filterItem);
	}

	@Override
	protected ItemStack createOnClient(RegistryFriendlyByteBuf extraData) {
		return ItemStack.STREAM_CODEC.decode(extraData);
	}

	@Override
	protected ItemStackHandler createGhostInventory() {
		return LinkedRemoteItem.getFrequencyItems(contentHolder);
	}

	@Override
	protected void addSlots() {
		addPlayerSlots(-30, 123);
		addSlot(new SlotItemHandler(ghostInventory, 0, 38, 26));
		addSlot(new SlotItemHandler(ghostInventory, 1, 38, 44));
	}

	@Override
	protected void saveData(ItemStack contentHolder) {
		LinkedRemoteItem.setFrequencyItems(contentHolder, ghostInventory);
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
