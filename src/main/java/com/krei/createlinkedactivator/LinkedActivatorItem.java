package com.krei.createlinkedactivator;

import static com.krei.createlinkedactivator.LinkedActivator.*;

import java.util.LinkedList;

import javax.annotation.Nullable;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerMenu;
import com.simibubi.create.foundation.item.ItemHelper;

import net.createmod.catnip.data.Couple;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

@SuppressWarnings({"unused", "null"}) // Remove later
public class LinkedActivatorItem extends Item implements MenuProvider {
    public LinkedActivatorItem(Properties properties) {
        super(properties.component(LINKED_ACTIVATOR_ITEMS_DC.get(), ItemContainerContents.EMPTY));
    }

    public static Couple<Frequency> getNetworkKey(ItemStack stack) {
        if (!stack.has(LINKED_ACTIVATOR_ITEMS_DC))
			return Couple.create(Frequency.EMPTY, Frequency.EMPTY);

        ItemStackHandler newInv = getFrequencyItems(stack);
		return Couple.create(Frequency.of(newInv.getStackInSlot(0)),
			Frequency.of(newInv.getStackInSlot(1)));
    }

    public static ItemStackHandler getFrequencyItems(ItemStack stack) {
        if (LINKED_ACTIVATOR_ITEM.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot get frequency items from non-activator: " + stack);
        ItemStackHandler newInv = new ItemStackHandler(2);
		ItemHelper.fillItemStackHandler(stack.getOrDefault(LINKED_ACTIVATOR_ITEMS_DC, ItemContainerContents.EMPTY), newInv);
        return newInv;
    }

    public static void setFrequencyItems(ItemStack stack, ItemStackHandler inv) {
        if (LINKED_ACTIVATOR_ITEM.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot set frequency items from non-activator: " + stack);
        stack.set(LINKED_ACTIVATOR_ITEMS_DC, ItemHelper.containerContentsFromHandler(inv));
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		ItemStack heldItem = player.getMainHandItem();
		return LinkedActivatorMenu.create(id, inv, heldItem);
    }

    @Override
    public Component getDisplayName() {
		return getDescription();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack heldItem = player.getItemInHand(usedHand);
       	if (player.isShiftKeyDown() && usedHand == InteractionHand.MAIN_HAND) {
			if (!level.isClientSide && player instanceof ServerPlayer && player.mayBuild())
				player.openMenu(this, buf -> {
					ItemStack.STREAM_CODEC.encode(buf, heldItem);
				});
			return InteractionResultHolder.success(heldItem);
		}
        return super.use(level, player, usedHand);
    }

}