package dev.obscuria.elixirum.mixin;

import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class MixinClientboundCustomPayloadPacket {

    //? if >=1.21 {
    /*// Payload bounds belong to stream codecs in 1.21, not the packet constructor.
    *///? } else {
    @ModifyConstant(method = {"<init>*"}, constant = @Constant(intValue = 1048576), require = 0)
    private int xlPackets(int maxPacketSize) {
        return Integer.MAX_VALUE;
    }
    //? }
}

