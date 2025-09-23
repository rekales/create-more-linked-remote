package com.krei.cmlinkedremote.mixin;

import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RedstoneLinkBlockEntity.class, remap = false)
public interface RedstoneLinkBlockEntityAccessor {

    @Accessor
    LinkBehaviour getLink();

}