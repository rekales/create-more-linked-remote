package com.kreidev.cmlinkedremote.compat.jei;

import com.kreidev.cmlinkedremote.LinkedRemoteScreen;
import com.simibubi.create.compat.jei.GhostIngredientHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.kreidev.cmlinkedremote.LinkedRemote.resLoc;

@SuppressWarnings("unused")
@JeiPlugin
public class LinkedRemoteJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = resLoc("cmlr_jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(LinkedRemoteScreen.class, new GhostIngredientHandler());
    }
}
