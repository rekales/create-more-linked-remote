package com.krei.createlinkedactivator;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.MenuEntry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(LinkedActivator.MODID)
public class LinkedActivator {
    public static final String MODID = "createlinkedactivator";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Registrate REGISTRATE = Registrate.create(MODID);
    
    public static final ItemEntry<LinkedActivatorItem> ITEM = REGISTRATE
            .item("linked_activator", LinkedActivatorItem::new)
            .properties(p -> p.stacksTo(1))
            .tab(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
            // .model(AssetLookup.itemModelWithPartials())
            .register();

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final Supplier<DataComponentType<ItemContainerContents>> ITEM_DATA_COMPONENT = DATA_COMPONENTS
            .registerComponentType(
                    "linked_activator_items", builder -> builder.persistent(ItemContainerContents.CODEC)
                            .networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final MenuEntry<LinkedActivatorMenu> MENU = REGISTRATE
            .menu("linked_activator", LinkedActivatorMenu::new, () -> LinkedActivatorScreen::new).register();

    public LinkedActivator(IEventBus modEventBus, ModContainer modContainer) {
        DATA_COMPONENTS.register(modEventBus);
        modEventBus.addListener(LinkedActivator::registerPackets);
        NeoForge.EVENT_BUS.register(LAClientHandler.class);
        NeoForge.EVENT_BUS.register(LAServerHandler.class);
    }

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(LAInputPacket.TYPE, LAInputPacket.STREAM_CODEC, new LAServerHandler());
    }
}
