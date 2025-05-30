package com.krei.cmlinkedremote;

import javax.annotation.Nullable;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.item.ItemHelper;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.createmod.catnip.data.Couple;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.Consumer;

@SuppressWarnings({"null" }) // Remove later
public class LinkedRemoteItem extends Item implements MenuProvider {
    public LinkedRemoteItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .component(LinkedRemote.ITEM_DATA_COMPONENT.get(), ItemContainerContents.EMPTY));
    }

    public static Couple<Frequency> getNetworkKey(ItemStack stack) {
        if (!stack.has(LinkedRemote.ITEM_DATA_COMPONENT))
            return Couple.create(Frequency.EMPTY, Frequency.EMPTY);

        ItemStackHandler newInv = getFrequencyItems(stack);
        return Couple.create(Frequency.of(newInv.getStackInSlot(0)),
                Frequency.of(newInv.getStackInSlot(1)));
    }

    public static ItemStackHandler getFrequencyItems(ItemStack stack) {
        if (!stack.is(LinkedRemote.ITEM.get()))
            throw new IllegalArgumentException("Cannot get frequency items from non-remote: " + stack);
        ItemStackHandler newInv = new ItemStackHandler(2);
        ItemHelper.fillItemStackHandler(stack.getOrDefault(LinkedRemote.ITEM_DATA_COMPONENT, ItemContainerContents.EMPTY), newInv);
        return newInv;
    }

    public static void setFrequencyItems(ItemStack stack, ItemStackHandler inv) {
        if (!stack.is(LinkedRemote.ITEM.get()))
            throw new IllegalArgumentException("Cannot set frequency items from non-remote: " + stack);
        stack.set(LinkedRemote.ITEM_DATA_COMPONENT, ItemHelper.containerContentsFromHandler(inv));
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return LinkedRemoteMenu.create(id, inv, heldItem);
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
                player.openMenu(this, buf -> {ItemStack.STREAM_CODEC.encode(buf, heldItem);});
            return InteractionResultHolder.success(heldItem);
        }
        return super.use(level, player, usedHand);
        // Activation at LAClientHandler & LAServerHandler
    }

    @SuppressWarnings("removal")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new LinkedRemoteItemRenderer()));
    }
}