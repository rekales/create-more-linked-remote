package com.krei.createlinkedactivator;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(LinkedActivator.MODID)
public class LinkedActivator {
    public static final String MODID = "createlinkedactivator";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> LINKED_ACTIVATOR_ITEM = ITEMS.registerItem("linked_activator", LinkedActivatorItem::new);

    public LinkedActivator(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
    }
}
