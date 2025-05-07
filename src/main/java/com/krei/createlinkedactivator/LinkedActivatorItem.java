package com.krei.createlinkedactivator;

import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerServerHandler;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import com.mojang.datafixers.types.templates.List;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@SuppressWarnings("unused") // Remove later
public class LinkedActivatorItem extends BowItem {
    public LinkedActivatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            LinkedList<Couple<Frequency>> collect = new LinkedList<>();
            collect.add(getFrequencies(getDefaultInstance()));
            LinkedControllerServerHandler.receivePressed(level, player.blockPosition(), player.getUUID(), collect, true);
        } else {

        }
            
        return super.use(level, player, usedHand);
    }

    public static Couple<RedstoneLinkNetworkHandler.Frequency> getFrequencies(ItemStack activator) {
        ItemStack temp_first_freq = new ItemStack(Items.STONE);
        ItemStack temp_second_freq = new ItemStack(Items.STONE);

        return Couple.create(Frequency.of(temp_first_freq), Frequency.of(temp_second_freq));
    }


}