package com.krei.cmlinkedremote;

import java.util.function.Supplier;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.createmod.catnip.lang.FontHelper;
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

@Mod(LinkedRemote.MODID)
public class LinkedRemote {
    public static final String MODID = "cmlinkedremote";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).defaultCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey());

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public static final ItemEntry<LinkedRemoteItem> ITEM = REGISTRATE
            .item("linked_remote", LinkedRemoteItem::new)
            .register();
    public static final MenuEntry<LinkedRemoteMenu> MENU = REGISTRATE
            .menu("linked_remote", (MenuBuilder.ForgeMenuFactory<LinkedRemoteMenu>) LinkedRemoteMenu::new, () -> LinkedRemoteScreen::new)
            .register();

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final Supplier<DataComponentType<ItemContainerContents>> ITEM_DATA_COMPONENT = DATA_COMPONENTS
            .registerComponentType(
                    "linked_remote_frequency", builder -> builder.persistent(ItemContainerContents.CODEC)
                            .networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public LinkedRemote(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        modEventBus.addListener(LinkedRemote::registerPackets);
        NeoForge.EVENT_BUS.register(LRClientHandler.class);
        NeoForge.EVENT_BUS.register(LRServerHandler.class);
    }

    private static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(LRInputPacket.TYPE, LRInputPacket.STREAM_CODEC, new LRServerHandler());
    }
}
