package com.niuhi.network;

import com.niuhi.ElytraRevampedReReWritten;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VisualEventPayload(int typeId, double x, double y, double z, boolean hasColor, int[] colors)
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
                    buf.writeVarInt(value.colors().length);
                    for (int color : value.colors()) {
                        buf.writeInt(color);
                    }
                }
            },
            buf -> {
                int typeId = buf.readVarInt();
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                boolean hasColor = buf.readBoolean();
                int[] colors = new int[0];
                if (hasColor) {
                    int count = buf.readVarInt();
                    if (count > 0) {
                        colors = new int[count];
                        for (int i = 0; i < count; i++) {
                            colors[i] = buf.readInt();
                        }
                    }
                }
                return new VisualEventPayload(typeId, x, y, z, hasColor, colors);
            }
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
