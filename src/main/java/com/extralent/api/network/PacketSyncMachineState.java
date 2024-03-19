package com.extralent.api.network;

import com.extralent.Extralent;
import com.extralent.api.tools.Interfaces.IMachineStateContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncMachineState implements IMessage {

    private int energy;
    private int progress;

    public PacketSyncMachineState(int energy, int progress) {
        this.energy = energy;
        this.progress = progress;
    }

    public PacketSyncMachineState() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        energy = buf.readInt();
        progress = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(energy);
        buf.writeByte(progress);
    }

    public static class Handler implements IMessageHandler<PacketSyncMachineState, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncMachineState message, MessageContext ctx) {
            Extralent.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSyncMachineState message, MessageContext ctx) {
            EntityPlayer player = Extralent.proxy.getClientPlayer();
            if (player.openContainer instanceof IMachineStateContainer) {
                ((IMachineStateContainer) player.openContainer).sync(message.energy, message.progress);
            }
        }
    }
}
