package com.krei.createlinkedactivator;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerMenu;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.MenuEntry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(LinkedActivator.MODID)
public class LinkedActivator {
    public static final String MODID = "createlinkedactivator";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Registrate REGISTRATE = Registrate.create(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> LINKED_ACTIVATOR_ITEM = ITEMS.registerItem("linked_activator", LinkedActivatorItem::new);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final Supplier<DataComponentType<ItemContainerContents>> LINKED_ACTIVATOR_ITEMS_DC = DATA_COMPONENTS.registerComponentType(
        "linked_activator_items", builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final MenuEntry<LinkedActivatorMenu> LINKED_ACTIVATOR_MENU =
        REGISTRATE.menu("linked_activator", LinkedActivatorMenu::new, () -> LinkedActivatorScreen::new).register();


    // AllMenuTypes

    public LinkedActivator(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        modEventBus.addListener(LinkedActivator::addToCreativeTabs);
        modEventBus.addListener(LinkedActivator::registerPackets);
        NeoForge.EVENT_BUS.register(LAClientHandler.class);
        NeoForge.EVENT_BUS.register(LAServerHandler.class);
    }

    @SubscribeEvent
    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == AllCreativeModeTabs.BASE_CREATIVE_TAB.get()) {
            event.accept(LINKED_ACTIVATOR_ITEM.get());
        }
    }

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(LAInputPacket.TYPE, LAInputPacket.STREAM_CODEC, new LAServerHandler());
    }
}
