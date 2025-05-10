package com.krei.createlinkedactivator;

import static com.krei.createlinkedactivator.LinkedActivator.*;

import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerServerHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.mojang.datafixers.types.templates.List;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.createmod.catnip.data.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import oshi.software.os.unix.freebsd.FreeBsdFileSystem;

@SuppressWarnings("unused") // Remove later
// @SuppressWarnings("null")
public class LinkedActivatorItem extends Item {
    public LinkedActivatorItem(Properties properties) {
        super(properties
        .stacksTo(1)
        .component(LINKED_ACTIVATOR_ITEMS_DC.get(), ItemContainerContents.EMPTY)
        );
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Item itemFreq = context.getLevel().getBlockState(context.getClickedPos()).getBlock().asItem();
        setNetworkKey(context.getItemInHand(), Couple.create(Frequency.of(itemFreq.getDefaultInstance()), 
                Frequency.of(itemFreq.getDefaultInstance())));
        return InteractionResult.SUCCESS;
    }

    public static Couple<Frequency> getNetworkKey(ItemStack stack) {
        if (LINKED_ACTIVATOR_ITEM.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot set frequency items from non-activator: " + stack);
        if (!stack.has(LINKED_ACTIVATOR_ITEMS_DC))
			return Couple.create(Frequency.EMPTY, Frequency.EMPTY);

        ItemStackHandler newInv = new ItemStackHandler(2);
		ItemHelper.fillItemStackHandler(stack.getOrDefault(LINKED_ACTIVATOR_ITEMS_DC, ItemContainerContents.EMPTY), newInv);
		return Couple.create(Frequency.of(newInv.getStackInSlot(0)),
			Frequency.of(newInv.getStackInSlot(1)));
    }

    public static void setNetworkKey(ItemStack stack, Couple<Frequency> netkey) {
        if (LINKED_ACTIVATOR_ITEM.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot set frequency items from non-activator: " + stack);

        ItemStackHandler newInv = new ItemStackHandler(2);
        newInv.setStackInSlot(0, netkey.getFirst().getStack());
        newInv.setStackInSlot(1, netkey.getSecond().getStack());
        stack.set(LINKED_ACTIVATOR_ITEMS_DC, ItemHelper.containerContentsFromHandler(newInv));
    }
}