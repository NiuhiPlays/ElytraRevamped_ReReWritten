package com.niuhi.network;

import com.niuhi.ElytraRevampedReReWritten;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VisualEventPayload(int typeId, double x, double y, double z, boolean hasColor, int color)
        implements CustomPayload {
    public static final Id<VisualEventPayload> ID =
            new Id<>(Identifier.of(ElytraRevampedReReWritten.MOD_ID, "visual_event"));

    public static final PacketCodec<PacketByteBuf, VisualEventPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeVarInt(value.typeId());
                buf.writeDouble(value.x());
                buf.writeDouble(value.y());
                buf.writeDouble(value.z());
                buf.writeBoolean(value.hasColor());
                if (value.hasColor()) {
                    buf.writeInt(value.color());
                }
            },
            buf -> {
                int typeId = buf.readVarInt();
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                boolean hasColor = buf.readBoolean();
                int color = hasColor ? buf.readInt() : 0;
                return new VisualEventPayload(typeId, x, y, z, hasColor, color);
            }
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
