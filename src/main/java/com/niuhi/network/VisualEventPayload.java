package com.niuhi.network;

import com.niuhi.ElytraRevampedReReWritten;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record VisualEventPayload(int typeId, double x, double y, double z, boolean hasColor, int[] colors)
        implements CustomPacketPayload {
    public static final Type<VisualEventPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(ElytraRevampedReReWritten.MOD_ID, "visual_event"));

    public static final StreamCodec<FriendlyByteBuf, VisualEventPayload> CODEC = StreamCodec.ofMember(
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
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
