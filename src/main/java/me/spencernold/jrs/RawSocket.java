package me.spencernold.jrs;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class RawSocket implements AutoCloseable {

    private final long handle;

    public RawSocket(String device) throws IOException {
        this.handle = PacketCaptureBinding.open(device);
    }

    public RawSocket() throws IOException {
        this(PacketCaptureBinding.getDefaultDevice());
    }

    public void send(byte[] bytes, int offset, int length) {
        PacketCaptureBinding.send(handle, bytes, offset, length);
    }

    public void send(byte[] bytes) {
        send(bytes, 0, bytes.length);
    }

    public void listen(BiConsumer<RawSocket, byte[]> consumer) {
        final RawSocket socket = this;
        PacketCaptureBinding.listen(handle, (handle1, bytes, length) -> {
            if (bytes.length != length)
                bytes = Arrays.copyOfRange(bytes, 0, length);
            consumer.accept(socket, bytes);
        });
    }

    @Override
    public void close() throws Exception {
        PacketCaptureBinding.close(handle);
    }
}
