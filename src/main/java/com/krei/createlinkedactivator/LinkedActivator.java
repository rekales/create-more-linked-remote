package com.krei.createlinkedactivator;

import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
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

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> ITEM = ITEMS.registerItem("linked_activator", LinkedActivatorItem::new);

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final Supplier<DataComponentType<ItemContainerContents>> ITEM_DATA_COMPONENT = DATA_COMPONENTS
            .registerComponentType(
                    "linked_activator_frequency", builder -> builder.persistent(ItemContainerContents.CODEC)
                            .networkSynchronized(ItemContainerContents.STREAM_CODEC));


    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    public static final Supplier<MenuType<LinkedActivatorMenu>> MENU = MENUS.register(
            "linked_activator_menu", () -> IMenuTypeExtension.create(LinkedActivatorMenu::new));

    public LinkedActivator(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        MENUS.register(modEventBus);
        modEventBus.addListener(LinkedActivator::registerPackets);
        modEventBus.addListener(LinkedActivator::addToCreativeTabs);
        modEventBus.addListener(LinkedActivator::registerScreens);
        modEventBus.addListener(LAClientHandler::clientSetup);
        NeoForge.EVENT_BUS.register(LAClientHandler.class);
        NeoForge.EVENT_BUS.register(LAServerHandler.class);
    }

    private static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(LAInputPacket.TYPE, LAInputPacket.STREAM_CODEC, new LAServerHandler());
    }

    private static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == AllCreativeModeTabs.BASE_CREATIVE_TAB.get()) {
            event.accept(ITEM.get());
        }
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MENU.get(), LinkedActivatorScreen::new);
    }
}
